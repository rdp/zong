package com.xenoage.zong.gui.controller.dialogs;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Dialog.ModalityType;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.exceptions.ThrowableTools;
import com.xenoage.zong.Zong;
import com.xenoage.util.language.Lang;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.gui.util.MessageBox;
import com.xenoage.zong.gui.view.dialogs.ErrorDialog;
import com.xenoage.zong.util.ImageTools;


/**
 * Controller for the {@link ErrorDialog}.
 * 
 * @author Andreas Wenger
 */
public class ErrorDialogController
{
	
	private ErrorLevel level;
	private String message;
	private Throwable error;
	private String filePath;
	private Frame parentFrame;
	
	private ErrorDialog dialog = null;
	
	
	/**
	 * Creates a controller for an error dialog.
	 */
	public ErrorDialogController(ErrorLevel level, String message, Throwable error,
		String filePath, Frame parentFrame)
	{
		this.level = level;
		this.message = message;
		this.error = error;
		this.filePath = filePath;
		this.parentFrame = parentFrame;
	}
	
	
	/**
	 * Shows the error dialog.
	 */
	public void show()
	{
		//title
		String title = "";
		switch (level)
		{
			case Fatal: title = Lang.get(Voc.Dialog_FatalError); break;
			case Error: title = Lang.get(Voc.Dialog_Error); break;
			case DocumentFormatError: title = Lang.get(Voc.Dialog_DocumentFormatError); break;
			case Warning: title = Lang.get(Voc.Dialog_Warning); break;
		}
		//message
		String message = this.message;
		if (filePath != null)
		{
			message += "\n\nDatei: " + filePath;
		}
		switch (level)
		{
			case Fatal: message += "\n\n" + Lang.get(Voc.Message_FatalError); break;
			case Error: message += "\n\n" + Lang.get(Voc.Message_Error); break;
		}
		//icon
		ImageIcon icon = null;
		switch (level)
		{
			case Fatal: icon = ImageTools.tryToLoadIcon("data/img/icons/fatal48.png"); break;
			case Error: icon = ImageTools.tryToLoadIcon("data/img/icons/error48.png"); break;
			case DocumentFormatError: case Warning:
				icon = ImageTools.tryToLoadIcon("data/img/icons/warning48.png"); break;
		}
		//create dialog - TODO: freezes when for example a texture is not found!!!
		dialog = new ErrorDialog(this, title, message, icon,
			(level != ErrorLevel.Warning), (level != ErrorLevel.Warning) || (filePath != null),
			false, parentFrame);
		dialog.setVisible(true);
		dialog.dispose();
	}
	
	
	/**
	 * Invoked when the user clicks on the "Details..." button.
	 */
	public void showDetails()
	{
		//TODO: freezes when for example a texture is not found!!!
		ErrorDialog detailsDialog = new ErrorDialog(this, Lang.get(Voc.Dialog_Details),
			ThrowableTools.getStackTrace(error), ImageTools.tryToLoadIcon("data/img/icons/code48.png"),
			false, false, true, dialog);
		detailsDialog.setVisible(true);
	}
	
	
	/**
	 * Invoked when the user clicks on the "report error" button.
	 */
	public void reportError()
	{
		/*
		//prepare the e-mail and open e-mail application
		try
		{
			String subject = "[Report]";
			switch (level)
			{
				case Fatal: subject = "[Fatal Error]"; break;
				case Error: subject = "[Error]"; break;
			}
			//TODO: replace by HTTP protocol to send in errors!
			String message = "TODO: WILL BE REPLACED BY HTTP\n\n";
			message += "Error report\n\nFrom:\n" + App.getInstance().getSerialNumber() + "\n\nError:\n" +
				this.message + "\n\nDetails:\n" + ThrowableTools.getStackTrace(error);
			String data = Zong.EMAIL_ERROR_REPORTS + "?SUBJECT=" + subject + " " +
				App.getInstance().getName() + " " + Zong.PROJECT_VERSION + "." + Zong.PROJECT_ITERATION + "&BODY=" + message;
			if (filePath != null)
			{
				data += "___ATTACHMENT=\"" + filePath + "\"";
			}
			URI mailTo = new URI("mailto", data, null);
		  Desktop.getDesktop().mail(mailTo);
	  }
	  catch (Exception ex)
	  {
	  	//will be replaced by HTTP...
	  	MessageBox.show("Senden Sie bitte den Text unter \"Details...\" an " + Zong.EMAIL_ERROR_REPORTS, dialog);
	  }
	  */
		FeedbackDialogController ctrl = new FeedbackDialogController(filePath, message);
		JDialog dlg = ctrl.getDialog();
		dlg.setModalityType(ModalityType.APPLICATION_MODAL);
		dlg.setVisible(true);
	}
	
	
	

}
