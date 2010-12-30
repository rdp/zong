package com.xenoage.util.logging;


/**
 * Logging class for the applet version
 * of Xenoage Score.
 * 
 * At the moment no logging is done
 * in the applet version. (TODO)
 * 
 * @author Andreas Wenger
 */
public class AppletLog
  implements LogInterface
{

  public void close()
  {
  }


  public String getLogFilename()
  {
    return "";
  }


  public void log(int level, Object sender, String msg)
  {
  }


  public void log(int level, String msg)
  {
  }


  public void log(int level, Throwable e)
  {
  }


  public void log(Throwable e)
  {
  }


  public void log(int level, Object sender, Throwable e)
  {
  }


  public void setLoggingLevel(int Level)
  {
  }

}
