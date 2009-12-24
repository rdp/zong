package com.xenoage.zong.data.music.directions;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.Direction;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.format.Position;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a tempo direction, like "Andante", "♩ = 120"
 * or "Ziemlich langsam und mit Ausdruck".
 * 
 * The text is optional, but the meaning must be given.
 *
 * @author Andreas Wenger
 */
public final class Tempo
  extends Direction
{
	
	private final Fraction baseBeat;
	private final int beatsPerMinute;
	
	private final String text;

	
	/**
	 * Creates a new {@link Tempo} direction.
	 * @param baseBeat        the given musical meaning: which beat per minute
	 * @param beatsPerMinute  the given musical meaning: how many of that beats per minute
	 * @param text            custom caption, or null for format "baseBeat = beatsPerMinute"
	 * @param position        custom positioning, or null for default
	 */
	public Tempo(Fraction baseBeat, int beatsPerMinute, String text, Position position)
	{
		super(position);
		this.baseBeat = baseBeat;
		this.beatsPerMinute = beatsPerMinute;
		this.text = text;
	}
	
	
	@Override public Tempo deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}
	
	
	public Fraction getBaseBeat()
	{
		return baseBeat;
	}
	
	
	public int getBeatsPerMinute()
	{
		return beatsPerMinute;
	}
	
	
	public String getText()
	{
		return text;
	}
	
  
}
