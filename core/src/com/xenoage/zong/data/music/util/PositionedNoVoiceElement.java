package com.xenoage.zong.data.music.util;

import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.NoVoiceElement;


/**
 * This is a wrapper class to combine a {@link NoVoiceElement} with
 * the {@link ScorePosition} it belongs to.
 * 
 * @author Andreas Wenger
 */
public class PositionedNoVoiceElement
	extends PositionedMusicElement
{
	
	
	public PositionedNoVoiceElement(NoVoiceElement element, ScorePosition position)
	{
		super(element, position);
	}
	
	
	@Override public NoVoiceElement getElement()
	{
		return (NoVoiceElement) element;
	}
	

}
