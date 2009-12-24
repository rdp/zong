#include <windows.h>


void GetProgramDir(char* szProgramPath)
{
  GetModuleFileName(NULL, (LPSTR)szProgramPath, MAX_PATH);
  strcpy(strrchr((const char *)szProgramPath, '\\')+1, "");
  return;
}


int main(int argc, char *argv[])
{
  SHELLEXECUTEINFO SE;
  memset(&SE,0,sizeof(SE));
  SE.fMask = SEE_MASK_NOCLOSEPROCESS ;
  char szProgramPath[MAX_PATH + 1];
  GetProgramDir(szProgramPath);
  char szProgramDir[MAX_PATH + 1];
  GetProgramDir(szProgramDir);
  SE.lpFile = "javaw"; //strcat(szProgramPath, "lib\\jre\\bin\\javaw");
  SE.lpDirectory = szProgramDir;
  SE.lpParameters = "-cp \"lib/gervill.jar;lib/player.jar;lib/proxymusic-2.0.jar\" com.xenoage.zong.player.Player";
  SE.nShow = SW_SHOW;
  SE.cbSize = sizeof(SE);
  ShellExecuteEx(&SE);
}
