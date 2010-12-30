/**
 * 
 */
package com.xenoage.zong.util.exceptions;

/**
 * This exception is thrown, when the current selection is invalid for the
 * selected operation or there is none at all.
 * 
 * @author Uli Teschemacher
 */
public class InvalidSelectionException extends Exception 
{
  
	public InvalidSelectionException(String message)
	{
		super(message);
	}
	
}
