package com.xenoage.zong.musiclayout;

import java.util.LinkedList;
import java.util.List;

import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.iterators.MultiIt;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Rectangle2f;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.musiclayout.continued.ContinuedElement;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.Stamping;


/**
 * A score frame layout contains the layout of a
 * musical score for one score frame.
 * 
 * This includes the musical stampings as well as the current
 * selection and playback stampings. A list of continued elements
 * (e.g. slurs or voltas) to the following frame is also saved.
 * 
 * @author Andreas Wenger
 */
public final class ScoreFrameLayout
{
  
	//information about the systems in this frame
	private final FrameArrangement frameArrangement;
	
	//for quicker lookup: divided into staff stampings and other stampings
	private final Vector<StaffStamping> staffStampings;
  private final Vector<Stamping> otherStampings;
  
  //stampings
  private LinkedList<Stamping> selectionStampings = new LinkedList<Stamping>();
  private LinkedList<Stamping> playbackStampings = new LinkedList<Stamping>();
  
  //continued elements to the following frame
  private PVector<ContinuedElement> continuedElements;

  
  /**
   * Creates a new layout for a score frame with no selections.
   * @param frameArrangement   information about the systems in this frame
   * @param staffStampings     the list of staff stampings
   * @param otherStampings     the list of all other stampings
   * @param continuedElements  unclosed element of this frame that have to be continued
   *                           on the next frame
   */
  public ScoreFrameLayout(FrameArrangement frameArrangement,
  	Vector<StaffStamping> staffStampings, Vector<Stamping> otherStampings,
  	PVector<ContinuedElement> continuedElements)
  {
  	this.frameArrangement = frameArrangement;
  	this.staffStampings = staffStampings;
  	this.otherStampings = otherStampings;
  	this.continuedElements = continuedElements;
  }
  
  
  /**
   * Gets information about the systems in this frame.
   */
	public FrameArrangement getFrameArrangement()
	{
		return frameArrangement;
	}
	
	
	/**
   * Gets a list of all staff stampings of this frame.
   */
  public Vector<StaffStamping> getStaffStampings()
  {
    return staffStampings;
  }


	/**
   * Gets a list of all other stampings of this frame.
   */
  public Vector<Stamping> getOtherStampings()
  {
    return otherStampings;
  }
  
  
  /**
   * Gets all musical stampings (staves, notes, ...) of this frame,
   * but not selections or playback stampings.
   */
  public Iterable<Stamping> getMusicalStampings()
  {
  	return new MultiIt<Stamping>(staffStampings, otherStampings);
  }
  
  
  /**
   * Gets all stampings of this frame, including both the musical
   * and the status stampings (selection, playback).
   */
  public Iterable<Stamping> getAllStampings()
  {
  	return new MultiIt<Stamping>(staffStampings, otherStampings,
  		selectionStampings, playbackStampings);
  }



  /**
   * Returns the {@link Stamping} under the given position
   * in score layout coordinates (mm relative to the
   * upper left corner) or null, if there is none.
   */
  public Stamping getStampingAt(Point2f point)
  {
    Stamping ret = null;
    int highestLevel = -1;
    for (Stamping s : getMusicalStampings())
    {
      if (s.getLevel().ordinal() > highestLevel && s.getBoundingShape().contains(point))
      {
        highestLevel = s.getLevel().ordinal();
        ret = s;
      }
    }
    return ret;
  }
  
  
  /**
   * Returns the StaffStamping under the given position,
   * or null, if there is none.
   * @param point  the point in score layout coordinates
   */
  public StaffStamping getStaffStampingAt(Point2f point)
  {
    StaffStamping ret = null;
    for (StaffStamping s : staffStampings)
    {
      if (s.getBoundingShape().contains(point))
      {
        ret = (StaffStamping) s;
      }
    }
    return ret;
  }
  
  
  /**
   * Computes and returns the appropriate musical position
   * to the given metric position, or null, if unknown.
   * @param point  the point in score layout coordinates
   */
  public MP computeMP(Point2f point)
  {
    if (point == null)
      return null;
    //first test, if there is a staff element.
    StaffStamping staff = getStaffStampingAt(point);
    //if there is no staff, return null
    if (staff == null)
    {
      return null;
    }
    //otherwise, compute the beat at this position and return it
    else
    {
      float posX = point.x - staff.getPosition().x;
      return staff.getMPAtX(posX);
    }
  }
  
  
  /**
   * Gets a list of the selection stampings of this frame.
   */
  public List<Stamping> getSelectionStampings()
  {
    return selectionStampings;
  }
  
  
  /**
   * Sets the list of the selection stampings of this frame.
   */
  public void setSelectionStampings(LinkedList<Stamping> selectionStampings)
  {
    this.selectionStampings = selectionStampings;
  }
  
  
  /**
   * Gets a list of the playback stampings of this frame.
   */
  public List<Stamping> getPlaybackStampings()
  {
    return playbackStampings;
  }
  
  
  /**
   * Sets the list of the playback stampings of this frame.
   */
  public void setPlaybackStampings(LinkedList<Stamping> playbackStampings)
  {
    this.playbackStampings = playbackStampings;
  }


	/**
	 * Gets the staff stamping with the given containing the given measure,
	 * or null if not found.
	 */
	public StaffStamping getStaffStamping(int staff, int measure)
	{
		for (StaffStamping s : staffStampings)
		{
			if (s.getStaffIndex() == staff && measure >= s.getStartMeasureIndex() &&
				measure <= s.getEndMeasureIndex())
			{
				return s;
			}
		}
		return null;
	}
	
	
	/**
	 * Gets the list of continued elements, that means the unclosed elements
	 * that must also be painted on the next frame.
	 */
	public PVector<ContinuedElement> getContinuedElements()
	{
		return continuedElements;
	}
	
	
	/**
	 * Computes and returns the bounding rectangle of the system with the given index
	 * (relative to the frame). Only the staves are regarded, not text around them
	 * or notes over the top staff or notes below the bottom staff.
	 * If there are no staves in this system, null is returned.
	 */
	public Rectangle2f computeSystemBoundaries(int systemIndex)
	{
		boolean found = false;
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		for (StaffStamping staff : staffStampings)
		{
			if (staff.getSystemIndex() == systemIndex)
			{
				found = true;
				minX = Math.min(minX, staff.getPosition().x);
				minY = Math.min(minY, staff.getPosition().y);
				maxX = Math.max(maxX, staff.getPosition().x + staff.getLength());
				maxY = Math.max(maxY, staff.getPosition().y +
					(staff.getLinesCount() - 1) * staff.getInterlineSpace());
			}
		}
		if (found)
			return new Rectangle2f(minX, minY, maxX - minX, maxY - minY);
		else
			return null;
	}


}
