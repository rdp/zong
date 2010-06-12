package com.xenoage.zong.core.format;

import static com.xenoage.pdlib.PVector.pvec;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.annotations.MaybeEmpty;


/**
 * Layout information for a system.
 *
 * @author Andreas Wenger
 */
public final class SystemLayout
{
  
  private final float systemDistance;
  private final float systemMarginLeft;
  private final float systemMarginRight;
  private final PVector<StaffLayout> staffLayouts;
  
  public static final PVector<StaffLayout> emptyStaffLayouts = pvec();
  public static final SystemLayout defaultValue = new SystemLayout(30f, 5f, 5f, emptyStaffLayouts);
  

  /**
   * Creates a new {@link SystemLayout}.
   * @param systemDistance     distance between the bottom line of the previous system to
   *                           the top line of this system in mm
   * @param systemMarginLeft   left system margin: distance between the staves
   *                           and the page margin
   * @param systemMarginRight  right system margin
   * @param staffLayouts       layouts of the staves of the system (may not be null)
   */
	public SystemLayout(float systemDistance, float systemMarginLeft,
  	float systemMarginRight, PVector<StaffLayout> staffLayouts)
  {
  	this.systemDistance = systemDistance;
  	this.systemMarginLeft = systemMarginLeft;
  	this.systemMarginRight = systemMarginRight;
  	this.staffLayouts = staffLayouts;
  }

  
  /**
   * Gets the distance between the bottom line
   * of the previous system to the top line of this system in mm.
   * If this is the first system of a frame, this is the distance
   * to the top margin of the frame (attention: this is different
   * in MusicXML, where a special top-system-distance is used).
   */
  public float getSystemDistance()
  {
    return systemDistance;
  }
  
  
  /**
   * Sets the distance between the bottom line
   * of the previous system to the top line of this system in mm.
   * If this is the first system of a frame, this is the distance
   * to the top margin of the frame (attention: this is different
   * in MusicXML, where a special top-system-distance is used).
   */
  public SystemLayout withSystemDistance(float systemDistance)
  {
  	return new SystemLayout(systemDistance, systemMarginLeft, systemMarginRight, staffLayouts);
  }


  /**
   * Gets the distance between the left side of the staves
   * and the left page margin.
   */
  public float getSystemMarginLeft()
  {
    return systemMarginLeft;
  }
  
  
  /**
   * Sets the distance between the left side of the staves
   * and the left page margin.
   */
  public SystemLayout withSystemMarginLeft(float systemMarginLeft)
  {
    return new SystemLayout(systemDistance, systemMarginLeft,
    	systemMarginRight, staffLayouts);
  }


  /**
   * Gets the distance between the right side of the staves
   * and the left page margin.
   */
  public float getSystemMarginRight()
  {
    return systemMarginRight;
  }
  
  
  /**
   * Sets the distance between the right side of the staves
   * and the right page margin.
   */
  public SystemLayout withSystemMarginRight(float systemMarginRight)
  {
    return new SystemLayout(systemDistance, systemMarginLeft,
    	systemMarginRight, staffLayouts);
  }
  
  
  /**
   * Gets layout information for the staff with the given index,
   * or null if undefined.
   */
  public StaffLayout getStaffLayout(int staffIndex)
  {
  	if (staffLayouts != null &&
  		staffIndex >= 0 && staffIndex < staffLayouts.size())
  		return staffLayouts.get(staffIndex);
  	else
  		return null;
  }
  
  
  /**
   * Gets the layouts of the staves of the system (may also be or contain null).
   */
  @MaybeEmpty public PVector<StaffLayout> getStaffLayouts()
	{
		return staffLayouts;
	}
  
  
  /**
   * Sets the layouts of the staves of the system (may also be or contain null).
   */
  public SystemLayout withStaffLayouts(PVector<StaffLayout> staffLayouts)
	{
		return new SystemLayout(systemDistance, systemMarginLeft, systemMarginRight, staffLayouts);
	}
 
  
}
