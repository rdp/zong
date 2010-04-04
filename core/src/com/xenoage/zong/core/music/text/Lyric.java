package com.xenoage.zong.core.music.text;

import com.xenoage.zong.core.music.Attachable;
import com.xenoage.zong.core.music.MusicElement;


/**
 * This class represents a single syllable of lyrics.
 * 
 * @author Andreas Wenger
 */
public final class Lyric
	implements MusicElement, Attachable
{
	
	//TODO: Extend may not be enough. the layouter can not know when a line ends.
	//so when only a part of the score is rendered, at the last extend lyric the
	//layouter does not know whether to end the line or not
	public enum SyllableType { Single, Begin, Middle, End, Extend };
	
	private final String text;
	private final SyllableType syllableType;
	private final int verse;
	
	
	/**
	 * Creates a new {@link Lyric} element.
	 */
	public Lyric(String text, SyllableType syllableType, int verse)
	{
		this.text = text;
		this.syllableType = syllableType;
		this.verse = verse;
	}

	
	public static Lyric createExtend(int verse)
	{
		return new Lyric(null, SyllableType.Extend, verse);
	}

	
	public String getText()
	{
		return text;
	}

	
	public SyllableType getSyllableType()
	{
		return syllableType;
	}
	
	
	/**
	 * Gets the number of the verse this {@link Lyric} belongs to,
	 * starting with 0 for the first verse.
	 */
	public int getVerse()
	{
		return verse;
	}
	

}
