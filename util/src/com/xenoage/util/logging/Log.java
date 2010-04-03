package com.xenoage.util.logging;


/**
 * The logging class for Xenoage applications.
 * 
 * It can be initialized to use the application
 * or the applet logging class version.
 * 
 * If a logging method is called but the class
 * was not initialized yet, it is automatically
 * initialized as an application logging class.
 * 
 * @author Andreas Wenger
 */
public class Log
{

  private static LogInterface log = null;

  public static final int ALL = 0;
  public static final int DEBUG = 1;
  public static final int MESSAGE = 2;
  public static final int WARNING = 3;
  public static final int ERROR = 4;
  public static final int COMMAND = 5;

  
  /**
   * Initialize the application logging.
   */
  public static void initApplicationLog(String logFileName, String appNameAndVersion)
  {
  	log = new ApplicationLog(logFileName, appNameAndVersion);
  }
  
  
  /**
   * Initialize the applet logging.
   */
  public static void initAppletLog()
  {
    log = new AppletLog();
  }


  /**
   * Writes a message to the log.
   * @param level The LoggingLevel -> if the message occurs in the log, or not.
   * @param sender Object, which send the message, in most cases <code>this</code>
   * @param msg The Message
   */
  public static void log(int level, Object sender, String msg)
  {
  	assertInit();
    log.log(level, sender, msg);
  }


  /**
   * Writing a message without a sender
   * @param level LoggingLevel
   * @param msg The Message
   */
  public static void log(int level, String msg)
  {
  	assertInit();
    log.log(level, msg);
  }


  /**
   * Writes an exception to the log.
   * @param sender The Object, that caused the exception
   * @param e The thrown exception
   */
  public static void log(int level, Throwable e)
  {
  	assertInit();
    log.log(level, e);
  }
  
  
  /**
   * Writes an exception to the log.
   * @param e The thrown exception
   * 
   * TODO: combine with other function, so that this is possible:
   * log(int level, String msg, Exception ex)
   */
  public static void log(Throwable e)
  {
  	assertInit();
    log.log(e);
  }


  /**
   * Writes an exception to the log.
   * @param level LoggingLevel
   * @param sender The Object, that caused the exception
   * @param e The thrown exception
   * 
   * TIDY: only one log method with all parameters needed
   */
  public static void log(int level, Object sender, Throwable e)
  {
  	assertInit();
    log.log(level, sender, e);
  }
  

  /**
   * Sets the log-level to some value, look at the constants
   * @param Level The new logginglevel
   */

  public static void setLoggingLevel(int Level)
  {
  	assertInit();
    log.setLoggingLevel(Level);
  }


  /**
   * Closes and flushes the logging-File
   */
  public static void close()
  {
  	assertInit();
    log.close();
  }


  /**
   * Gets the filename of the log file.
   */
  public static String getLogFilename()
  {
  	assertInit();
    return log.getLogFilename();
  }
  
  
  /**
   * If this class is not initialized yet, an RuntimeException is thrown.
   */
  private static void assertInit()
  {
    if (log == null)
    {
    	throw new RuntimeException("Logging not initiated");
    }
  }

}
