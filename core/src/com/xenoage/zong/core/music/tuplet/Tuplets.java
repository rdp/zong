package com.xenoage.zong.core.music.tuplet;

import com.xenoage.pdlib.PMap;
import com.xenoage.pdlib.PSet;
import com.xenoage.zong.core.music.chord.Chord;


/**
 * Set of tuplets belonging to chords. A single instance of this
 * class should be used for the whole score.
 * 
 * Given a chord, its tuplet can be queried in hash lookup time.
 * Adding or removing a tuplet is done in near hash lookup time
 * (actually in linear time regarding the number of chords
 * within the tuplet).
 * 
 * @author Andreas Wenger
 */
public final class Tuplets
{
	
	private PSet<Tuplet> tuplets;
	private PMap<Chord, Tuplet> chordsMap;
	
	private static final Tuplets empty =
		new Tuplets(new PSet<Tuplet>(), new PMap<Chord, Tuplet>());
	

	/**
	 * Creates a set of tuplets with the given chord-to-beam mapping.
	 */
	private Tuplets(PSet<Tuplet> tuplets, PMap<Chord, Tuplet> chordsMap)
	{
		this.tuplets = tuplets;
		this.chordsMap = chordsMap;
	}
	
	
	/**
	 * Gets an empty set of tuplets.
	 */
	public static Tuplets empty()
	{
		return empty;
	}
	
	
	/**
	 * Adds a tuplet. No chord of the tuplet may already belong to another tuplet,
	 * otherwise an {@link IllegalStateException} is thrown.
	 */
	public Tuplets plus(Tuplet tuplet)
	{
		//all chords must be free of other beams
		for (Chord chord : tuplet.getChords())
		{
			if (chordsMap.containsKey(chord))
			{
				throw new IllegalStateException("Chord is already in another tuplet");
			}
		}
		//add beam
		PSet<Tuplet> tuplets = this.tuplets.plus(tuplet);
		PMap<Chord, Tuplet> chordsMap = this.chordsMap;
		for (Chord chord : tuplet.getChords())
		{
			chordsMap = chordsMap.plus(chord, tuplet);
		}
		return new Tuplets(tuplets, chordsMap);
	}
	
	
	/**
	 * Gets the tuplet belonging to the given chord, or null if there is none.
	 */
	public Tuplet get(Chord chord)
	{
		return chordsMap.get(chord);
	}
	
	
	/**
	 * Removes a tuplet.
	 */
	public Tuplets minus(Tuplet tuplet)
	{
		PSet<Tuplet> beams = this.tuplets.minus(tuplet);
		PMap<Chord, Tuplet> chordsMap = this.chordsMap;
		for (Chord chord : tuplet.getChords())
		{
			chordsMap = chordsMap.minus(chord);
		}
		return new Tuplets(beams, chordsMap);
	}
	
	
	/**
	 * Replaces the tuplet (if any) at the given old chord by an equal new tuplet
	 * at the given new chord.
	 */
	public Tuplets replaceChord(Chord oldChord, Chord newChord)
	{
		Tuplet tuplet = get(oldChord);
		if (tuplet != null)
		{
			Tuplets tuplets = this;
			tuplets = tuplets.minus(tuplet);
			tuplet = tuplet.replaceChord(oldChord, newChord);
			tuplets = tuplets.plus(tuplet);
			return tuplets;
		}
		else
		{
			return this;
		}
	}


}
