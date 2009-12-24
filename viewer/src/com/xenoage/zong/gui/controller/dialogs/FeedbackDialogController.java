package com.xenoage.zong.gui.controller.dialogs;

import java.io.IOException;

import com.xenoage.util.FileTools;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.io.IO;
import com.xenoage.util.language.Lang;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.commands.help.BugtrackerCommand;
import com.xenoage.zong.gui.view.dialogs.FeedbackDialog;
import com.xenoage.zong.util.report.HttpReport;


/**
 * Controller for the {@link FeedbackDialog}.
 * 
 * @author Andreas Wenger
 */
public class FeedbackDialogController
{
	
	private final FeedbackDialog dialog;
	private final String documentPath;
	
	
	/**
	 * Creates a {@link FeedbackDialogController}.
	 */
	public FeedbackDialogController()
	{
		this.dialog = new FeedbackDialog(this, null, null);
		this.documentPath = null;
	}
	
	
	/**
	 * Creates a {@link FeedbackDialogController} for a document
	 * with the given path
	 */
	public FeedbackDialogController(String documentPath, String text)
	{
		this.dialog = new FeedbackDialog(this, documentPath, text);
		this.documentPath = documentPath;
	}
	
	
	public FeedbackDialog getDialog()
	{
		return dialog;
	}
	
	
	public void openBugtrackerWebsite()
	{
		//TODO: use app's command performer
		App.getInstance().getScoreDocument().getCommandPerformer().execute(
			new BugtrackerCommand());
	}
	
	
	public boolean send()
	{
		HttpReport report = new HttpReport();
		report.registerData("lang.txt", Lang.getCurrentLanguageID());
		report.registerData("info.txt", dialog.getText());
		if (dialog.getEmail().length() > 0)
		{
			report.registerData("email.txt", dialog.getEmail());
		}
		if (dialog.isSendLog())
		{
			try
			{
				report.registerData("app.log", IO.openDataFile("data/app.log"));
			}
			catch (Exception ex)
			{
				report.registerData("app.log", "Error when inserting app.log");
			}
		}
		if (dialog.isSendDoc() && documentPath != null)
		{
			String filename = FileTools.getFileName(documentPath);
			try
			{
				report.registerData(filename, IO.openDataFile(documentPath));
			}
			catch (Exception ex)
			{
				report.registerData(filename, "Error when inserting " + filename);
			}
		}
		try
		{
			report.send();
			App.getInstance().showMessageDialog(Lang.get(Voc.Feedback_SendOK));
			return true;
		}
		catch (IOException ex)
		{
			App.err().report(ErrorLevel.Warning, Voc.Feedback_SendFailed);
			return false;
		}
	}
	

}
