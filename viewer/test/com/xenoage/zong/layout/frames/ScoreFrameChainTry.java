package com.xenoage.zong.layout.frames;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.core.Score;


/**
 * Test class for a {@link ScoreFrameChain}.
 * 
 * @author Andreas Wenger
 */
public class ScoreFrameChainTry
{
	
	/**
	 * Returns a new {@link ScoreFrameChain} containing one
	 * {@link ScoreFrame} with 1m x 1m for testing purposes
	 * for the given score.
	 * The layouter is not started automatically.
	 */
	public static ScoreFrameChain createScoreFrame1m1m(Score score)
	{
		ScoreFrame scoreFrame = new ScoreFrame(new Point2f(0, 0), new Size2f(1000, 1000), score);
		return scoreFrame.getScoreFrameChain();
	}

}
