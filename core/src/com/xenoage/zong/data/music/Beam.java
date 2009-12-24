package com.xenoage.zong.data.music;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.xenoage.util.iterators.It;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.BeamWaypoint.Type;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a beam that connects
 * two or more chords or notes.
 *
 * @author Andreas Wenger
 */
public class Beam
  extends MusicElement
{
  
  private ArrayList<BeamWaypoint> waypoints;
  
  
  //cache - must be updated each time the beam waypoints change
  public enum HorizontalSpan
	{
		SingleMeasure, Other;
	}
  private HorizontalSpan horizontalSpan = HorizontalSpan.Other;
  public enum VerticalSpan
	{
		SingleStaff, TwoAdjacentStaves, Other;
	}
  private VerticalSpan verticalSpan = VerticalSpan.Other;
  private Measure firstMeasure = null;
  private int firstMeasureIndex = -1;
  private int firstVoiceIndex = -1;
  private Staff upperStaff = null;
  private Staff lowerStaff = null;
  
  
  /**
   * Creates a new beam connecting the given chords.
   * @param chords  the list of chords
   */
  public Beam(LinkedList<Chord> chords)
  {
    if (chords.size() < 2)
      throw new IllegalArgumentException(
        "At least two chords are needed to create a beam!");
    this.waypoints = new ArrayList<BeamWaypoint>(chords.size());
    //create waypoints
    for (Chord chord : chords)
    {
    	BeamWaypoint wp = new BeamWaypoint(this, chord);
    	this.waypoints.add(wp);
    	chord.setBeamWaypoint(wp);
    }
    updateCache();
  }
  
  
  private Beam()
  {
  }
  
  
  /**
	 * Returns a copy of this {@link Beam}.
	 * The waypoints are not copied deeply, but their references are reused.
	 */
  @SuppressWarnings("unchecked") public Beam copy()
	{
		Beam ret = new Beam();
		ret.waypoints = (ArrayList<BeamWaypoint>) this.waypoints.clone();
		ret.horizontalSpan = this.horizontalSpan;
		ret.verticalSpan = this.verticalSpan;
		return ret;
	}
	
	
	/**
	 * Not supported. Use <code>copy</code> instead.
	 */
	@Override public MusicElement deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		throw new UnsupportedOperationException();
	}


  public int getWaypointsCount()
  {
  	return waypoints.size();
  }
  
	
	public It<BeamWaypoint> getWaypoints()
	{
		return new It<BeamWaypoint>(waypoints);
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
		if (wp == waypoints.get(0))
  	{
  		return Type.Start;
  	}
  	else if (wp == waypoints.get(waypoints.size() - 1))
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
	 * Removes this beam. All connected chords are freed from their
	 * beam waypoints.
	 */
	public void remove()
	{
		for (BeamWaypoint wp : waypoints)
		{
			wp.getChord().setBeamWaypoint(null);
		}
	}
	
	
	/**
	 * Replaces the given old chord with the given new one.
	 * A copy of the corresponding {@link BeamWaypoint} is created and returned.
	 */
	public BeamWaypoint replaceChord(Chord oldChord, Chord newChord)
	{
		for (int i = 0; i < waypoints.size(); i++)
		{
			if (waypoints.get(i).getChord() == oldChord)
			{
				BeamWaypoint newWaypoint = new BeamWaypoint(this, newChord);
				waypoints.set(i, newWaypoint);
				updateCache();
				return newWaypoint;
			}
		}
		throw new IllegalArgumentException("Given chord is not part of this beam!");
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
	 * Gets the index of the given chord or -1 if
	 * the chord is not part of this beam.
	 */
	@Deprecated public int getChordIndex(Chord chord)
	{
		It<BeamWaypoint> waypoints = this.getWaypoints();
		for (BeamWaypoint waypoint : waypoints)
		{
			if (waypoint.getChord() == chord)
			{
				return waypoints.getIndex();
			}
		}
		return -1;
	}
	
	
	/**
	 * Gets the horizontal spanning of this beam.
	 */
	public HorizontalSpan getHorizontalSpan()
	{
		return horizontalSpan;
	}
	
	
	/**
	 * Gets the vertical spanning of this beam.
	 */
	public VerticalSpan getVerticalSpan()
	{
		return verticalSpan;
	}
	
	
	/**
	 * Gets the measure of the first waypoint.
	 */
	public Measure getFirstMeasure()
	{
		return firstMeasure;
	}
	
	
	/**
	 * Gets the index of the measure of the first waypoint.
	 */
	public int getFirstMeasureIndex()
	{
		return firstMeasureIndex;
	}
	
	
	/**
	 * Gets the index of the voice of the first waypoint.
	 */
	public int getFirstVoiceIndex()
	{
		return firstVoiceIndex;
	}
	
	
	/**
	 * Gets the upper staff of the beam, that is the staff
	 * with the least index that is reached by this beam.
	 */
	public Staff getUpperStaff()
	{
		return upperStaff;
	}
	
	
	/**
	 * Gets the lower staff of the beam, that is the staff
	 * with the highest index that is reached by this beam.
	 */
	public Staff getLowerStaff()
	{
		return lowerStaff;
	}
	
	
	/**
	 * Gets the maximum number of beam lines used in this beam.
	 */
	public int getMaxBeamLinesCount()
	{
		Fraction minDuration = waypoints.get(0).getChord().getDuration();
		It<BeamWaypoint> waypoints = getWaypoints();
		waypoints.next();
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
	 * Updates the cache, which contains information about the beam
	 * (which is quite expensive to compute).
	 */
	private void updateCache()
	{
		Chord firstChord = waypoints.get(0).getChord();
		if (firstChord.getVoice() == null)
		{
			//beam does not belong to a score yet, the following computations do not work
			return;
		}
		
		this.firstMeasure = firstChord.getVoice().getMeasure();
		this.firstMeasureIndex = this.firstMeasure.getIndex();
		this.firstVoiceIndex = firstChord.getVoiceIndex();
		
		//check if the beam spans over a single measure (the current one)
		boolean singleMeasure = true;
		for (BeamWaypoint waypoint : waypoints)
		{
			Chord chord = waypoint.getChord();
			if (chord.getVoice().getMeasure().getIndex() != this.firstMeasureIndex)
			{
				singleMeasure = false;
				break;
			}
		}
		this.horizontalSpan = (singleMeasure ?
			HorizontalSpan.SingleMeasure : HorizontalSpan.Other);
		
		//check if the beam spans over a single staff or two adjacent staves or more
		Set<Staff> staves = new HashSet<Staff>();
		for (BeamWaypoint waypoint : waypoints)
		{
			Chord chord = waypoint.getChord();
			staves.add(chord.getVoice().getMeasure().getStaff());
		}
		VerticalSpan vSpan = VerticalSpan.Other; 
		if (staves.size() == 1)
		{
			vSpan = VerticalSpan.SingleStaff;
		}
		else if (staves.size() == 2)
		{
			Staff[] twoStaves = staves.toArray(new Staff[0]);
			if (Math.abs(twoStaves[0].getIndex() - twoStaves[1].getIndex()) <= 1)
			{
				vSpan = VerticalSpan.TwoAdjacentStaves;
			}
		}
		this.verticalSpan = vSpan;
		
		//staves of the beam
		if (this.verticalSpan == VerticalSpan.SingleStaff ||
			this.verticalSpan == VerticalSpan.Other) //TODO: wrong for Other.
		{
			lowerStaff = waypoints.get(0).getChord().getVoice().getMeasure().getStaff();
			upperStaff = lowerStaff;
		}
		else if (this.verticalSpan == VerticalSpan.TwoAdjacentStaves)
		{
			Staff[] stavesArray = staves.toArray(new Staff[0]);
			Staff staff1 = stavesArray[0];
			Staff staff2 = stavesArray[1];
			int index1 = staff1.getIndex();
			int index2 = staff2.getIndex();
			this.upperStaff = (index1 > index2 ? staff2 : staff1);
			this.lowerStaff = (index1 > index2 ? staff1 : staff2);
		}
		
	}
	

}
