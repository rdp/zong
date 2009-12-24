package com.xenoage.zong.musiclayout.layouter.cache.util;

import com.xenoage.util.iterators.It;
import com.xenoage.zong.data.music.Beam;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.StemDirection;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.StemStamping;

import java.util.ArrayList;


/**
 * This class is used by the layouter
 * to collect the stems connected by one beam.
 * 
 * The middle stems are stamped last, so only
 * their horizontal and both possible vertical
 * start positions are saved.
 *
 * @author Andreas Wenger
 */
public class BeamedStemStampings
{
  
	public static class OpenBeamMiddleStem
	{
		public StaffStamping staff;
		public Chord chord;
		public StemDirection stemDirection;
		public float positionX;
		public float bottomNoteLP;
		public float topNoteLP;
	}
	
  private Beam beam;
  private StemStamping firstStem;
  private ArrayList<OpenBeamMiddleStem> middleStems;
  private StemStamping lastStem;
  
  
  /**
   * Creates an empty list of beamed stems
   * for the given {@link Beam} instance.
   */
  public BeamedStemStampings(Beam beam)
  {
    this.beam = beam;
    this.middleStems = new ArrayList<OpenBeamMiddleStem>(beam.getWaypointsCount()); 
  }
  
  
  /**
   * Gets the {@link Beam} instance this list of beamed
   * stems belongs to.
   */
  public Beam getBeam()
  {
    return beam;
  }
  
  
  /**
   * Gets the first stem of the beam.
   */
  public StemStamping getFirstStem()
  {
    return this.firstStem;
  }
  
  
  /**
   * Sets the first stem of the beam.
   */
  public void setFirstStem(StemStamping firstStem)
  {
    this.firstStem = firstStem;
  }
  
  
  /**
   * Gets the number of stems.
   */
  public int getStemsCount()
  {
    return this.middleStems.size() + 2;
  }
  
  
  /**
   * Adds a stem to the middle part of beam.
   */
  public void addMiddleStem(OpenBeamMiddleStem middleStem)
  {
    this.middleStems.add(middleStem);
  }
  
  
  /**
   * Gets an iterator over the middle stems.
   */
  public It<OpenBeamMiddleStem> getMiddleStems()
  {
  	return new It<OpenBeamMiddleStem>(middleStems);
  }
  
  
  /**
   * Gets the last stem of the beam.
   */
  public StemStamping getLastStem()
  {
    return this.lastStem;
  }
  
  
  /**
   * Sets the last stem of the beam.
   */
  public void setLastStem(StemStamping lastStem)
  {
    this.lastStem = lastStem;
  }
  
  
  /**
   * Gets the horizontal position of the stem with the given index
   * (it may be the first, last or any middle stem).
   */
  public float getStemX(int index)
  {
  	if (index == 0)
  		return this.firstStem.getPositionX();
  	else if (index == middleStems.size() + 1)
  		return this.lastStem.getPositionX();
  	else
  		return this.middleStems.get(index - 1).positionX;
  }
  

}
