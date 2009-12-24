package com.xenoage.zong.layout;

import com.xenoage.util.math.Point2f;


/**
 * This class contains a position within a layout:
 * a page index and coordinates relative to the
 * upper left corner in mm.
 * 
 * @author Andreas Wenger
 */
public final class LayoutPosition
{
  
  private final int pageIndex;
  private final Point2f position;
  
  
  public LayoutPosition(int pageIndex, float x, float y)
  {
    this.pageIndex = pageIndex;
    this.position = new Point2f(x, y);
  }
  
  
  public LayoutPosition(int pageIndex, Point2f position)
  {
    this.pageIndex = pageIndex;
    this.position = position;
  }

  
  public int getPageIndex()
  {
    return pageIndex;
  }
  
  
  public Point2f getPosition()
  {
    return position;
  }

  
  public float getX()
  {
    return position.x;
  }

  
  public float getY()
  {
    return position.y;
  }
  

}
