/***************************************************
* FILE NAME: ps.c
*
* Copyright 2004 Virtual Portal Corporation
*
* PURPOSE:
*    CGI-interface for post commands
*
* CHANGE HISTORY
*
* $LOG$
*
* Author              Date            Modifications
* Takhir Bedertdinov  04-august-2004  Original
****************************************************/

#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <fstab.h>
#include <string.h>
#include <errno.h>
#include <sys/stat.h>
#include <unistd.h>
#include <pwd.h>
#include <grp.h>
#include <time.h>
#include <sys/param.h>
#include <signal.h>
#include <syslog.h>
#include <stdarg.h>
#include <stdlib.h>
#include <string.h>


#define SN_TYPE         "Content-type: binary/octet-stream\r\n\r\n"

/*****************************************************
 * FUNCTION NAME: psSaveFile()
 * 
 * PURPOSE: 
 *    saves file to local file system
 * SPECIAL CONSIDERATIONS:
 *      
 *****************************************************/
#define BUF_SIZE 1024

int psEchoFile(void)
{
   char buf[BUF_SIZE];
   int bytesRead = 0, i;

   memset(buf, 0, sizeof(buf));
   while(bytesRead < BUF_SIZE && read(0, buf + bytesRead, 1) == 1 && buf[bytesRead] != 0)
   {
      bytesRead++;
   }
   if(bytesRead == 0)
   {
      /* GET */
      write(1, "This is GET", strlen("This is GET"));
   }
   else
   {
		write(1, buf, bytesRead);
   }
   return bytesRead;
}


/*****************************************************
 * FUNCTION NAME: main()
 * 
 * PURPOSE: 
 *    main entry point. and lib request decoder
 * SPECIAL CONSIDERATIONS:
 *      
 *****************************************************/
int main()
{
   int ds;
   char b[64];
   setuid(0);
   setgid(0);

   write (1, SN_TYPE, strlen (SN_TYPE));
   system("env");
   write (1, "\r\n", 2);
   write (1, getenv("QUERY_STRING"), strlen(getenv("QUERY_STRING")));
   write (1, "\r\n", 2);
   psEchoFile();
   return 0;
}


