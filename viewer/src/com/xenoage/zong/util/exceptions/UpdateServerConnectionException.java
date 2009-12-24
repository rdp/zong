package com.xenoage.zong.util.exceptions;


/**
 * This exception is thrown while updating the application,
 * when no connection to the server could be established.
 * 
 * This is a subclass of UpdateException. It can be used
 * to distinguish non-critical problems (e.g. when there
 * was just no internet connection) from
 * severe problems (e.g. wrong update format).
 *
 * @author Andreas Wenger
 */
public class UpdateServerConnectionException
  extends UpdateException
{
  
  public UpdateServerConnectionException()
  {
    super("error.update.noconnectiontoserver");
  }
  
  
  public UpdateServerConnectionException(String message)
  {
    super(message);
  }

}
