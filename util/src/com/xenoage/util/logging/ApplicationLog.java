package com.xenoage.util.logging;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;

import com.xenoage.util.io.IO;


/**
 * Logging class for the application version
 * of Xenoage Score.
 * 
 * @author Andreas Wenger
 */
public class ApplicationLog
  implements LogInterface
{
  
  public static final String FILENAME_DEFAULT = "data/app.log";
  
  private static String logFileName;
  
  private static PrintStream writer = null;
  private static int logLevel = 0;

  
  /**
   * Initialize the logging class.
   */
  public ApplicationLog(String logFileName, String appNameAndVersion)
  {
    try
    {
      ApplicationLog.logFileName = logFileName;
      writer = new PrintStream(IO.openOutputStream(logFileName));
      //start message
      writer.print(time() + " " + getLevelString(Log.MESSAGE) +
        " Logging started for: " + appNameAndVersion + "\n");
      //os
      writer.print(time() + " " + getLevelString(Log.MESSAGE) +
        " Operating system: " + System.getProperty("os.name") + ", " +
        System.getProperty("os.version") + "\n");
      //java version
      writer.print(time() + " " + getLevelString(Log.MESSAGE) +
        " Java version: " + System.getProperty("java.version") + "\n");
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }


  /**
   * Writes a message to the log.
   * @param level The LoggingLevel -> if the message occurs in the log, or not.
   * @param sender Object, which send the message, in most cases <code>this</code>
   * @param msg The Message
   */
  public void log(int level, Object sender, String msg)
  {
    if (logLevel <= level)
    {
      if (sender == null)
      {
        sender = new Object();
      }
      writer.print(time() + " " + getLevelString(level) + " "
        + sender.getClass().getName() + ": " + msg + "\n");
      writer.flush();
    }
  }


  /**
   * Writing a message without a sender
   * @param level LoggingLevel
   * @param msg The Message
   */
  public void log(int level, String msg)
  {
    if (logLevel <= level)
      writer.print(time() + " " + getLevelString(level) + " " + msg + "\n");
    writer.flush();
  }


  /**
   * Writes an exception to the log.
   * @param level LoggingLevel
   * @param e The thrown exception
   */
  public void log(int level, Throwable e)
  {
    log(level, null, e);
  }
  
  
  /**
   * Writes an exception to the log.
   * @param e The thrown exception
   * 
   * TODO: combine with other function, so that this is possible:
   * log(int level, String msg, Exception ex)
   */
  public void log(Throwable e)
  {
    log(Log.ERROR, null, e);
  }


  /**
   * Writes an exception to the log.
   * @param level LoggingLevel
   * @param sender The Object, that caused the exception
   * @param e The thrown exception
   */
  public void log(int level, Object sender, Throwable e)
  {
    if (sender == null)
    {
      sender = new Object();
    }
    writer.print(time() + " " + getLevelString(level) + " "
      + sender.getClass().getName() + " threw an exception: " + e.getMessage()
      + "\nStack Trace:\n");
    e.printStackTrace(writer);
    writer.print("\n");
    writer.flush();
  }


  /**
   * Sets the log-level to some value, look at the constants
   * @param Level The new logginglevel
   */

  public void setLoggingLevel(int Level)
  {
    logLevel = Level;

  }


  /**
   * Closes and flushes the logging-File
   */
  public void close()
  {
    writer.print(time() + " " + getLevelString(Log.MESSAGE)
      + " Logging closed." + "\n");
    writer.flush();
    writer.close();
  }


  /**
   * Returns the current time in the format HH:MM:SS.
   * @return a string containing the time
   */
  private static String time()
  {
    DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);

    String s = null;
    s = df.format(new Date());
    return s;
  }


  /**
   * Returns a string that representates a log level
   */
  private String getLevelString(int level)
  {
    if (level == Log.MESSAGE)
      return "       ";
    else if (level == Log.WARNING)
      return "WARNING";
    else if (level == Log.ERROR)
      return "ERROR  ";
    else if (level == Log.DEBUG)
      return "DEBUG  ";
    else if (level == Log.COMMAND)
      return "COMMAND";
    else
      return "???    ";
  }


  /**
   * Gets the filename of the log file.
   */
  public String getLogFilename()
  {
    return logFileName;
  }

}
