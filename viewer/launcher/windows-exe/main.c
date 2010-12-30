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
  SE.lpParameters = "-cp \"lib/gervill.jar;lib/gluegen-rt.jar;lib/jogl.jar;lib/proxymusic-2.0.jar;lib/tablelayout.jar;lib/viewer.jar\" -Djava.library.path=\"lib/windows-i586\" com.xenoage.zong.app.ViewerMain";
  SE.nShow = SW_SHOW;
  SE.cbSize = sizeof(SE);
  ShellExecuteEx(&SE);
}
