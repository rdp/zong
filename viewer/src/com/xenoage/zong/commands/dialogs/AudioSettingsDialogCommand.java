package com.xenoage.zong.commands.dialogs;

import com.xenoage.zong.app.App;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.io.midi.out.SynthManager;
import com.xenoage.zong.player.gui.AudioSettingsDialog;


/**
 * This command shows a dialog that allows to change
 * the audio settings, load a soundfont and so on.
 * 
 * @author Andreas Wenger
 */
public class AudioSettingsDialogCommand
	extends Command
{


	/**
	 * Shows the dialog.
	 */
	@Override
	public void execute()
	{
		AudioSettingsDialog dlg = new AudioSettingsDialog(App.getInstance().getMainFrame());
		dlg.setVisible(true);
		if (dlg.isOK())
		{
			//reinit midi
			try
			{
				SynthManager.init(true);
			}
			catch (Exception ex)
			{
				//TODO
			}
		}
	}
	
}
