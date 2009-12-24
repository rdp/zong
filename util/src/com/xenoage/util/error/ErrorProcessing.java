package com.xenoage.util.error;

import java.util.List;

import com.xenoage.util.language.Lang;
import com.xenoage.util.language.VocabularyID;
import com.xenoage.util.logging.Log;


/**
 * This class handles all errors and warnings that are caught
 * within this program.
 * 
 * It can be extended to process the error further, e.g. by
 * showing dialogs.
 * 
 * @author Andreas Wenger
 */
public class ErrorProcessing
{
	
	
	/**
	 * Creates a new {@link ErrorProcessing} instance.
	 */
	public ErrorProcessing()
	{
	}
	
	
	/**
	 * Reports a problem.
	 * @param level      the severity of the error
	 * @param messageID  the ID of the message to show
	 */
	public void report(ErrorLevel level, VocabularyID messageID)
	{
		report(level, messageID, new Exception("VocabularyID: " + messageID.toString()), null);
	}
	
	
	/**
	 * Reports a problem.
	 * @param level      the severity of the error
	 * @param messageID  the ID of the message to show
	 * @param error      the exception or error
	 */
	public void report(ErrorLevel level, VocabularyID messageID, Throwable error)
	{
		report(level, messageID, error, null);
	}
	
	
	/**
	 * Reports a problem.
	 * @param level      the severity of the error
	 * @param messageID  the ID of the message to show
	 * @param error      the exception or error
	 * @param filePath   the path of the associated file/URL
	 */
	public void report(ErrorLevel level, VocabularyID messageID, Throwable error, String filePath)
	{
		//log the error
		Log.log(level.getLogLevel(), messageID.getID());
		Log.log(level.getLogLevel(), error);
		//console
		error.printStackTrace();
		//handle error
		handleError(level, Lang.get(messageID), error, filePath);
	}
	
	
	/**
	 * Reports a problem.
	 * @param level      the severity of the error
	 * @param messageID  the ID of the message to show
	 * @param files      a list of filenames that will be added to the message (each in a new line)
	 */
	public void report(ErrorLevel level, VocabularyID messageID, List<String> files)
	{
		//list the files
		StringBuilder filesList = new StringBuilder();
		for (String file : files)
		{
			filesList.append("\n");
			filesList.append(file);
		}
		//log the error
		String logMessage = messageID.getID() + ". The following files were reported: " + filesList.toString().replaceAll("\n", " ");
		Log.log(level.getLogLevel(), logMessage);
		Log.log(level.getLogLevel(), new Exception()); //for the strack trace
		//console
		System.err.println(logMessage);
		//handle error
		handleError(level, Lang.get(messageID) + "\n" + filesList.toString(), new Exception(logMessage), null);
	}
	
	
	/**
	 * Reports a problem. Only call this method if a {@link VocabularyID} can not be used,
	 * because localization of the message is missing here.
	 * @param level      the severity of the error
	 * @param message    the message to show 
	 */
	public void report(ErrorLevel level, String message)
	{
		report(level, message, new Exception(message), null);
	}
	
	
	/**
	 * Reports a problem. Only call this method if a {@link VocabularyID} can not be used,
	 * because localization of the message is missing here.
	 * @param level      the severity of the error
	 * @param message    the message to show 
	 * @param error      the exception or error
	 */
	public void report(ErrorLevel level, String message, Throwable error)
	{
		report(level, message, error, null);
	}
	
	
	/**
	 * Reports a problem. Only call this method if a {@link VocabularyID} can not be used,
	 * because localization of the message is missing here.
	 * @param level      the severity of the error
	 * @param message    the message to show 
	 * @param error      the exception or error
	 * @param filePath   the path of the associated file/URL
	 */
	public void report(ErrorLevel level, String message, Throwable error, String filePath)
	{
		//log the error
		Log.log(level.getLogLevel(), message);
		Log.log(level.getLogLevel(), error);
		//console
		//error.printStackTrace();
		//handle error
		handleError(level, message, error, filePath);
	}
	
	
	/**
	 * Does nothing. Can be overridden to process the error further, e.g.
	 * by showing a dialog.
	 */
	protected void handleError(ErrorLevel level, String message, Throwable error, String filePath)
	{	
	}
	

}
