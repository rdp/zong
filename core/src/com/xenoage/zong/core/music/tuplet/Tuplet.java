package com.xenoage.zong.core.music.tuplet;

import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.chord.Chord;


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
public final class Tuplet
{
	
	private final int actualNotes;
	private final int normalNotes;
	private final Fraction baseDuration;
	
	private final boolean isBracketVisible;
	
	private final PVector<Chord> chords;
	
	
	/**
	 * Creates a new {@link Tuplet}.
	 */
	public Tuplet(int actualNotes, int normalNotes, Fraction baseDuration, boolean isBracketVisible,
		PVector<Chord> chords)
	{
		if (chords.size() < 1)
			throw new IllegalArgumentException("Tuplet must contain at least one chord");
		this.actualNotes = actualNotes;
		this.normalNotes = normalNotes;
		this.baseDuration = baseDuration;
		this.isBracketVisible = isBracketVisible;
		this.chords = chords;
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
	
	
	public Vector<Chord> getChords()
	{
		return chords;
	}

	
	/**
	 * Replaces the given old chord with the given new one.
	 */
	public Tuplet replaceChord(Chord oldChord, Chord newChord)
	{
		int index = chords.indexOf(oldChord);
		if (index > -1)
		{
			return new Tuplet(actualNotes, normalNotes, baseDuration, isBracketVisible,
				chords.with(index, newChord));
		}
		throw new IllegalArgumentException("Given chord is not part of this tuplet");
	}

}
