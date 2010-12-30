package com.xenoage.zong.app.error;

import java.awt.Frame;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.exceptions.ThrowableTools;
import com.xenoage.zong.app.App;
import com.xenoage.zong.gui.controller.dialogs.ErrorDialogController;


/**
 * This class handles all errors and warnings that are caught
 * within this program.
 * 
 * @author Andreas Wenger
 */
public class ErrorProcessing
	extends com.xenoage.util.error.ErrorProcessing
{
	
	private Frame parentFrame;
	
	
	/**
	 * Creates a new {@link ErrorProcessing} instance.
	 */
	public ErrorProcessing()
	{
	}
	
	
	/**
	 * Sets the parent frame for showing GUI.
	 * @param parentFrame  if the error is shown in a dialog, this window
	 *                     is used as the parent window (or null).
	 */
	public void setParentFrame(Frame parentFrame)
	{
		this.parentFrame = parentFrame;
	}
	
	
	@Override protected void handleError(ErrorLevel level, String message, Throwable error, String filePath)
	{
		//if it is a fatal error, error or warning, show a dialog
		if (level == ErrorLevel.Fatal || level == ErrorLevel.Error ||
			level == ErrorLevel.DocumentFormatError || level == ErrorLevel.Warning)
		{
			ErrorDialogController dlg = new ErrorDialogController(level, message, error, filePath, parentFrame);
			dlg.show();
		}
		//if it is a fatal error, close application
		if (level == ErrorLevel.Fatal)
		{
			if (App.getInstance().isDesktopApp())
				System.exit(0);
			else
			{
				App.getInstance().showMessageDialog(error.toString()); //TEST
				App.getInstance().showMessageDialog(ThrowableTools.getStackTrace(error));
				throw new Error("Fatal"); //TODO: find nice way to closedown applet
			}
		}
	}

}
