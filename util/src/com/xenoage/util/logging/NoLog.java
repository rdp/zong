package com.xenoage.util.logging;


/**
 * No logging (quiet).
 * 
 * @author Andreas Wenger
 */
public class NoLog
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
