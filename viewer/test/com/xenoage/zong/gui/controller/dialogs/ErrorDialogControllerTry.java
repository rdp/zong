package com.xenoage.zong.gui.controller.dialogs;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.language.Lang;
import com.xenoage.zong.util.LookAndFeel;


/**
 * Tests for the {@link ErrorDialogController} class.
 * 
 * @author Andreas Wenger
 */
public class ErrorDialogControllerTry
{
	
	public static void main(String[] args)
	{
		Lang.loadLanguage("de");
		LookAndFeel.activateSystemLookAndFeel();
		try
		{
			int a = 5 / 0;
			a++;
		}
		catch (Exception ex)
		{
			new ErrorDialogController(ErrorLevel.Fatal, "Es ist ein Rechenfehler aufgetreten!\nBitte lernen Sie vernünftig rechnen.",
				ex, "/home/andi/ToDo/Test/Haha.txt", null).show();
		}
	}

}
