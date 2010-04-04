package com.xenoage.zong.core.format;


/**
 * Layout information for a staff.
 *
 * @author Andreas Wenger
 */
public final class StaffLayout
{
  
  //distance between the bottom line of the previous staff to the top line
  //of this staff in mm
  private final float staffDistance;
  
  //default
  private static final StaffLayout defaultInstance = new StaffLayout(10);
  
  
  public StaffLayout(float staffDistance)
  {
  	this.staffDistance = staffDistance;
  }
  
  
  public static StaffLayout getDefault()
  {
  	return defaultInstance;
  }

  
  /**
   * Gets the distance between the bottom line of the previous staff to the top line
   * of this staff in mm.
   * This value has no meaning for the first staff of a system.
   */
  public Float getStaffDistance()
  {
    return staffDistance;
  }
  
  
  /**
   * Sets the distance between the bottom line of the previous staff to the top line
   * of this staff in mm.
   * This value has no meaning for the first staff of a system.
   */
  public StaffLayout withStaffDistance(Float staffDistance)
  {
    return new StaffLayout(staffDistance);
  }

  
}