package com.xenoage.zong.core.music.beam;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.Range.range;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.beam.BeamWaypoint.Type;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.util.DurationInfo;


/**
 * Class for a beam that connects two or more chords.
 *
 * @author Andreas Wenger
 */
public final class Beam
	implements MusicElement
{
  
	public enum HorizontalSpan
	{
		SingleMeasure, Other;
	}
	
	public enum VerticalSpan
	{
		SingleStaff, TwoAdjacentStaves, Other;
	}
	
  private final PVector<BeamWaypoint> waypoints;
  
  //cache
  private HorizontalSpan horizontalSpan = null;
  private VerticalSpan verticalSpan = null;
  private int upperStaffIndex = -1;
  private int lowerStaffIndex = -1;
  
  
  /**
   * Creates a new beam consisting of the given waypoints.
   */
  public static Beam beam(PVector<BeamWaypoint> waypoints)
  {
  	if (waypoints.size() < 2)
    {
      throw new IllegalArgumentException(
        "At least two chords are needed to create a beam!");
    }
		return new Beam(waypoints);
  }
  
  
  /**
   * Creates a new beam consisting of the given chords
   * with no subdivisions.
   */
  public static Beam beam(List<Chord> chords)
  {
  	if (chords.size() < 2)
    {
      throw new IllegalArgumentException(
        "At least two chords are needed to create a beam!");
    }
  	PVector<BeamWaypoint> waypoints = pvec();
  	for (Chord chord : chords)
  	{
  		waypoints = waypoints.plus(new BeamWaypoint(chord, false));
  	}
		return new Beam(waypoints);
  }
  
  
  private Beam(PVector<BeamWaypoint> waypoints)
  {
    this.waypoints = waypoints;
  }
  
	
	public PVector<BeamWaypoint> getWaypoints()
	{
		return waypoints;
	}
	
	
	public BeamWaypoint getFirstWaypoint()
  {
  	return waypoints.get(0);
  }
	
	
	public BeamWaypoint getLastWaypoint()
  {
  	return waypoints.get(waypoints.size() - 1);
  }
	
	
	/**
	 * Gets the type of the given waypoint: Start, Stop or Continue.
	 */
	public Type getWaypointType(BeamWaypoint wp)
	{
		if (wp == waypoints.getFirst())
  	{
  		return Type.Start;
  	}
  	else if (wp == waypoints.getLast())
  	{
  		return Type.Stop;
  	}
  	else if (waypoints.contains(wp))
  	{
  		return Type.Continue;
  	}
  	else
  	{
  		throw new IllegalArgumentException("Given BeamWaypoint is not part of this Beam.");
  	}
	}
	
	
	/**
	 * Gets the type of the given chord: Start, Stop or Continue.
	 */
	public Type getWaypointType(Chord chord)
	{
		if (chord == waypoints.getFirst().getChord())
  	{
  		return Type.Start;
  	}
  	else if (chord == waypoints.getLast().getChord())
  	{
  		return Type.Stop;
  	}
  	else
  	{
  		for (int i : range(1, waypoints.size() - 2))
  		{
  			if (chord == waypoints.get(i).getChord())
  			{
  				return Type.Continue;
  			}
  		}
  	}
  	throw new IllegalArgumentException("Given chord is not part of this Beam.");
	}
	
	
	/**
	 * Gets the horizontal spanning of this beam.
	 */
	public HorizontalSpan getHorizontalSpan(Globals globals)
	{
		if (horizontalSpan == null)
			computeSpan(globals);
		return horizontalSpan;
	}
	
	
	/**
	 * Gets the vertical spanning of this beam.
	 */
	public VerticalSpan getVerticalSpan(Globals globals)
	{
		if (verticalSpan == null)
			computeSpan(globals);
		return verticalSpan;
	}
	
	
	/**
	 * Gets the index of the topmost staff this beam belongs to.
	 */
	public int getUpperStaffIndex(Globals globals)
	{
		if (upperStaffIndex == -1)
			computeSpan(globals);
		return upperStaffIndex;
	}
	
	
	/**
	 * Gets the index of the bottommost staff this beam belongs to.
	 */
	public int getLowerStaffIndex(Globals globals)
	{
		if (lowerStaffIndex == -1)
			computeSpan(globals);
		return lowerStaffIndex;
	}
	
	
	/**
	 * Gets the maximum number of beam lines used in this beam.
	 */
	public int getMaxBeamLinesCount()
	{
		Fraction minDuration = waypoints.get(0).getChord().getDuration();
		for (BeamWaypoint waypoint : waypoints)
		{
			Fraction duration = waypoint.getChord().getDuration();
			if (duration.compareTo(minDuration) < 0)
			{
				minDuration = duration;
			}
		}
		return DurationInfo.getFlagsCount(minDuration);
	}
	
	
	/**
	 * Replaces the given old chord with the given new one.
	 */
	public Beam replaceChord(Chord oldChord, Chord newChord)
	{
		for (int i : range(waypoints))
		{
			if (waypoints.get(i).getChord() == oldChord)
			{
				PVector<BeamWaypoint> waypoints = this.waypoints.with(i,
					new BeamWaypoint(newChord, this.waypoints.get(i).getSubdivision()));
				return new Beam(waypoints);
			}
		}
		throw new IllegalArgumentException("Given chord is not part of this beam");
	}
	
	
	/**
	 * Returns true, if a beam lines subdivision ends at the chord
	 * with the given index.
	 */
	public boolean isEndOfSubdivision(int chordIndex)
	{
		return waypoints.get(chordIndex).getSubdivision();
	}
	
	
	/**
	 * Gets the chord with the given index.
	 */
	public Chord getChord(int chordIndex)
	{
		return waypoints.get(chordIndex).getChord();
	}
	
	
	/**
	 * Computes the horizontal and vertical span of this beam.
	 */
	private void computeSpan(Globals globals)
	{
		//find out horizontal and vertical span
  	Chord firstChord = waypoints.get(0).getChord();
  	MP firstMP = globals.getMP(firstChord);
  	int minStaffIndex = Integer.MAX_VALUE;
  	int maxStaffIndex = Integer.MIN_VALUE;
		
		//check if the beam spans over a single measure (the current one)
		boolean singleMeasure = true;
		for (BeamWaypoint waypoint : waypoints)
		{
			Chord chord = waypoint.getChord();
			MP mpChord = globals.getMP(chord);
			if (mpChord == null)
			{
				throw new IllegalArgumentException("Chord is not registered in globals");
			}
			minStaffIndex = Math.min(minStaffIndex, mpChord.getStaff());
			maxStaffIndex = Math.max(maxStaffIndex, mpChord.getStaff());
			int chordMeasure = mpChord.getMeasure();
			if (chordMeasure != firstMP.getMeasure())
			{
				singleMeasure = false;
				break;
			}
		}
		HorizontalSpan horizontalSpan = (singleMeasure ?
			HorizontalSpan.SingleMeasure : HorizontalSpan.Other);
		
		//check if the beam spans over a single staff or two adjacent staves or more
		Set<Integer> staves = new HashSet<Integer>();
		for (BeamWaypoint waypoint : waypoints)
		{
			Chord chord = waypoint.getChord();
			staves.add(globals.getMP(chord).getStaff());
		}
		VerticalSpan verticalSpan = VerticalSpan.Other; 
		if (staves.size() == 1)
		{
			verticalSpan = VerticalSpan.SingleStaff;
		}
		else if (staves.size() == 2)
		{
			Integer[] twoStaves = staves.toArray(new Integer[0]);
			if (Math.abs(twoStaves[0] - twoStaves[1]) <= 1)
			{
				verticalSpan = VerticalSpan.TwoAdjacentStaves;
			}
		}
		
		this.horizontalSpan = horizontalSpan;
		this.verticalSpan = verticalSpan;
		this.upperStaffIndex = minStaffIndex;
		this.lowerStaffIndex = maxStaffIndex;
	}
	

}
