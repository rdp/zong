package com.xenoage.zong.data.format;


/**
 * Layout information for a system.
 *
 * @author Andreas Wenger
 */
public class SystemLayout
{
  
  //distance between the bottom line of the previous system to the top line
  //of this system in mm
  private Float systemDistance = null;
  
  //left and right system margin: distance between the staves and the page margin
  private Float systemMarginLeft = null;
  private Float systemMarginRight = null;
  
  
  private SystemLayout()
  {
  }
  
  
  public static SystemLayout createEmptySystemLayout()
  {
  	return new SystemLayout();
  }
  
  
  public static SystemLayout createDefaultSystemLayout()
  {
  	SystemLayout ret = new SystemLayout();
  	ret.systemDistance = 30f;
  	ret.systemMarginLeft = 5f;
  	ret.systemMarginRight = 5f;
  	return ret;
  }

  
  /**
   * Gets the distance between the bottom line
   * of the previous system to the top line of this system in mm,
   * or null if unknown.
   * If this is the first system of a frame, this is the distance
   * to the top margin of the frame (attention: this is different
   * in MusicXML, where a special top-system-distance is used).
   */
  public Float getSystemDistance()
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
  public void setSystemDistance(Float systemDistance)
  {
    this.systemDistance = systemDistance;
  }


  /**
   * Gets the distance between the left side of the staves
   * and the left page margin,
   * or null if unknown.
   */
  public Float getSystemMarginLeft()
  {
    return systemMarginLeft;
  }
  
  
  /**
   * Sets the distance between the left side of the staves
   * and the left page margin.
   */
  public void setSystemMarginLeft(Float systemMarginLeft)
  {
    this.systemMarginLeft = systemMarginLeft;
  }


  /**
   * Gets the distance between the right side of the staves
   * and the left page margin,
   * or null if unknown.
   */
  public Float getSystemMarginRight()
  {
    return systemMarginRight;
  }
  
  
  /**
   * Sets the distance between the left side of the staves
   * and the left page margin.
   */
  public void setSystemMarginRight(Float systemMarginRight)
  {
    this.systemMarginRight = systemMarginRight;
  }
 
  
}
