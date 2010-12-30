package com.xenoage.zong.core.music.beam;

import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.chord.Chord;


/**
 * Waypoint for a beam, belonging to a chord.
 * 
 * The beam can begin or end here, or the
 * chord can be connected to it inbetween.
 * 
 * The waypoint can also mark the ending of a subdivision.
 * This is a group of consecutive notes that are beamed in
 * a normal way, but then only a single beam line connects
 * the marked note with the following one. So this is only
 * applicable for 16th notes or shorter ones.
 * 
 * To find the beam a chord belongs to, use the
 * corresponding function in the {@link Score} class.
 *
 * @author Andreas Wenger
 */
public final class BeamWaypoint
{

	public enum Type
	{
		Start,
		Stop,
		Continue;
	}

	private final Chord chord;
	private final boolean subdivision;


	/**
	 * Creates a new waypoint for a beam.
	 * @param chord        the associated chord of this waypoint
	 * @param subdivision  true, if the beam has a subdivision ending
	 *                     at this point, otherwise false
	 */
	public BeamWaypoint(Chord chord, boolean subdivision)
	{
		this.chord = chord;
		this.subdivision = subdivision;
	}


	/**
	 * Gets the associated chord of this waypoint.
	 */
	public Chord getChord()
	{
		return chord;
	}


	/**
	 * Returns true, if the beam has a subdivision ending
	 * at this point. This means, that only a single beam line
	 * connects this chord to the next chord within the beam
	 * (and is such only applicable for 16th notes or shorter).
	 */
	public boolean getSubdivision()
	{
		return subdivision;
	}


}
