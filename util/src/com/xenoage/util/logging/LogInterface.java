package com.xenoage.util.logging;


/**
 * Interface for a logging class.
 * TODO: clean up the methods, update the javadocs.
 *
 * @author Andreas Wenger
 */
interface LogInterface
{


  /**
   * Writes a message to the log.
   * @param level The LoggingLevel -> if the message occurs in the log, or not.
   * @param sender Object, which send the message, in most cases <code>this</code>
   * @param msg The Message
   */

  public void log(int level, Object sender, String msg);


  /**
   * Writing a message without a sender
   * @param level LoggingLevel
   * @param msg The Message
   */
  public void log(int level, String msg);


  /**
   * Writes an exception to the log.
   * @param level LoggingLevel
   * @param e The thrown exception
   */
  public void log(int level, Throwable e);
  
  
  /**
   * Writes an exception to the log.
   * @param e The thrown exception
   * 
   * TODO: combine with other function, so that this is possible:
   * log(int level, String msg, Exception ex)
   */
  public void log(Throwable e);


  /**
   * Writes an exception to the log.
   * @param level LoggingLevel
   * @param sender The Object, that caused the exception
   * @param e The thrown exception
   */
  public void log(int level, Object sender, Throwable e);


  /**
   * Sets the log-level to some value, look at the constants
   * @param Level The new logginglevel
   */
  public void setLoggingLevel(int Level);


  /**
   * Closes and flushes the logging-File
   */
  public void close();


  /**
   * Gets the filename of the log file.
   */
  public String getLogFilename();
  
}
