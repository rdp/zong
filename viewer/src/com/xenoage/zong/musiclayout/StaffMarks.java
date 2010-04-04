package com.xenoage.zong.musiclayout;

import static com.xenoage.zong.core.music.MP.mp;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;


/**
 * A {@link StaffMarks} class stores positioning
 * information on the elements of a {@link StaffStamping}.
 * 
 * The horizontal coordinates of the start and ending of each
 * measure is saved here (there may be gaps in the staff,
 * so also the end position is interesting).
 * 
 * It contains also the relevant {@link MP}s
 * and their horizontal positions within the staff in mm.
 * Coordinates can be converted to ScorePositions and backwards.
 * The voice of the positions are not relevant.
 * 
 * There is no validation of the given data, so be careful what you do.
 * 
 * @author Andreas Wenger
 */
public final class StaffMarks
{
  
	private final int systemIndex;
  private final int staffIndex;
  private final int startMeasureIndex;
  private final int endMeasureIndex;
  
  private final MeasureMarks[] measureMarks;
  
  
  /**
   * Creates a new {@link StaffStampingMarks} instance, containing positioning
   * information on the elements of a {@link StaffStamping}.
   * @param systemIndex        system index relative to frame
   * @param staffIndex         global staff index
   * @param startMeasureIndex  global index of the first measure in the staff stamping
   * @param measureMarks       positioning information about each measure in the staff stamping
   */
  public StaffMarks(int systemIndex, int staffIndex, int startMeasureIndex, MeasureMarks[] measureMarks)
  {
  	if (measureMarks.length == 0)
  	{
  		throw new IllegalArgumentException("At least one measure must be given");
  	}
  	this.systemIndex = systemIndex;
    this.staffIndex = staffIndex;
    this.startMeasureIndex = startMeasureIndex;
    this.endMeasureIndex = startMeasureIndex + measureMarks.length - 1;
    this.measureMarks = measureMarks;
  }
  
  
  /**
   * Gets the scorewide staff index.
   */
  public int getStaffIndex()
  {
    return staffIndex;
  }
  
  
  /**
   * Gets the system index relative to the frame.
   */
  public int getSystemIndex()
  {
    return systemIndex;
  }
  
  
  /**
   * Gets the global index of the first staff in the staff stamping.
   */
  public int getStartMeasureIndex()
  {
  	return startMeasureIndex;
  }
  
  
  /**
   * Gets the global index of the last staff in the staff stamping.
   */
  public int getEndMeasureIndex()
  {
  	return endMeasureIndex;
  }
  
  
  /**
   * Gets the musical position at the given horizontal position in mm.
   * 
   * If it is between two marks (which will be true almost ever), the
   * the right mark is selected (like it is usual e.g. in text
   * processing applications). If it is behind all known marks of the
   * hit measure, the last known beat is returned. Always voice 0 is returned.
   * 
   * If it is not within the boundaries of a measure, null is returned.
   */
  public MP getMPAt(float xMm)
  {
  	//find the measure
  	int measureIndex = getMeasureIndexAt(xMm);
  	//when measure was not found, return null
  	if (measureIndex == -1)
  		return null;
    //get the beat at the given position
  	Fraction beat = measureMarks[measureIndex].getBeatAt(xMm);
    //create and return the score position
    return mp(staffIndex, measureIndex, 0, beat);
  }

  
  /**
   * Gets the horizontal position in mm, relative to the beginning of the staff,
   * of the given measure and beat.
   * If the given measure is not within this list, null is returned.
   * If the given beat is not within this list, the next available beat within
   * this measure is used, or the last beat if the given beat is behind all
   * known beats.
   */
  public Float getXMmAt(int measureIndex, Fraction beat)
  {
  	if (measureIndex < startMeasureIndex || measureIndex > endMeasureIndex)
  		return null;
  	else
  		return measureMarks[measureIndex - startMeasureIndex].getXMmAt(beat);
  }

  
  /**
   * Gets positioning information about the measure with the given global index.
   */
  public MeasureMarks getMeasureMarksAt(int measureIndex)
  {
  	measureIndex -= startMeasureIndex;
  	if (measureIndex >= 0 && measureIndex < measureMarks.length)
  	{
  		return measureMarks[measureIndex];
  	}
  	else
  	{
  		throw new IllegalArgumentException("Measure not within staff: Requested " +
  			measureIndex + ", but must be within [" + startMeasureIndex + "," + endMeasureIndex + "]");
  	}
  }
  
  
  /**
   * Gets the index of the measure at the given position in mm,
   * or -1 if there is none.
   */
  private int getMeasureIndexAt(float xMm)
  {
  	for (int i = 0; i < measureMarks.length; i++)
  	{
  		if (measureMarks[i].contains(xMm))
  		{
  			return i;
  		}
  	}
  	return -1;
  }
  
  
  /**
   * Gets all {@link MeasureMarks}s.
   */
  public MeasureMarks[] getMeasureMarks()
  {
  	return measureMarks;
  }
  

}
