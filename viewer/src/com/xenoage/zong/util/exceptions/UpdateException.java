package com.xenoage.zong.util.exceptions;


/**
 * This exception is thrown, when there
 * was an error while updating the application.
 *
 * @author Andreas Wenger
 */
public class UpdateException
  extends Exception
{
  
  public UpdateException()
  {
    super("error.update.general");
  }
  
  
  public UpdateException(String message)
  {
    super(message);
  }

}
