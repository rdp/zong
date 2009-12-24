package com.xenoage.zong.data.music.util;

import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.MusicElement;


/**
 * This is a wrapper class to combine a {@link MusicElement} with
 * the {@link ScorePosition} it belongs to.
 * 
 * @author Andreas Wenger
 */
public abstract class PositionedMusicElement
{
	
	protected final MusicElement element;
	protected final ScorePosition position;
	
	
	public PositionedMusicElement(MusicElement element, ScorePosition position)
	{
		this.element = element;
		this.position = position;
	}
	
	
	public ScorePosition getPosition()
	{
		return position;
	}
	
	
	public abstract MusicElement getElement();
	

}
