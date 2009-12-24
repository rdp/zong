package com.xenoage.zong.data.music.text;

import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * This class represents a single syllable of lyrics.
 * 
 * @author Andreas Wenger
 */
public final class Lyric
	extends MusicElement
{
	
	//TODO: Extend may not be enough. the layouter can not know when a line ends.
	//so when only a part of the score is rendered, at the last extend lyric the
	//layouter does not know whether to end the line or not
	public enum SyllableType { Single, Begin, Middle, End, Extend };
	
	private final String text;
	private final SyllableType syllableType;
	
	
	/**
	 * Creates a new {@link Lyric} element.
	 */
	public Lyric(String text, SyllableType syllableType)
	{
		this.text = text;
		this.syllableType = syllableType;
	}
	
	
	/**
	 * Return this {@link Lyric}. No deep copy is needed since everything is final.
	 */
	@Override public MusicElement deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}
	
	
	
	public static Lyric createExtend()
	{
		return new Lyric(null, SyllableType.Extend);
	}

	
	public String getText()
	{
		return text;
	}

	
	public SyllableType getSyllableType()
	{
		return syllableType;
	}
	

}
