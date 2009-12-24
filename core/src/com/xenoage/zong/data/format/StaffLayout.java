package com.xenoage.zong.data.format;


/**
 * Layout information for a staff.
 *
 * @author Andreas Wenger
 */
public class StaffLayout
{
  
  //distance between the bottom line of the previous staff to the top line
  //of this staff in mm
  private Float staffDistance = null;

  
  private StaffLayout()
  {
  }
  
  
  public static StaffLayout createEmptyStaffLayout()
  {
  	return new StaffLayout();
  }
  
  
  public static StaffLayout createDefaultStaffLayout()
  {
  	StaffLayout ret = new StaffLayout();
  	ret.staffDistance = 10f;
  	return ret;
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
  public void setStaffDistance(Float staffDistance)
  {
    this.staffDistance = staffDistance;
  }
  
}