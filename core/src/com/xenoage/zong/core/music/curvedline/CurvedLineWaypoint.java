package com.xenoage.zong.core.music.curvedline;

import static com.xenoage.util.NullTools.throwNullArg;

import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.format.BezierPoint;


/**
 * Waypoint for a {@link CurvedLine},
 * belonging to a note of a chord, optionally with
 * formatting information.
 * 
 * The tie or slur can begin or end here,
 * or it is an intermediate point (called continue).
 * The last option is only needed to store
 * formatting information.
 *
 * @author Andreas Wenger
 */
public final class CurvedLineWaypoint
{
  
  public enum Type
  {
    Start, Stop, Continue;
  }
  
  private final Chord chord;
	private final Integer noteIndex; //may be null if the whole chord is meant
	
	//formatting information
	private final BezierPoint bezierPoint;
  
  
  /**
	 * Creates a new waypoint with the given formatting.
	 */
  public CurvedLineWaypoint(Chord chord, Integer noteIndex, BezierPoint bezierPoint)
  {
  	throwNullArg(chord);
    this.chord = chord;
    this.noteIndex = noteIndex;
    this.bezierPoint = bezierPoint;
  }
  
  
  /**
   * Gets the chord this waypoint belongs to.
   */
  public Chord getChord()
	{
		return chord;
	}
  
  
  /**
   * Gets the index of the note, or null if unknown.
   */
  public Integer getNoteIndex()
  {
  	return noteIndex;
  }
  
  
  /**
   * Gets the formatting information, or null.
   */
  public BezierPoint getBezierPoint()
  {
  	return bezierPoint;
  }
  

}
