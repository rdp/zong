package com.xenoage.zong.commands.io;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.tools.PlaybackTool;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.commands.input.KeyboardScoreInputCommand;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.util.exceptions.PropertyAlreadySetException;


/**
 * This command stops the MIDI playback of the score,
 * if currently running.
 * 
 * The {@link PlaybackTool} is deactivated.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class StopMidiPlaybackCommand
	extends Command
	implements KeyboardScoreInputCommand
{

	private final ScoreDocument doc;


	public StopMidiPlaybackCommand(ScoreDocument doc)
	{
		this.doc = doc;
	}


	/**
	 * Creates a command to stop the playback of the current score.
	 */
	public StopMidiPlaybackCommand()
	{
		this.doc = null;
	}


	@Override public void execute() throws PropertyAlreadySetException
	{
		ScoreDocument doc = this.doc;
		if (doc == null)
		{
			doc = App.getInstance().getScoreDocument(); //TODO: null
		}
		if (doc.getSelectedTool() instanceof PlaybackTool)
		{
			doc.setSelectedTool(null);
		}
	}

}
