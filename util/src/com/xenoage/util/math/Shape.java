package com.xenoage.util.math;


/**
 * Interface for a 2d shape (rectangle, circle,
 * polygon, ...)
 *
 * @author Andreas Wenger
 */
public interface Shape
{
  
  /**
   * Returns true, if the given point is contained
   * within this shape.
   */
  public boolean contains(Point2f point);

}