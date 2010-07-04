package com.xenoage.zong.musiclayout.layouter;

import static com.xenoage.zong.core.music.MP.atStaff;

import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.io.score.ScoreController;
import com.xenoage.zong.layout.frames.ScoreFrameChain;


/**
 * This class can be used as a parameter in all {@link ScoreLayouterStrategy}
 * classes. It contains basic information about the layouting process,
 * like the {@link Score} to be layouted and the used {@link SymbolPool}.
 * 
 * @author Andreas Wenger
 */
public final class ScoreLayouterContext
{
	
	private final Score score;
	private final ScoreFrameChain scoreFrameChain;
	private final SymbolPool symbolPool;
	
	//cache
	private final float maxInterlineSpace;
	
	
	/**
	 * Creates a new {@link ScoreLayouterContext}.
	 * @param ScoreFrameChain  the chain of score frames
	 * @param symbolPool       the symbol style to use
	 */
	public ScoreLayouterContext(ScoreFrameChain scoreFrameChain, SymbolPool symbolPool)
	{
		this.score = scoreFrameChain.getScore();
		this.scoreFrameChain = scoreFrameChain;
		this.symbolPool = symbolPool;
		//cache
		float maxInterlineSpace = 0;
		for (int staff = 0; staff < score.getStavesCount(); staff++)
		{
			maxInterlineSpace = Math.max(maxInterlineSpace,
				ScoreController.getInterlineSpace(score, atStaff(staff)));
		}
		this.maxInterlineSpace = maxInterlineSpace;
	}
	
	
	/**
	 * Gets the score which is layouted.
	 */
	public Score getScore()
	{
		return score;
	}
	
	
	/**
	 * Gets the score frame chain.
	 */
	public ScoreFrameChain getScoreFrameChain()
	{
		return scoreFrameChain;
	}
	
	
	/**
	 * Gets the used symbol style.
	 */
	public SymbolPool getSymbolPool()
	{
		return symbolPool;
	}
	
	
	/**
	 * Gets the biggest interline space of the score.
	 */
	public float getMaxInterlineSpace()
	{
		return maxInterlineSpace;
	}
	

}
