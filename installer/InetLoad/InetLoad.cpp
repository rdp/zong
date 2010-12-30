/*******************************************************
* FILE NAME: InetLoad.cpp
*
* Copyright 2004 - Present NSIS
*
* PURPOSE:
*    ftp/http file download plug-in
*    on the base of MS Inet API
*    todo: status write mutex (?)
*          4 GB limit (http support?)
*
* CHANGE HISTORY
*
* Author      Date  Modifications
* Takhir Bedertdinov
*     Nov 11, 2004  Original
*     Dec 17, 2004  Embedded edition -
*              NSISdl GUI style as default
*              (nsisdl.cpp code was partly used)
*     Dec 17, 2004  MSI Banner style
*     Feb 20, 2005  Resume download
*              feature for big files and bad connections
*     Mar 05, 2005  Proxy authentication
*              and /POPUP caption prefix option
*     Mar 25, 2005  Connect timeout option
*              and FTP switched to passive mode
*     Apr 18, 2005  Crack URL buffer size 
*              bug fixed (256->string_size)
*              HTTP POST added
*     Jun 06, 2005  IDOK on "Enter" key locked
*              POST HTTP header added 
*     Jun 22, 2005  non-interaptable mode /nocancel
*              and direct connect /noproxy
*     Jun 29, 2005  post.php written and tested
*     Jul 05, 2005  60 sec delay on WinInet detach problem
*              solved (not fine, but works including
*              installer exit and system reboot) 
*     Jul 08, 2005  'set foreground' finally removed
*     Jul 26, 2005  POPUP translate option
*     Aug 23, 2005  https service type in InternetConnect
*              and "ignore certificate" flags
*     Sep 30, 2005  https with bad certificate from old OS;
*              Forbidden handling
*     Mar 12, 2006  http auth dialog and 401 status code
*     Oct 29, 2006  VS 8 FtpCommand name conflict patch
*     Nov 04, 2006  switch to popup in silent mode for 
*              better stability
*     Jan 27, 2007  String buffers increased.
*     Jun 11, 2007 FTP download permitted even if server rejects
*              SIZE request (stupied ProFTPD). 
*     Jan 09, 2008 {_trueparuex^}' fix - InternetSetFilePointer()
*              returns -1 on error.
*******************************************************/


#define _WIN32_WINNT 0x0500

#include <windows.h>
#include <windowsx.h>
#include <wininet.h>
#include <commctrl.h>
#include <stdio.h>
#include <fcntl.h>
#include <time.h>
#include <io.h>
#include <sys/stat.h>
#include "..\exdll\exdll.h"
#include "resource.h"

// IE 4 safety
typedef BOOL (__stdcall *FTP_CMD)(HINTERNET,BOOL,DWORD,LPCSTR,DWORD,HINTERNET *);
FTP_CMD myFtpCommand = NULL;

#define PLUGIN_NAME "InetLoad plug-in"
#define PB_RANGE 400
#define PAUSE1_SEC 2 // transfer error indication time, for reget only
#define PAUSE2_SEC 3 // paused state time, increase this if need (60?)
#define PAUSE3_SEC 1 // pause after resume button pressed
#define NOT_AVAILABLE 0xffffffff
#define POST_HEADER "Content-Type: application/x-www-form-urlencoded"
#define INTERNAL_OK 0xFFEE

enum STATUS_CODES {
   ST_OK = 0,
   ST_CONNECTING,
   ST_DOWNLOAD,
   ST_CANCELLED,
   ST_URLOPEN,
   ST_PAUSE,
   ERR_TERMINATED,
   ERR_DIALOG,
   ERR_INETOPEN,
   ERR_URLOPEN,
   ERR_TRANSFER,
   ERR_FILEOPEN,
   ERR_FILEWRITE,
   ERR_REGET,
   ERR_CONNECT,
   ERR_OPENREQUEST,
   ERR_SENDREQUEST,
   ERR_CRACKURL,
   ERR_NOTFOUND,
   ERR_THREAD,
   ERR_PROXY,
   ERR_FORBIDDEN,
   ERR_REQUEST,
   ERR_SERVER,
   ERR_AUTH
};


static char szStatus[][128] = {
   "OK", "Connecting", "Downloading", "Cancelled", "Connecting", //"Opening URL",
   "Reconnect Pause", "Terminated", "Dialog Error", "Open Internet Error",
   "Open URL Error", "Transfer Error", "File Open Error", "File Write Error",
   "Reget Error", "Connection Error", "OpenRequest Error", "SendRequest Error",
   "URL Parts Error", "File Not Found (404)", "CreateThread Error", "Proxy Error (407)",
   "Access Forbidden (403)", "Request Error", "Server Error", "Unauthorized (401)"};

HINSTANCE g_hInstance;
char fn[MAX_PATH] = "",
     *url = NULL,
     *post = NULL,
     *szProxy = NULL,
     szBanner[256] = "",
     szCaption[128] = "",
     szUsername[128] = "",
     szPassword[128] = "",
     szResume[256] = "Your internet connection seems to have dropped out!\nPlease reconnect and click Retry to resume downloading...";

int status = ST_CONNECTING;
DWORD cnt = 0,
      fs = 0,
      timeout = 0;
DWORD startTime, transfTime, openType = INTERNET_OPEN_TYPE_PRECONFIG;
bool silent = false, popup = false, resume = false, nocancel = false, noproxy = false;

HWND        g_hwndProgressBar = NULL;
HWND        g_hwndStatic = NULL;
static void *lpWndProcOld = NULL;
UINT uMsgCreate;
HWND childwnd;
HWND hwndL;
HWND hwndB;
HWND hDlg;


/*****************************************************
 * FUNCTION NAME: sf(HWND)
 * PURPOSE: 
 *    moves HWND to top and activates it
 * SPECIAL CONSIDERATIONS:
 *    commented because annoying
 *****************************************************/
/*
void sf(HWND hw)
{
   DWORD ctid = GetCurrentThreadId();
   DWORD ftid = GetWindowThreadProcessId(GetForegroundWindow(), NULL);
   AttachThreadInput(ftid, ctid, TRUE);
   SetForegroundWindow(hw);
   AttachThreadInput(ftid, ctid, FALSE);
}
*/

/*****************************************************
 * FUNCTION NAME: ParentWndProc(HWND)
 * PURPOSE: 
 *    this is how we can catch Cancel on the INSTFILES page
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
static LRESULT CALLBACK ParentWndProc(HWND hwnd,
                                      UINT message,
                                      WPARAM wParam,
                                      LPARAM lParam)
{
  if (uMsgCreate && message == uMsgCreate)
  {
    static HWND hwndPrevFocus;
    static BOOL fCancelDisabled;
    HWND hwndS = GetDlgItem(childwnd, 1006);

    if (wParam)
    {
      hwndL = GetDlgItem(childwnd, 1016);
      hwndB = GetDlgItem(childwnd, 1027);
      HWND hwndP = GetDlgItem(childwnd, 1004);

      if (childwnd && hwndP && hwndS)
      {
// Where to restore focus to before we disable the cancel button
        hwndPrevFocus = GetFocus();
        if (!hwndPrevFocus)
          hwndPrevFocus = hwndP;

        if (IsWindowVisible(hwndL))
          ShowWindow(hwndL, SW_HIDE);
        else
          hwndL = NULL;
        if (IsWindowVisible(hwndB))
          ShowWindow(hwndB, SW_HIDE);
        else
          hwndB = NULL;

        RECT wndRect, ctlRect;

        GetClientRect(childwnd, &wndRect);

        GetWindowRect(hwndS, &ctlRect);

        HWND s = g_hwndStatic = CreateWindow(
          "STATIC",
          "",
          WS_CHILD | WS_CLIPSIBLINGS | SS_CENTER,
          0,
          wndRect.bottom / 2 - (ctlRect.bottom - ctlRect.top) / 2,
          wndRect.right,
          ctlRect.bottom - ctlRect.top,
          childwnd,
          NULL,
          g_hInstance,
          NULL
        );

        DWORD dwStyle = WS_CHILD | WS_CLIPSIBLINGS;
        dwStyle |= GetWindowLong(hwndP, GWL_STYLE) & PBS_SMOOTH;

        GetWindowRect(hwndP, &ctlRect);

        HWND pb = g_hwndProgressBar = CreateWindow(
          "msctls_progress32",
          "",
          dwStyle,
          0,
          wndRect.bottom / 2 + (ctlRect.bottom - ctlRect.top) / 2,
          wndRect.right,
          ctlRect.bottom - ctlRect.top,
          childwnd,
          NULL,
          g_hInstance,
          NULL
        );

        long c;
        c = SendMessage(hwndP, PBM_SETBARCOLOR, 0, 0);
        SendMessage(hwndP, PBM_SETBARCOLOR, 0, c);
        SendMessage(pb, PBM_SETBARCOLOR, 0, c);
        c = SendMessage(hwndP, PBM_SETBKCOLOR, 0, 0);
        SendMessage(hwndP, PBM_SETBKCOLOR, 0, c);
        SendMessage(pb, PBM_SETBKCOLOR, 0, c);

        SendMessage(pb, PBM_SETRANGE, 0, MAKELPARAM(0, PB_RANGE));

        // set font
        long hFont = SendMessage((HWND) lParam, WM_GETFONT, 0, 0);
        SendMessage(pb, WM_SETFONT, hFont, 0);
        SendMessage(s, WM_SETFONT, hFont, 0);
        ShowWindow(pb, SW_SHOWNA);
        ShowWindow(s, SW_SHOWNA);

        fCancelDisabled = EnableWindow(GetDlgItem(hwnd, IDCANCEL), TRUE);
        SendMessage(hwnd, WM_NEXTDLGCTL, (WPARAM)GetDlgItem(hwnd, IDCANCEL), TRUE);
      }
      else
        childwnd = NULL;
    }
    else if (childwnd)
    {
      if (hwndB)
      {
        ShowWindow(hwndB, SW_SHOWNA);
        hwndB = NULL;
      }
      if (hwndL)
      {
        ShowWindow(hwndL, SW_SHOWNA);
        hwndL = NULL;
      }

// Prevent wierd stuff happening if the cancel button happens to be
// pressed at the moment we are finishing and restore the previous focus
// and cancel button states
      SendMessage(hwnd, WM_NEXTDLGCTL, (WPARAM)hwndPrevFocus, TRUE);
      SendMessage(GetDlgItem(hwnd, IDCANCEL), BM_SETSTATE, FALSE, 0);
      if (fCancelDisabled)
        EnableWindow(GetDlgItem(hwnd, IDCANCEL), FALSE);
      if (g_hwndStatic)
      {
        DestroyWindow(g_hwndStatic);
        g_hwndStatic = NULL;
      }
      if (g_hwndProgressBar)
      {
        DestroyWindow(g_hwndProgressBar);
        g_hwndProgressBar = NULL;
      }
      childwnd = NULL;
    }
  }
  else if (message == WM_COMMAND && LOWORD(wParam) == IDCANCEL)
  {
    PostMessage(hDlg, WM_COMMAND, IDCANCEL, 0);
  }
  else
  {
    return CallWindowProc(
      (WNDPROC) lpWndProcOld,
      hwnd,
      message,
      wParam,
      lParam
    );
  }
  return 0;
}

static char szUrl[256] = "";
static char szDownloading[128] = "Downloading %s";
static char szConnecting[64] = "Connecting ...";
static char szSecond[64] = "second";
static char szMinute[32] = "minute";
static char szHour[32] = "hour";
static char szPlural[32] = "s";
static char szProgress[128] = "%dkB (%d%%) of %dkB @ %d.%01dkB/s";
static char szRemaining[64] = " (%d %s%s remaining)";

/*****************************************************
 * FUNCTION NAME: progress_callback
 * PURPOSE: 
 *    old-style progress bar text updates
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
void progress_callback(void)
{
   char buf[1024] = "", b[1024] = "";
   int time_sofar = time(NULL) - transfTime;
   int bps = cnt / (time_sofar ? time_sofar : 1);
   int remain = (fs > 0 && fs != NOT_AVAILABLE) ? (MulDiv(time_sofar, fs, cnt) - time_sofar) : 0;
   char *rtext=szSecond;
   if(remain < 0) remain = 0;
   if (remain >= 60)
   {
                  remain/=60;
                  rtext=szMinute;
                  if (remain >= 60)
                  {
                    remain/=60;
                    rtext=szHour;
                  }
   }
   wsprintf(buf,
            szProgress,
            cnt/1024,
            fs > 0 && fs != NOT_AVAILABLE ? MulDiv(100, cnt, fs) : 0,
            fs != NOT_AVAILABLE ? fs/1024 : 0,
            bps/1024,((bps*10)/1024)%10
            );
   if (remain) wsprintf(buf+lstrlen(buf),
                        szRemaining,
                        remain,
                        rtext,
                        remain==1?"":szPlural
                        );
   SetWindowText(g_hwndStatic, (cnt == 0 || status == ST_CONNECTING) ? szConnecting : buf);
   SendMessage(g_hwndProgressBar, PBM_SETPOS, fs > 0 && fs != NOT_AVAILABLE ?
      MulDiv(cnt, PB_RANGE, fs) : 0, 0);
   wsprintf(buf,
            szDownloading,
            strchr(fn, '\\') ? strrchr(fn, '\\') + 1 : fn
            );
   HWND hwndS = GetDlgItem(childwnd, 1006);
   GetWindowText(hwndS, b, sizeof(b));
   if(lstrcmp(b, buf) != 0)
   {
      SetWindowText(hwndS, buf);
      RedrawWindow(childwnd, NULL, NULL, RDW_INVALIDATE|RDW_ERASE);
   }
}

/*****************************************************
 * FUNCTION NAME: attachNsis freeNsis
 * PURPOSE: 
 *    attaches to and detaches from installer window
 *    messages handling procedure
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
bool attachNsis(HWND parent)
{
   uMsgCreate = RegisterWindowMessage("inetload create");
   lpWndProcOld = (void *)SetWindowLong(parent,GWL_WNDPROC,(long)ParentWndProc);
   SendMessage(parent, uMsgCreate, TRUE, (LPARAM) parent);
   SetWindowText(g_hwndStatic, szConnecting);
   return true;
}

bool freeNsis(HWND parent)
{
   SendMessage(parent, uMsgCreate, FALSE, (LPARAM) parent);
   SetWindowLong(parent,GWL_WNDPROC,(long)lpWndProcOld);
   lpWndProcOld = NULL;
   return true;
}


/*****************************************************
 * FUNCTION NAME: fileTransfer()
 * PURPOSE: 
 *    http/ftp file transfer itself
 *    for any protocol I guess
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
void fileTransfer(HANDLE getFile,
                  HINTERNET hFile)
{
   byte data_buf[1024*8];
   DWORD rslt = 0, done = 0;

   status = ST_DOWNLOAD;
   while(status != ST_CANCELLED &&
      InternetReadFile(hFile, data_buf, sizeof(data_buf), &rslt))
   {
      if(rslt == 0) // EOF reached
      {
         status = ST_OK;
         break;
      }
      if(!WriteFile(getFile, data_buf, rslt, &done, NULL) ||
         done != rslt)
      {
         status = ERR_FILEWRITE;
         break;
      }
      cnt += rslt;
   }
   if(status != ST_OK &&
      status != ST_CANCELLED)
      status = ERR_TRANSFER;
}

/*****************************************************
 * FUNCTION NAME: openInetFile()
 * PURPOSE: 
 *    file open, size request, lseek
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
HINTERNET openInetFile(HINTERNET hConn,
                       INTERNET_SCHEME nScheme,
                       char *path)
{
   char buf[256];
   HINTERNET hFile;
   DWORD rslt, err;
   bool req_sent_ok = false;

   status = ST_URLOPEN;
   if(nScheme == INTERNET_SCHEME_FTP)
   {
      if(cnt == 0)
      {
/* too clever FtpCommand returnes false on the valid "550 Not found" server answer,
   to read answer I had to ignory returned false (!= 999999) :-( 
   GetLastError also possible, but MSDN description of codes is very limited */
         wsprintf(buf, "SIZE %s", path);
         if(myFtpCommand != NULL &&
            myFtpCommand(hConn, false, FTP_TRANSFER_TYPE_ASCII, buf, 0, &hFile) != 9999 &&
            memset(buf, 0, sizeof(buf)) != NULL &&
            InternetGetLastResponseInfo(&err, buf, &(rslt = sizeof(buf))))
         {
            if(strstr(buf, "213 "))
            {
               fs = strtol(strchr(buf, ' ') + 1, NULL, 0);
            }
            /*else if(strstr(buf, "550 "))
            {
               status = ERR_NOTFOUND;
               return NULL;
            }*/
         }
         if(fs == 0) fs = NOT_AVAILABLE;
      }
      else
      {
         wsprintf(buf, "REST %d", cnt);
         if(myFtpCommand == NULL ||
            !myFtpCommand(hConn, false, FTP_TRANSFER_TYPE_BINARY, buf, 0, &hFile) ||
            memset(buf, 0, sizeof(buf)) == NULL ||
            !InternetGetLastResponseInfo(&err, buf, &(rslt = sizeof(buf))) ||
            (strstr(buf, "350") == NULL && strstr(buf, "110") == NULL))
         {
            status = ERR_REGET;
            return NULL;
         }
      }
      if((hFile = FtpOpenFile(hConn, path, GENERIC_READ,
            FTP_TRANSFER_TYPE_BINARY|INTERNET_FLAG_RELOAD,0)) == NULL)
      {
         rslt = GetLastError();
// firewall related error, let's give user time to disable it
         if((rslt == 12003 || rslt == 12002) && !silent)
            resume = true;
         status = ERR_URLOPEN;
      }
   }
   else
   {
      if((hFile = HttpOpenRequest(hConn, post == NULL ? NULL : "POST",
         path, NULL, NULL, NULL,
         INTERNET_FLAG_RELOAD | INTERNET_FLAG_KEEP_CONNECTION | 
         (nScheme == INTERNET_SCHEME_HTTPS ? INTERNET_FLAG_SECURE |
         INTERNET_FLAG_IGNORE_CERT_CN_INVALID | INTERNET_FLAG_IGNORE_CERT_DATE_INVALID |
         INTERNET_FLAG_IGNORE_REDIRECT_TO_HTTP | INTERNET_FLAG_IGNORE_REDIRECT_TO_HTTPS : 0), 0)) != NULL)
      {

         if(*szUsername != 0)
            InternetSetOption(hFile, INTERNET_OPTION_PROXY_USERNAME, szUsername, lstrlen(szUsername) + 1);
         if(*szPassword != 0)
            InternetSetOption(hFile, INTERNET_OPTION_PROXY_PASSWORD, szPassword, sizeof(szPassword));
         if(post != NULL)
            HttpAddRequestHeaders(hFile, POST_HEADER, lstrlen(POST_HEADER),
               HTTP_ADDREQ_FLAG_ADD | HTTP_ADDREQ_FLAG_REPLACE);
         if(nScheme == INTERNET_SCHEME_HTTPS)
         {
            if(!HttpSendRequest(hFile, NULL, 0, post, post == NULL ? 0 : lstrlen(post)))
            {
               InternetQueryOption (hFile, INTERNET_OPTION_SECURITY_FLAGS,
                  (LPVOID)&rslt, &(err = sizeof(rslt)));
               rslt |= SECURITY_FLAG_IGNORE_UNKNOWN_CA | SECURITY_FLAG_IGNORE_REVOCATION;
               InternetSetOption (hFile, INTERNET_OPTION_SECURITY_FLAGS,
                              &rslt, sizeof(rslt) );
            }
            else req_sent_ok = true;
         }
resend:
         if(req_sent_ok || HttpSendRequest(hFile, NULL, 0, post, post == NULL ? 0 : lstrlen(post)))
         {
            if(cnt == 0)
            {
               if(HttpQueryInfo(hFile, HTTP_QUERY_STATUS_CODE,
                  buf, &(rslt = sizeof(buf)), NULL))
               {
                  if(strncmp(buf, "401", 3) == 0)
                     status = ERR_AUTH;
                  else if(strncmp(buf, "403", 3) == 0)
                     status = ERR_FORBIDDEN;
                  else if(strncmp(buf, "404", 3) == 0)
                     status = ERR_NOTFOUND;
                  else if(strncmp(buf, "407", 3) == 0)
                     status = ERR_PROXY;
                  else if(*buf == '4')
                     status = ERR_REQUEST;
                  else if(*buf == '5')
                     status = ERR_SERVER;
               }
               if(HttpQueryInfo(hFile, HTTP_QUERY_CONTENT_LENGTH, buf,
                                &(rslt = sizeof(buf)), NULL))
                  fs = strtoul(buf, NULL, 0);
               else fs = NOT_AVAILABLE;
            }
            else
            {
               if((int)InternetSetFilePointer(hFile, cnt, NULL, FILE_BEGIN, 0) == -1)
               {
                  status = ERR_REGET;
               }
            }
         }
         else
         {
            status = ERR_SENDREQUEST;
            /*wsprintf(buf, "GetLastError=%d", GetLastError());
            MessageBox(childwnd, buf, "", 1);*/
         }
         if(!silent && (status == ERR_PROXY || status == ERR_AUTH))
         {
            rslt = InternetErrorDlg(hDlg, hFile, ERROR_SUCCESS, 
                           FLAGS_ERROR_UI_FILTER_FOR_ERRORS | 
                           FLAGS_ERROR_UI_FLAGS_CHANGE_OPTIONS |
                           FLAGS_ERROR_UI_FLAGS_GENERATE_DATA,
                           NULL);
            if (rslt != ERROR_CANCELLED)
            {
               status = ST_URLOPEN;
               req_sent_ok = false;
               goto resend;
            }
         }
      }
      else status = ERR_OPENREQUEST;
   }
   if(status != ST_URLOPEN && hFile != NULL)
   {
      InternetCloseHandle(hFile);
      hFile = NULL;
   }
   return hFile;
}

/*****************************************************
 * FUNCTION NAME: inetLoad()
 * PURPOSE: 
 *    http/ftp file download
 *    min NSIS specifics :)
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
DWORD __stdcall inetLoad(void *hw)
{
   HINTERNET hSes, hConn, hFile;
   HINSTANCE hInstance = NULL;
   HANDLE getFile = NULL;
   HWND hDlg = (HWND)hw;
   DWORD lastCnt, rslt;
   char *host = (char*)GlobalAlloc(GPTR, g_stringsize),
        *path = (char*)GlobalAlloc(GPTR, g_stringsize),
        *user = (char*)GlobalAlloc(GPTR, g_stringsize),
        *passwd = (char*)GlobalAlloc(GPTR, g_stringsize),
        *params = (char*)GlobalAlloc(GPTR, g_stringsize);

   URL_COMPONENTS uc = {sizeof(URL_COMPONENTS), NULL, 0,
      (INTERNET_SCHEME)0, host, g_stringsize, 0 , user, g_stringsize,
      passwd, g_stringsize, path, g_stringsize, params, g_stringsize};

   if((hSes = InternetOpen("NSIS_InetLoad (Mozilla)", openType, szProxy, NULL, 0)) != NULL)
   {
      if(InternetQueryOption(hSes, INTERNET_OPTION_CONNECTED_STATE, &(rslt=0),
         &(lastCnt=sizeof(DWORD))) &&
         (rslt & INTERNET_STATE_DISCONNECTED_BY_USER))
      {
         INTERNET_CONNECTED_INFO ci = {INTERNET_STATE_CONNECTED, 0};
         InternetSetOption(hSes, 
            INTERNET_OPTION_CONNECTED_STATE, &ci, sizeof(ci));
      }
      if(timeout > 0)
         lastCnt = InternetSetOption(hSes, INTERNET_OPTION_CONNECT_TIMEOUT, &timeout, sizeof(timeout));
// 60 sec WinInet.dll detach delay on socket time_wait fix
//      if(hInstance = GetModuleHandle("wininet.dll"))
      if(hInstance = LoadLibrary("wininet.dll"))
         myFtpCommand = (FTP_CMD)GetProcAddress(hInstance, "FtpCommandA");
      while(!popstring(url) && lstrcmpi(url, "/end") != 0)
      {
         if(popstring(fn) != 0 || lstrcmpi(url, "/end") == 0) break;
         status = ST_CONNECTING;
         cnt = fs = *host = *user = *passwd = *path = *params = 0;
         PostMessage(hDlg, WM_TIMER, 1, 0); // show url & fn, do it sync
         if((getFile = CreateFile(fn, GENERIC_WRITE, FILE_SHARE_READ, NULL, CREATE_ALWAYS,
            0, NULL)) != NULL)
         {
            uc.dwHostNameLength = uc.dwUserNameLength = uc.dwPasswordLength =
               uc.dwUrlPathLength = uc.dwExtraInfoLength = g_stringsize;
            if(InternetCrackUrl(url, 0, ICU_ESCAPE , &uc))
            {
               lstrcat(path, params);
               transfTime = time(NULL);
               do
               {
                  status = ST_CONNECTING;
                  lastCnt = cnt;
                  if((hConn = InternetConnect(hSes, host, uc.nPort,
                     lstrlen(user) > 0 ? user : NULL,
                     lstrlen(passwd) > 0 ? passwd : NULL,
                     uc.nScheme == INTERNET_SCHEME_FTP ? INTERNET_SERVICE_FTP : INTERNET_SERVICE_HTTP,
                     uc.nScheme == INTERNET_SCHEME_FTP ? INTERNET_FLAG_PASSIVE : 0, 0)) != NULL)
                  {
                     if((hFile = openInetFile(hConn, uc.nScheme, path)) != NULL)
                     {
                        fileTransfer(getFile, hFile);
                        InternetCloseHandle(hFile);
                     }
                     InternetCloseHandle(hConn);
                  }
                  else
                  {
                     rslt = GetLastError();
                     if((rslt == 12003 || rslt == 12002) && !silent)
                        resume = true;
                     status = ERR_CONNECT;
                  }
               } while((cnt > lastCnt &&
                  status == ERR_TRANSFER &&
                  SleepEx(PAUSE1_SEC * 1000, false) == 0 &&
                  (status = ST_PAUSE) != ST_OK &&
                  SleepEx(PAUSE2_SEC * 1000, false) == 0)
                  || (resume &&
                  status != ST_OK &&
                  status != ST_CANCELLED &&
                  status != ERR_NOTFOUND &&
                  ShowWindow(hDlg, SW_HIDE) != -1 &&
                  MessageBox(hDlg, szResume, szCaption, MB_RETRYCANCEL|MB_ICONWARNING) == IDRETRY &&
                  (status = ST_PAUSE) != ST_OK &&
                  ShowWindow(hDlg, (popup && !silent) ? SW_NORMAL : SW_HIDE) == false &&
                  SleepEx(PAUSE3_SEC * 1000, false) == 0));
            }
            else status = ERR_CRACKURL;
            CloseHandle(getFile);
            if(status != ST_OK)
            {
               DeleteFile(fn);
               break;
            }
         }
         else status = ERR_FILEOPEN;
      }
      InternetCloseHandle(hSes);
   }
   else status = ERR_INETOPEN;
   GlobalFree(host);
   GlobalFree(path);
   GlobalFree(user);
   GlobalFree(passwd);
   GlobalFree(params);
   PostMessage(hDlg, WM_COMMAND, MAKELONG(IDOK, INTERNAL_OK), 0);
   return status;
}

/*****************************************************
 * FUNCTION NAME: fsFormat()
 * PURPOSE: 
 *    formats DWORD (max 4 GB) file size for dialog, big MB
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
void fsFormat(DWORD bfs,
              char *b)
{
   if(bfs == NOT_AVAILABLE)
      lstrcpy(b, "???");
   else if(bfs == 0)
      lstrcpy(b, "0");
   else if(bfs < 10 * 1024)
      wsprintf(b, "%u bytes", bfs);
   else if(bfs < 10 * 1024 * 1024)
      wsprintf(b, "%u kB", bfs / 1024);
   else wsprintf(b, "%u MB", (bfs / 1024 / 1024));
}

/*****************************************************
 * FUNCTION NAME: onTimer()
 * PURPOSE: 
 *    updates text fields every second
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
void onTimer(HWND hDlg)
{
   char b[256];
   DWORD ct = time(NULL) - transfTime,
      tt = time(NULL) - startTime;
// dialog window caption
   wsprintf(b,  "%s - %s", szCaption, szStatus[status]);
   if(fs > 0 && fs != NOT_AVAILABLE && status == ST_DOWNLOAD)
   {
      wsprintf(b + lstrlen(b), ", %d%%", MulDiv(100, cnt, fs));
   }
   if(*szBanner == 0) SetWindowText(hDlg, b);
// current file and url
   SetDlgItemText(hDlg, IDC_STATIC1, url);
   SetDlgItemText(hDlg, IDC_STATIC2, strchr(fn, '\\') ? strrchr(fn, '\\') + 1 : fn);
// bytes done and rate
   if(cnt > 0)
   {
      fsFormat(cnt, b);
      if(ct > 1 && status == ST_DOWNLOAD)
      {
         lstrcat(b, "   ( ");
         fsFormat(cnt / ct, b + lstrlen(b));
         lstrcat(b, "/sec )");
      }
   }
   else *b = 0;
   SetDlgItemText(hDlg, IDC_STATIC3, b);
// total download time
   wsprintf(b, "%d:%02d:%02d", tt / 3600, (tt / 60) % 60, tt % 60);
   SetDlgItemText(hDlg, IDC_STATIC6, b);
// file size, time remaining, progress bar
   if(fs == NOT_AVAILABLE)
   {
      SetWindowText(GetDlgItem(hDlg, IDC_STATIC5), "Not Available");
      SetWindowText(GetDlgItem(hDlg, IDC_STATIC4), "Unknown");
      ShowWindow(GetDlgItem(hDlg, IDC_PROGRESS1), SW_HIDE);

   }
   else if(fs > 0)
   {
      fsFormat(fs, b);
      SetDlgItemText(hDlg, IDC_STATIC5, b);
      ShowWindow(GetDlgItem(hDlg, IDC_PROGRESS1), SW_NORMAL);
      SendDlgItemMessage(hDlg, IDC_PROGRESS1, PBM_SETPOS, MulDiv(cnt, PB_RANGE, fs), 0);
      if(cnt > 5000)
      {
         ct = MulDiv(fs - cnt, ct, cnt);
         wsprintf(b, "%d:%02d:%02d", ct / 3600, (ct / 60) % 60, ct % 60);
      }
      else *b = 0;
      SetWindowText(GetDlgItem(hDlg, IDC_STATIC4), b);
   }
   else
   {
      SetDlgItemText(hDlg, IDC_STATIC5, "");
      SetDlgItemText(hDlg, IDC_STATIC4, "");
      SendDlgItemMessage(hDlg, IDC_PROGRESS1, PBM_SETPOS, 0, 0);
   }
}

/*****************************************************
 * FUNCTION NAME: dlgProc()
 * PURPOSE: 
 *    dlg message handling procedure
 * SPECIAL CONSIDERATIONS:
 *    todo: better dialog design
 *****************************************************/
BOOL WINAPI dlgProc(HWND hDlg,
                    UINT message,
                    WPARAM wParam,
                    LPARAM lParam )  {
   switch(message)    {
   case WM_INITDIALOG:
      SendDlgItemMessage(hDlg, IDC_PROGRESS1, PBM_SETRANGE,
         0, MAKELPARAM(0, PB_RANGE));
      if(*szBanner != 0)
      {
         SendDlgItemMessage(hDlg, IDC_STATIC13, STM_SETICON,
            (WPARAM)LoadIcon(GetModuleHandle(NULL), MAKEINTRESOURCE(103)), 0);
         SetDlgItemText(hDlg, IDC_STATIC12, szBanner);
         if(*szCaption != 0) SetWindowText(hDlg, szCaption);
      }
      SetTimer(hDlg, 1, 1000, NULL);
      if(*szUrl != 0)
      {
         SetDlgItemText(hDlg, IDC_STATIC20, szUrl);
         SetDlgItemText(hDlg, IDC_STATIC21, szDownloading);
         SetDlgItemText(hDlg, IDC_STATIC22, szConnecting);
         SetDlgItemText(hDlg, IDC_STATIC23, szProgress);
         SetDlgItemText(hDlg, IDC_STATIC24, szSecond);
         SetDlgItemText(hDlg, IDC_STATIC25, szRemaining);
      }
      break;
   case WM_TIMER:
      if(!silent)
      {
         if(popup) onTimer(hDlg);
         else progress_callback();
      }
      break;
   case WM_COMMAND:
      switch(LOWORD(wParam))
      {
      case IDCANCEL:
         if(nocancel) break;
         status = ST_CANCELLED;
      case IDOK:
         if(status != ST_CANCELLED && HIWORD(wParam) != INTERNAL_OK) break;
// otherwise in the silent mode next banner windows may go to background
//         if(silent) sf(hDlg);
//         Sleep(10000);
         DestroyWindow(hDlg);
         break;
      }
   default: return false;
   }
   return true;
}

/*****************************************************
 * FUNCTION NAME: load()
 * PURPOSE: 
 *    http/ftp file download entry point
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
extern "C"
void __declspec(dllexport) load(HWND hwndParent,
                                int string_size,
                                char *variables,
                                stack_t **stacktop,
                                extra_parameters *extra
                                )
{
   HANDLE hThread;
   DWORD dwThreadId;
   MSG msg;
   char *slnt = NULL;

   EXDLL_INIT();

   url = (char*)GlobalAlloc(GPTR, string_size);
// global silent option
   if(extra->exec_flags->silent != 0)
      silent = true;
// we must take this from stack, or push url back
   while(!popstring(url) && *url == '/')
   {
      if(lstrcmpi(url, "/silent") == 0)
      {
         silent = true;
         slnt = (char*)GlobalAlloc(GPTR, string_size);
         popstring(slnt);
      }
      else if(lstrcmpi(url, "/nocancel") == 0)
      {
         
         nocancel = true;
      }
      else if(lstrcmpi(url, "/noproxy") == 0)
      {
         
         openType = INTERNET_OPEN_TYPE_DIRECT;
      }
      else if(lstrcmpi(url, "/resume") == 0)
      {
         popstring(url);
         if(lstrlen(url) > 0)
            lstrcpy(szResume, url);
         resume = true;
      }
      else if(lstrcmpi(url, "/popup") == 0)
      {
         popup = true;
         popstring(szCaption);
      }
      else if(lstrcmpi(url, "/translate") == 0)
      {
         if(popup)
         {
            popstring(szUrl);
            popstring(szStatus[ST_DOWNLOAD]); // Downloading
            popstring(szStatus[ST_CONNECTING]); // Connecting
            lstrcpy(szStatus[ST_URLOPEN], szStatus[ST_CONNECTING]);
            popstring(szDownloading);// file name
            popstring(szConnecting);// received
            popstring(szProgress);// file size
            popstring(szSecond);// remaining time
            popstring(szRemaining);// total time
         }
         else
         {
            popstring(szDownloading);
            popstring(szConnecting);
            popstring(szSecond);
            popstring(szMinute);
            popstring(szHour);
            popstring(szPlural);
            popstring(szProgress);
            popstring(szRemaining);
         }
      }
      else if(lstrcmpi(url, "/banner") == 0)
      {
         popup = true;
         popstring(szCaption);
         popstring(szBanner);
      }
      else if(lstrcmpi(url, "/proxy") == 0)
      {
         szProxy = (char*)GlobalAlloc(GPTR, string_size);
         popstring(szProxy);
         openType = INTERNET_OPEN_TYPE_PROXY;
      }
      else if(lstrcmpi(url, "/username") == 0)
      {
         popstring(szUsername);
      }
      else if(lstrcmpi(url, "/password") == 0)
      {
         popstring(szPassword);
      }
      else if(lstrcmpi(url, "/post") == 0)
      {
         post = (char*)GlobalAlloc(GPTR, string_size);
         popstring(post);
      }
      else if(lstrcmpi(url, "/timeout") == 0)
      {
         popstring(url);
         timeout = strtol(url, NULL, 10);
      }
   }
   if(*szCaption == 0) lstrcpy(szCaption, PLUGIN_NAME);
   pushstring(url);
   if(silent) { resume = false; popup = true; }
// may be silent for plug-in, but not so for installer itself
   if(hwndParent != NULL &&
      (childwnd = FindWindowEx(hwndParent, NULL, "#32770", NULL)) != NULL)
      SetDlgItemText(childwnd, 1006,
      (slnt != NULL && *slnt != 0) ? slnt : PLUGIN_NAME);
   else InitCommonControls(); // or NSIS do this in silent mode and before .onInit?
   startTime = time(NULL);
   if((hDlg = CreateDialog(g_hInstance,
      MAKEINTRESOURCE(*szBanner ? IDD_DIALOG2 : IDD_DIALOG1),
      hwndParent, dlgProc)) != NULL)
   {

      if((hThread = CreateThread(NULL, 0, inetLoad, (LPVOID)hDlg, 0,
         &dwThreadId)) != NULL)
      {
         if(!silent)
         {
            if(popup) ShowWindow(hDlg, SW_NORMAL);
            else attachNsis(hwndParent);
         }

         while(IsWindow(hDlg) &&
               GetMessage(&msg, NULL, 0, 0) > 0)
         {
            if(!IsDialogMessage(hDlg, &msg) &&
               !IsDialogMessage(hwndParent, &msg) &&
               !TranslateMessage(&msg))
                  DispatchMessage(&msg);
         }

         if(WaitForSingleObject(hThread, 3000) == WAIT_TIMEOUT)
         {
            TerminateThread(hThread, 1);
            status = ERR_TERMINATED;
         }
         CloseHandle(hThread);
         if(childwnd != NULL)
         {
            SetDlgItemText(childwnd, 1006, "");
            RedrawWindow(childwnd, NULL, NULL, RDW_INVALIDATE|RDW_ERASE);
         }
         if(!popup && !silent)
         {
               freeNsis(hwndParent);
         }
      }
      else
      {
         status = ERR_THREAD;
         DestroyWindow(hDlg);
      }
   }
   else status = ERR_DIALOG;
   GlobalFree(url);
   if(szProxy) GlobalFree(szProxy);
   if(post) GlobalFree(post);
   if(slnt) GlobalFree(slnt);
   pushstring(szStatus[status]);
}

/*****************************************************
 * FUNCTION NAME: DllMain()
 * PURPOSE: 
 *    Dll main (initialization) entry point
 * SPECIAL CONSIDERATIONS:
 *    
 *****************************************************/
BOOL WINAPI DllMain(HANDLE hInst,
                    ULONG ul_reason_for_call,
                    LPVOID lpReserved)
{
    g_hInstance=(HINSTANCE)hInst;
    return TRUE;
}
