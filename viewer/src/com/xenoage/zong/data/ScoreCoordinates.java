package com.xenoage.zong.data;

import com.xenoage.util.math.Point2f;


/**
 * This class contains a position within
 * a score, not in musical sense but
 * in metric coordinates: page index
 * and coordinates relative to the
 * upper left corner in mm.
 * 
 * @author Andreas Wenger
 */
@Deprecated public class ScoreCoordinates
{
  
  private int pageIndex;
  private float x;
  private float y;
  
  
  public ScoreCoordinates(int pageIndex, float x, float y)
  {
    this.pageIndex = pageIndex;
    this.x = x;
    this.y = y;
  }

  
  public int getPageIndex()
  {
    return pageIndex;
  }
  
  
  public Point2f getPosition()
  {
    return new Point2f(x, y);
  }

  
  public float getX()
  {
    return x;
  }

  
  public float getY()
  {
    return y;
  }
  

}
