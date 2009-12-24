package com.xenoage.zong.data.music.util;

import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.VoiceElement;


/**
 * This is a wrapper class to combine a {@link VoiceElement} with
 * the {@link ScorePosition} it belongs to.
 * 
 * @author Andreas Wenger
 */
public class PositionedVoiceElement
	extends PositionedMusicElement
{
	
	
	public PositionedVoiceElement(VoiceElement element, ScorePosition position)
	{
		super(element, position);
	}
	
	
	@Override public VoiceElement getElement()
	{
		return (VoiceElement) element;
	}
	

}
