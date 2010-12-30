package com.xenoage.util.math;


/**
 * Class for a 2d rectangle.
 *
 * @author Andreas Wenger
 */
public final class Rectangle2i
{
  
  public final Point2i position;
  public final Size2i size;
  
  
  public Rectangle2i(Point2i position, Size2i size)
  {
  	this.position = position;
  	this.size = size;
  }
  
  
  public Rectangle2i(int x, int y, int width, int height)
  {
    if (width < 0)
    {
      x += width;
      width = -width;
    }
    if (height < 0)
    {
      y += height;
      height = -height;
    }
    position = new Point2i(x, y);
    size = new Size2i(width, height);
  }
  
  
  public static Rectangle2i ri(int x, int y, int width, int height)
  {
  	return new Rectangle2i(x, y, width, height);
  }
  
  
  public boolean contains(Rectangle2i r)
  {
    return (r.position.x >= position.x && r.position.y >= position.y &&
    	r.position.x + r.size.width <= position.x + size.width &&
    	r.position.y + r.size.height <= position.y + size.height);
  }
  
  
  /**
   * Expands this rectangle by the given point.
   * After the operation the given point is enclosed by this rectangle
   * (optimally, if the rectangle had to be extended).
   */
  public Rectangle2i extend(Point2i p)
  {
  	int newX1 = Math.min(this.position.x, p.x);
  	int newX2 = Math.max(this.position.x + this.size.width, p.x);
  	int newY1 = Math.min(this.position.y, p.y);
  	int newY2 = Math.max(this.position.y + this.size.height, p.y);
    return new Rectangle2i(newX1, newY1, newX2 - newX1, newY2 - newY1);
  }
  
  
  @Override public String toString()
  {
  	return "x=" + position.x + ", y=" + position.y + ", w=" + size.width + ", h=" + size.height;
  }

  
}
