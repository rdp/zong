package com.xenoage.zong.commands.io;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.tools.PlaybackTool;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.commands.input.KeyboardScoreInputCommand;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.util.exceptions.PropertyAlreadySetException;


/**
 * This command starts a MIDI playback of the score.
 * 
 * The {@link PlaybackTool} is activated, which monitors
 * the playback process (and shows a cursor and so on).
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class StartMidiPlaybackCommand
	extends Command
	implements KeyboardScoreInputCommand
{

	private final ScoreDocument doc;
	private final Score score;


	public StartMidiPlaybackCommand(ScoreDocument doc, Score score)
	{
		this.doc = doc;
		this.score = score;
	}


	/**
	 * Creates a command to playback the current score.
	 */
	public StartMidiPlaybackCommand()
	{
		this.doc = null;
		this.score = null;
	}


	@Override public void execute() throws PropertyAlreadySetException
	{
		ScoreDocument doc = this.doc;
		Score score = this.score;
		if (doc == null || score == null)
		{
			doc = App.getInstance().getScoreDocument(); //TODO: null
			score = doc.getScore(0);
		}
		PlaybackTool tool = new PlaybackTool(doc, score);
		doc.setSelectedTool(tool);
	}

}
