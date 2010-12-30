package com.xenoage.zong.data.event;

import com.xenoage.util.Range;
import com.xenoage.zong.core.Score;


/**
 * This event is fired when the score has been
 * changed.
 * 
 * @author Andreas Wenger
 */
public class ScoreChangedEvent
{
	
	private Score score;
	private Range measures;
	
	
	/**
	 * Creates a new ScoreChangedEvent. If only the contents of some
	 * measures have been changed, use the <code>ScoreChangedEvent(Score, Range)</code>
	 * constructor instead for better performance.
	 * This is the right constructor if measures were added, deleted, inserted
	 * or if the content was heavily changed.
	 * @param score     the affected score
	 */
	public ScoreChangedEvent(Score score)
	{
		this.score = score;
		this.measures = null;
	}
	
	
	/**
	 * Creates a new ScoreChangedEvent.
	 * @param score     the affected score
	 * @param measures  the range of measures, whose contents have been changed.
	 *                  This parameter allows the layout engine only to update
	 *                  a small portion of the layout, but is only useful for
	 *                  small changes.
	 */
	public ScoreChangedEvent(Score score, Range measures)
	{
		this.score = score;
		this.measures = measures;
	}
	
	
	/**
	 * Gets the affected score.
	 */
	public Score getScore()
	{
		return score;
	}
	
	
	/**
	 * Gets the indices of the measures, whose contents have been
	 * changed, or null, if there was a greater change.
	 */
	public Range getMeasures()
	{
		return measures;
	}
	

}
