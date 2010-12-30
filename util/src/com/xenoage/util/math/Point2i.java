package com.xenoage.util.math;

import java.awt.Point;


/**
 * Class for a 2d point.
 *
 * @author Andreas Wenger
 */
public final class Point2i
{
  
  public final int x;
  public final int y;
  
  
  public Point2i(Point p)
  {
    this.x = p.x;
    this.y = p.y;
  }
  
  
  public Point2i(int x, int y)
  {
    this.x = x;
    this.y = y;
  }
  
  
  public Point2i(Point2f p)
  {
    this.x = Math.round(p.x);
    this.y = Math.round(p.y);
  }
  
  
  public Point2i add(int x, int y)
  {
  	return new Point2i(this.x + x, this.y + y);
  }
  
  
  public Point2i add(Point2i p)
  {
  	return new Point2i(this.x + p.x, this.y + p.y);
  }
  
  
  public Point2i sub(Point2i p)
  {
  	return new Point2i(this.x - p.x, this.y - p.y);
  }
  
  
  @Override public String toString()
  {
    return x + ";" + y;
  }

}
