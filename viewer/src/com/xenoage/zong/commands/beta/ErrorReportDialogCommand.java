package com.xenoage.zong.commands.beta;

import com.xenoage.zong.commands.Command;
import com.xenoage.zong.gui.controller.dialogs.FeedbackDialogController;
import com.xenoage.zong.gui.view.dialogs.FeedbackDialog;


/**
 * This command shows a dialog that allows to report
 * error messages and feature requests.
 * 
 * @author Andreas Wenger
 */
public class ErrorReportDialogCommand
	extends Command
{
	
	private final String documentPath;
	
	
	public ErrorReportDialogCommand()
	{
		this.documentPath = null;
	}
	
	
	public ErrorReportDialogCommand(String documentPath)
	{
		this.documentPath = documentPath;
	}


	/**
	 * Shows the dialog.
	 */
	@Override
	public void execute()
	{
		FeedbackDialog dlg = new FeedbackDialogController(documentPath, null).getDialog();
		dlg.setVisible(true);
	}
	
}
