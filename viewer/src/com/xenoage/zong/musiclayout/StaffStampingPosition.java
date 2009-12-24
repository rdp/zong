package com.xenoage.zong.musiclayout;

import com.xenoage.zong.musiclayout.stampings.StaffStamping;


/**
 * A StaffStampingPosition is a reference
 * to a StaffStamping and a x-coordinate
 * in mm, as well as the index of its score frame.
 * 
 * @author Andreas Wenger
 */
public class StaffStampingPosition //TIDY: rename
{
  
  private StaffStamping staff;
  private int frameIndex;
  private float positionX;
  
  
  public StaffStampingPosition(StaffStamping staff, int frameIndex, float positionX)
  {
    this.staff = staff;
    this.frameIndex = frameIndex;
    this.positionX = positionX;
  }


  public StaffStamping getStaff()
  {
    return staff;
  }
  
  
  public int getFrameIndex()
  {
    return frameIndex;
  }
  
  
  public float getPositionX()
  {
    return positionX;
  }
  

}
