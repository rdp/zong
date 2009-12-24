package com.xenoage.zong.data.music;


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
 * @author Andreas Wenger
 */
public class BeamWaypoint
{
  
  public enum Type
  {
    Start, Stop, Continue;
  }
  
  private final Beam beam;
  private boolean subdivision = false;
	private Chord chord;
  
  
  /**
   * Creates a new waypoint for a beam.
   * @param beam         the associated beam
   * @param chord        the associated chord of this waypoint
   */
  public BeamWaypoint(Beam beam, Chord chord)
  {
    this.beam = beam;
    this.chord = chord;
  }
  
  
  public Beam getBeam()
  {
    return beam;
  }
  
  
  public Type getType()
  {
    return beam.getWaypointType(this);
  }
  
  
  public Chord getChord()
  {
    return chord;
  }
  
  
  /**
	 * Sets the chord that belongs to this waypoint.
	 */
  public void setChord(Chord chord)
  {
  	this.chord = chord;
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
  
  
  /**
   * True, if the beam has a subdivision ending at this point,
   * otherwise false.
   */
  public void setSubdivision(boolean subdivision)
  {
  	this.subdivision = subdivision;
  }
  

}
