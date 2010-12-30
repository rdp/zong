/**
 * 
 */
package com.xenoage.zong.gui.view.dialogs;

import com.xenoage.util.language.Lang;
import com.xenoage.zong.gui.controller.dialogs.FeedbackDialogController;
import com.xenoage.zong.util.LookAndFeel;


/**
 *
 * @author Uli Teschemacher
 *
 */
public class FeedbackDialogTry
{
	public static void main(String args[])
	{
		LookAndFeel.activateSystemLookAndFeel();
		Lang.loadLanguage("en");
		FeedbackDialog dialog = new FeedbackDialogController("/its/justatest.txt", "Example text").getDialog();
		dialog.setVisible(true);
	}
}
