/**
 * 
 */
package com.xenoage.zong.gui.view.dialogs;

import java.awt.Frame;

import com.xenoage.util.language.Lang;
import com.xenoage.zong.util.ImageTools;
import com.xenoage.zong.util.LookAndFeel;


/**
 * @author Uli Teschemacher
 *
 */
public class ErrorDialogTry
{
	//TEST
	public static void main(String[] args)
	{
		Lang.loadLanguage("de");
		LookAndFeel.activateSystemLookAndFeel();
		new ErrorDialog(null, "Fehler!", "Sorry, da ist was verdammt schief gelaufen.\n\n" +
			"Bitte probieren Sie es auch später nicht noch einmal. " +
			"Ja, wirklich, bitte probieren Sie es auch später nicht noch einmal."/* + \n\nHaha!\n\nHaha!\n\nHaha!" /**/
				+ "\n\nDamit der Fehler möglichst schnell behoben werden kann, können Sie uns das Dokument, das den Fehler verursacht hat, " +
				"per E-Mail zukommen lassen. Klicken Sie hierzu auf \"Fehler per E-Mail melden\".",
				ImageTools.tryToLoadIcon("data/img/icons/fatal48.png"), false, false, false, (Frame)null).setVisible(true);
	}

}
