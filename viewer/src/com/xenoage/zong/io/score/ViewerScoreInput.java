package com.xenoage.zong.io.score;

import com.xenoage.util.logging.Log;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.io.score.ScoreInputOptions.ErrorLevel;


/**
 * {@link ScoreInput} implementation for the viewer.
 * 
 * @author Andreas Wenger
 */
public class ViewerScoreInput
	extends ScoreInput
{
	

	//the input options used for this score
	ScoreInputOptions options;
	

	/**
	 * Creates a new input interface for the given score.
	 */
	public ViewerScoreInput(Score score, ScoreInputOptions options)
	{
		super(score);
		if (options == null)
			throw new IllegalArgumentException("options may not be null");
		this.options = options;
	}
	
	
	/**
	 * Gets the score this input interface is working on.
	 */
	@Override public Score getScore()
	{
		return score;
	}
	
	
	/**
	 * Gets the current input options.
	 */
	public ScoreInputOptions getOptions()
	{
		return options;
	}
	
	
	/**
	 * Handles the given exception according to the current error level defined
	 * in the input options, or throws it further when {@link ErrorLevel} is <code>Throw</code>.
	 */
	@Override public <T extends Exception> void handleException(T ex) throws T
	{
		if (options.getErrorLevel() == ErrorLevel.Log)
		{
			Log.log(Log.WARNING, this, ex);
		}
		else if (options.getErrorLevel() == ErrorLevel.ShowAndLog)
		{
			App.err().report(com.xenoage.util.error.ErrorLevel.Warning, Voc.Error_InputError, ex);
		}
		else if (options.getErrorLevel() == ErrorLevel.Throw)
		{
			throw ex;
		}
	}

}
