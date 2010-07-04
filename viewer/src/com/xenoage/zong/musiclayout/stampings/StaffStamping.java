package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.util.math.Fraction;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Rectangle2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.musiclayout.StaffMarks;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.StaffStampingScreenInfo;
import com.xenoage.zong.renderer.stampings.StaffStampingRenderer;


/**
 * Class for a staff stamping.
 *
 * @author Andreas Wenger
 */
public final class StaffStamping
  extends Stamping
{
	
	//format of the staff
	private final Point2f position;
	private final float length;
	private final float interlineSpace;
	private final int linesCount;
	
	//musical position marks, e.g. to convert layout coordinates to musical positions
  private final StaffMarks staffMarks;
  
  //cached information for screen display
  private final StaffStampingScreenInfo screenInfo;
  
  
  /**
   * TIDY
   * @param position        left border, top line
   */
  public StaffStamping(Point2f position, float length, int linesCount,
  	float interlineSpace, StaffMarks staffMarks)
  {
    super(null, Stamping.Level.Staff, null, new Rectangle2f(position,
    	new Size2f(length, (linesCount - 1) * interlineSpace /*TODO: line width! */)));
    this.position = position;
    this.length = length;
    this.linesCount = linesCount;
    this.interlineSpace = interlineSpace;
    this.staffMarks = staffMarks;
    this.screenInfo = new StaffStampingScreenInfo(this);
  }

  
  /**
   * Gets positioning information about this staff stamping,
   */
  public StaffMarks getStaffMarks()
  {
  	return staffMarks;
  }
  
  
  public Point2f getPosition()
  {
    return position;
  }
  
  
  public float getLength()
  {
    return length;
  }
  
  
  public int getLinesCount()
  {
    return linesCount;
  }

  
  public float getInterlineSpace()
  {
    return interlineSpace;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    StaffStampingRenderer.paint(this, params);
  }
  
  
  /**
   * Gets the width of the line in mm.
   */
  public float getLineWidth() //TODO: everything in interline spaces
  {
    return interlineSpace / 8f; //TODO: allow custom values
  }
  
  
  /**
   * Gets cached information about the staff
   * for screen display.
   */
  public StaffStampingScreenInfo getScreenInfo()
  {
  	return screenInfo;
  }
  
  
  /**
   * Computes and returns the y-coordinate of an object
   * on the given line position in mm.
   * Also non-integer values (fractions of interline spaces)
   * are allowed.
   */
  public float computeYMm(float lp)
  {
    return position.y + (linesCount - 1) * interlineSpace -
      lp * interlineSpace / 2 + getLineWidth() / 2; 
  }
  
  
  /**
   * Computes and returns the y-coordinate of an object
   * at the given vertical position in mm as a line position.
   * Also non-integer values are allowed.
   */
  public float computeYLP(float mm)
  {
    return (position.y + (linesCount - 1) * interlineSpace +
    	getLineWidth() / 2 - mm) * 2 / interlineSpace;
  }
  
  
  /**
   * Gets the start position in mm of the measure with the given global index,
   * or throws an {@link IllegalStateException} if positions are unknown.
   */
  public float getMeasureStartMm(int measureIndex)
  {
  	return staffMarks.getMeasureMarksAt(measureIndex).getStartMm();
  }
  
  
  /**
   * Gets the end position in mm of the leading spacing of the measure with the given global index,
   * or throws an {@link IllegalStateException} if positions are unknown.
   */
  public float getMeasureLeadingMm(int measureIndex)
  {
  	return staffMarks.getMeasureMarksAt(measureIndex).getLeadingMm();
  }
  
  
  /**
   * Gets the end position in mm of the measure with the given global index,
   * or throws an {@link IllegalStateException} if positions are unknown.
   */
  public float getMeasureEndMm(int measureIndex)
  {
  	return staffMarks.getMeasureMarksAt(measureIndex).getEndMm();
  }
  
  
  /**
   * Gets the global index of the first measure in this staff,
   * or throws an {@link IllegalStateException} if positions are unknown.
   */
  public int getStartMeasureIndex()
	{
  	return staffMarks.getStartMeasureIndex();
	}


  /**
   * Gets the global index of the last measure in this staff,
   * or throws an {@link IllegalStateException} if positions are unknown.
   */
  public int getEndMeasureIndex()
	{
  	return staffMarks.getEndMeasureIndex();
	}


	/**
	 * See {@link StaffMarks#getMP(float)}.
   * Throws an {@link IllegalStateException} if positions are unknown.
	 */
	public MP getMPAtX(float positionX)
	{
	  return staffMarks.getMPAt(positionX);
	}


	/**
	 * See {@link StaffMarks#getXMmAt(int, Fraction)}.
	 * Throws an {@link IllegalStateException} if positions are unknown.
	 */
	public Float getXMmAt(int measureIndex, Fraction beat)
	{
	  return staffMarks.getXMmAt(measureIndex, beat);
	}
	
	
	/**
	 * See {@link StaffMarks#getXMmAt(int, Fraction)}.
	 * Throws an {@link IllegalStateException} if positions are unknown.
	 */
	public Float getXMmAt(MP mp)
	{
	  return staffMarks.getXMmAt(mp.getMeasure(), mp.getBeat());
	}
  

	/**
	 * Gets the system index of this staff element, relative to its parent frame.
	 * Throws an {@link IllegalStateException} if positions are unknown.
	 */
	public int getSystemIndex()
	{
	  return staffMarks.getSystemIndex();
	}


	/**
	 * Gets the scorewide staff index of this staff element.
	 * Throws an {@link IllegalStateException} if positions are unknown.
	 */
	public int getStaffIndex()
	{
	  return staffMarks.getStaffIndex();
	}
  
	
	public StaffStamping withStaffMarks(StaffMarks staffMarks)
	{
		return new StaffStamping(position, length, linesCount, interlineSpace, staffMarks);
	}
	
  
}
