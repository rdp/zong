package com.xenoage.zong.data.music;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xenoage.util.iterators.It;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.CurvedLineWaypoint.Type;
import com.xenoage.zong.data.music.text.Lyric;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a chord.
 * 
 * To make things easy, every note is in a chord.
 * Thus also single notes are chord elements
 * by definition.
 * 
 * A chord can have a stem and can be attached to one or more beams.
 * 
 * A chord can be part of a tuplet. Currently, tuplets can not be nested.
 *
 * @author Andreas Wenger
 */
public class Chord
	extends VoiceElement
  implements DurationElement
{
  
  //chord data
	private ChordData data;
	
	//stem, or null for default stem
	//TODO: move into ChordData
	private Stem stem = null;
  
  //the beam waypoint, if the chord is part of a beam,
	//otherwise null (to save space)
  private BeamWaypoint beamWaypoint;
  
  //the ties/slurs, if one or more notes are part of one or more ties/slurs,
  //otherwise null (to save space)
  private Set<CurvedLine> curvedLines;
  
  //the lyric syllables belonging to this chord, one for each verse
  private LinkedList<Lyric> lyrics;
  
  //directions belonging to this chord
  private LinkedList<Direction> directions = null;
  
  //tuplet information
  private TupletInfo tupletInfo = null;
  
  
  /**
   * Creates a new {@link Chord}.
   */
  public Chord(ChordData data)
  {
    this.data = data;
    this.beamWaypoint = null;
    this.curvedLines = null;
    this.lyrics = new LinkedList<Lyric>();
  }
  
  
  /**
	 * Returns a deep copy of this {@link Chord}, using the given
	 * parent voice and {@link DeepCopyCache}.
	 */
	@Override public Chord deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		Chord ret = new Chord(data);
		ret.stem = stem;
		//copy beam
		if (beamWaypoint != null)
		{
			//if there is already a copy of this beam in the cache, use it
			//otherwise, create a new beam and add it to the cache
			Beam oldBeam = beamWaypoint.getBeam();
			Beam newBeam = cache.getNewBeam(oldBeam);
			if (newBeam != null)
			{
				ret.beamWaypoint = newBeam.replaceChord(this, ret);
			}
			else
			{
				newBeam = oldBeam.copy();
				ret.beamWaypoint = newBeam.replaceChord(this, ret);
				cache.addNewBeam(oldBeam, newBeam);
			}
		}
		//copy ties/slurs - TIDY
		if (curvedLines != null && curvedLines.size() > 0)
		{
			for (CurvedLine oldCL : curvedLines)
			{
				CurvedLineWaypoint oldWP = oldCL.getWaypoint(this);
				//if there is already a copy of this slur/tie in the cache, use it
				//otherwise, create a new tie and add it to the cache
				CurvedLine newCL = cache.getNewCurvedLine(oldCL);
				if (newCL == null)
				{
					newCL = oldCL.copy();
					cache.addNewCurvedLine(oldCL, newCL);
				}
				//create and set waypoint
				CurvedLineWaypoint newWP = new CurvedLineWaypoint(
					ret, oldWP.getNoteIndex(), oldWP.getBezierPoint());
				if (oldCL.getWaypointType(this) == Type.Start)
					newCL.setStart(newWP);
				else
					newCL.setStop(newWP);
				//connect to chord
				ret.addCurvedLine(newCL);
			}
		}
		//reuse lyrics
		for (Lyric lyric : lyrics)
		{
			ret.lyrics.add(lyric);
		}
		//directions
		if (directions != null)
			ret.directions = (LinkedList<Direction>) directions.clone();
		else
			ret.directions = null;
		//parent voice
		ret.parentVoice = parentVoice;
		return ret;
	}
  
  
  public ChordData getData()
  {
  	return data;
  }
  
  
  /**
   * Changes the data of the chord. Notice, that indices
   * used by {@link CurvedLineWaypoint}s for example might be
   * invalid now and have to be replaced too.
   */
  public void setData(ChordData data)
  {
  	this.data = data;
  }
  
  
  /**
   * Gets the stem of this chord, or null if a default stem is used.
   */
  public Stem getStem()
  {
  	return stem;
  }
  
  
  /**
   * Sets the stem of this chord, or null if a default stem is used.
   */
  public void setStem(Stem stem)
  {
  	this.stem = stem;
  }
   
  
  @Override public Fraction getDuration()
  {
    return data.getDuration();
  }
  
  
  public Note[] getNotes()
  {
    return data.getNotes();
  }
  
  
  public int getNoteIndex(Note note)
  {
  	for (int i = 0; i < data.getNotes().length; i++)
  	{
  		if (note == data.getNotes()[i])
  			return i;
  	}
  	throw new IllegalArgumentException("Given note is not part of chord");
  }
  
  
  /**
   * Gets the beam waypoint of this note, or
   * null if it isn't connected to a beam.
   */
  public BeamWaypoint getBeamWaypoint()
  {
    return beamWaypoint;
  }
  
  
  /**
   * Sets the beam waypoint of this chord, or
   * null if it isn't connected to a beam.
   * 
   * Call this method only from the {@link Beam} class.
   */
  void setBeamWaypoint(BeamWaypoint beamWaypoint)
  {
    this.beamWaypoint = beamWaypoint;
  }
  
  
  /**
   * Adds a tie/slur to this chord.
   * 
   * Call this method only from the {@link CurvedLine} class
   * or from the deepCopy method.
   */
  void addCurvedLine(CurvedLine curvedLine)
  {
  	if (curvedLines == null)
  		curvedLines = new HashSet<CurvedLine>();
    curvedLines.add(curvedLine);
  }
  
  
  /**
   * Removes a tie/slur waypoint from this chord.
   * 
   * Call this method only from the {@link CurvedLine} class.
   */
  void removeCurvedLine(CurvedLine curvedLine)
  {
    curvedLines.remove(curvedLine);
    if (curvedLines.size() == 0)
    	curvedLines = null; //save space
  }
  
  
  /**
   * Gets the ties/slurs of this chords, or an empty set,
   * if none of the notes is connected to a {@link CurvedLine}.
   */
  public It<CurvedLine> getCurvedLines()
  {
    return new It<CurvedLine>(curvedLines);
  }

  
  /**
   * Collects and returns all pitches of this chord.
   * Pitches that are used more times are also used more
   * times in the list.
   */
  public Pitch[] getPitches()
  {
    return data.getPitches();
  }


	/**
	 * Gets the number of verses for which lyrics are available.
	 * Notice, that there may be unused verses inbetween.
	 */
	public int getLyricsCount()
	{
		return lyrics.size();
	}
	
	
	/**
	 * Gets the lyric of the verse with the given index.
	 * Returns null, if there is none.
	 */
	public Lyric getLyric(int index)
	{
		if (index < lyrics.size())
			return lyrics.get(index);
		else
			return null;
	}
	
	
	/**
	 * Sets the lyric of the verse with the given index.
	 */
	public void setLyric(Lyric lyric, int index)
	{
		//create enough space
		while (index >= lyrics.size())
			lyrics.add(null);
		//set lyric
		lyrics.set(index, lyric);
		//remove null elements from the end
		while (lyrics.size() > 0 && lyrics.getLast() == null)
			lyrics.removeLast();
	}
	
	
	/**
   * Gets a list of the {@link Direction}s attached to this chord,
   * or null if there are none.
   */
  public List<Direction> getDirections()
  {
  	return directions;
  }
  
  
  /**
   * Adds a direction to this chord.
   */
  public void addDirection(Direction direction)
  {
  	if (directions == null)
  		directions = new LinkedList<Direction>();
  	directions.add(direction);
  }


	/**
	 * Gets information about the tuplet this chord is part of, or null
	 * if there is no tuplet or no information about it.
	 */
	public TupletInfo getTupletInfo()
	{
		return tupletInfo;
	}


	/**
	 * Sets information about the tuplet this chord is part of, or null
	 * if there is no tuplet or no information about it.
	 */
	public void setTupletInfo(TupletInfo tupletInfo)
	{
		this.tupletInfo = tupletInfo;
	}

  

}
