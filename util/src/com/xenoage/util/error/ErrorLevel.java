package com.xenoage.util.error;

import com.xenoage.util.logging.Log;


/**
 * The kind of an error.
 * 
 * @author Andreas Wenger
 */
public enum ErrorLevel
{
	
	/**
	 * Error message is shown, program has to be closed.
	 * For example, if inconsistent score data is found.
	 */
	Fatal,
	
	/**
	 * Error message is shown, program can continue.
	 * For example, if a command fails that should always work.
	 */
	Error,
	
	/**
	 * This message is shown, when a document could not be loaded.
	 * The user can send in the document to the developers. 
	 */
	DocumentFormatError,
	
	/**
	 * Just a warning, which is shown on the screen, too.
	 * For example, if a file can not be opened because it doesn't exist.
	 */
	Warning,
	
	/**
	 * An event, which is not interesting for the user, but should be logged.
	 * For example, if a opened file has an invalid format, but can be opened anyway.
	 */
	Remark;
	
	
	/**
	 * Gets the log level of this {@link ErrorLevel}.
	 */
	public int getLogLevel()
	{
		switch (this)
		{
			case Fatal: case Error:
				return Log.ERROR;
			default:
				return Log.WARNING;
		}
	}
	
}
