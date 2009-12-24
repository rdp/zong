package com.xenoage.zong.musiclayout.layouter;

import java.util.ArrayList;

import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.layout.frames.ScoreFrame;
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
	private float maxInterlineSpace;
	
	
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
		//TODO: visible staves
		float ret = 0;
		for (int staff = 0; staff < score.getStavesCount(); staff++)
		{
			ret = Math.max(ret, score.getStaff(staff).getInterlineSpace());
		}
		return ret;
	}
	

}
