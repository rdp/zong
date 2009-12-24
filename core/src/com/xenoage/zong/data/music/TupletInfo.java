package com.xenoage.zong.data.music;

import com.xenoage.util.math.Fraction;


/**
 * Information about a tuplet (e.g. a triplet).
 * 
 * This class is inspired by MusicXML's time-modification element.
 * 
 * For example, to represent a triplet containing a quarter note
 * and an eighth note, that has the total duration of two eighth notes,
 * <code>baseDuration</code> is 1/8, <code>actualNotes</code> is 3
 * (because there is space for three eighth notes within the tuplet)
 * and <code>normalNotes</code> is 2 (because the duration of
 * the tuplet is two eighth notes).
 * 
 * This class also stores the references of its chords.
 * 
 * @author Andreas Wenger
 */
public final class TupletInfo
{
	
	private final int actualNotes;
	private final int normalNotes;
	private final Fraction baseDuration;
	
	private final boolean isBracketVisible;
	
	private final Chord[] chords;
	
	
	/**
	 * Creates a new {@link TupletInfo}.
	 * The references of the chords back to this instance are created automatically.
	 */
	public TupletInfo(int actualNotes, int normalNotes, Fraction baseDuration, boolean isBracketVisible, Chord[] chords)
	{
		if (chords.length < 1)
			throw new IllegalArgumentException("Tuplet must contain at least one chord");
		this.actualNotes = actualNotes;
		this.normalNotes = normalNotes;
		this.baseDuration = baseDuration;
		this.isBracketVisible = isBracketVisible;
		this.chords = chords;
		for (Chord chord : chords)
		{
			chord.setTupletInfo(this);
		}
	}
	
	
	public int getActualNotes()
	{
		return actualNotes;
	}

	
	public int getNormalNotes()
	{
		return normalNotes;
	}

	
	public Fraction getBaseDuration()
	{
		return baseDuration;
	}

	
	public boolean isBracketVisible()
	{
		return isBracketVisible;
	}
	
	
	public Chord[] getChords()
	{
		return chords;
	}
	
	
	public Chord getFirstChord()
	{
		return chords[0];
	}
	
	
	public Chord getLastChord()
	{
		return chords[chords.length - 1];
	}


}
