package com.xenoage.zong.core.music.beam;

import com.xenoage.pdlib.PMap;
import com.xenoage.pdlib.PSet;
import com.xenoage.zong.core.music.chord.Chord;


/**
 * Set of beams belonging to chords. A single instance of this
 * class should be used for the whole score.
 * 
 * Given a chord, its beam can be queried in hash lookup time.
 * Adding or removing a beam is done in near hash lookup time
 * (actually in linear time regarding the number of chords
 * within the beam).
 * 
 * @author Andreas Wenger
 */
public final class Beams
{
	
	private PSet<Beam> beams;
	private PMap<Chord, Beam> chordsMap;
	
	private static final Beams empty =
		new Beams(new PSet<Beam>(), new PMap<Chord, Beam>());
	

	/**
	 * Creates a set of beams with the given chord-to-beam mapping.
	 */
	private Beams(PSet<Beam> beams, PMap<Chord, Beam> chordsMap)
	{
		this.beams = beams;
		this.chordsMap = chordsMap;
	}
	
	
	/**
	 * Gets an empty set of beams.
	 */
	public static Beams empty()
	{
		return empty;
	}
	
	
	/**
	 * Adds a beam. No chord of the beam may already belong to another beam,
	 * otherwise an {@link IllegalStateException} is thrown.
	 */
	public Beams plus(Beam beam)
	{
		//all chords must be free of other beams
		for (BeamWaypoint wp : beam.getWaypoints())
		{
			if (chordsMap.containsKey(wp.getChord()))
			{
				throw new IllegalStateException("Chord is already beamed");
			}
		}
		//add beam
		PSet<Beam> beams = this.beams.plus(beam);
		PMap<Chord, Beam> chordsMap = this.chordsMap;
		for (BeamWaypoint wp : beam.getWaypoints())
		{
			chordsMap = chordsMap.plus(wp.getChord(), beam);
		}
		return new Beams(beams, chordsMap);
	}
	
	
	/**
	 * Gets the beam belonging to the given chord, or null if there is none.
	 */
	public Beam get(Chord chord)
	{
		return chordsMap.get(chord);
	}
	
	
	/**
	 * Removes a beam.
	 */
	public Beams minus(Beam beam)
	{
		PSet<Beam> beams = this.beams.minus(beam);
		PMap<Chord, Beam> chordsMap = this.chordsMap;
		for (BeamWaypoint wp : beam.getWaypoints())
		{
			chordsMap = chordsMap.minus(wp.getChord());
		}
		return new Beams(beams, chordsMap);
	}
	
	
	/**
	 * Replaces the beam (if any) at the given old chord by an equal new beam
	 * at the given new chord.
	 */
	public Beams replaceChord(Chord oldChord, Chord newChord)
	{
		Beam beam = get(oldChord);
		if (beam != null)
		{
			Beams beams = this;
			beams = beams.minus(beam);
			beam = beam.replaceChord(oldChord, newChord);
			beams = beams.plus(beam);
			return beams;
		}
		else
		{
			return this;
		}
	}


}
