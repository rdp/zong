package com.xenoage.util.math;

import java.awt.geom.Rectangle2D;


/**
 * Class for a 2d rectangle.
 *
 * @author Andreas Wenger
 */
public final class Rectangle2f
  implements Shape
{
  
  public final Point2f position;
  public final Size2f size;
  
  
  public Rectangle2f(Point2f position, Size2f size)
  {
    this.position = position;
    this.size = size;
  }
  
  
  public Rectangle2f(float x, float y, float width, float height)
  {
    position = new Point2f(x, y);
    size = new Size2f(width, height);
  }
  
  
  public Rectangle2f(Rectangle2D rectangle)
	{
	  position = new Point2f((float) rectangle.getX(), (float) rectangle.getY());
	  size = new Size2f((float) rectangle.getWidth(), (float) rectangle.getHeight());
	}
  
  
  public static Rectangle2f rf(float x, float y, float width, float height)
  {
  	return new Rectangle2f(x, y, width, height);
  }
  
  
  public Rectangle2f scale(float f)
  {
  	return new Rectangle2f(position.x * f, position.y * f, size.width * f, size.height * f);
  }
  
  
  public Rectangle2f scaleX(float f)
  {
  	return new Rectangle2f(position.x * f, position.y, size.width * f, size.height);
  }
  
  
  public Rectangle2f scaleY(float f)
  {
  	return new Rectangle2f(position.x, position.y * f, size.width, size.height * f);
  }
  
  
  public Rectangle2f move(float x, float y)
  {
  	return new Rectangle2f(position.add(x, y), size);
  }


  public boolean contains(Point2f point)
  {
    return (point.x >= position.x && point.y >= position.y &&
      point.x <= position.x + size.width && point.y <= position.y + size.height);
  }
  
  
  /**
   * Expands this rectangle by the given rectangle.
   * After the operation both rectangles are optimally enclosed
   * by this rectangle.
   */
  public Rectangle2f extend(Rectangle2f r)
  {
  	float newX1 = Math.min(this.position.x, r.position.x);
  	float newX2 = Math.max(this.position.x + this.size.width, r.position.x + r.size.width);
  	float newY1 = Math.min(this.position.y, r.position.y);
  	float newY2 = Math.max(this.position.y + this.size.height, r.position.y + r.size.height);
    return new Rectangle2f(newX1, newY1, newX2 - newX1, newY2 - newY1);
  }
  
  
  /**
   * Expands this rectangle by the given point.
   * After the operation the given point is enclosed by this rectangle
   * (optimally, if the rectangle had to be extended).
   */
  public Rectangle2f extend(Point2f p)
  {
  	float newX1 = Math.min(this.position.x, p.x);
  	float newX2 = Math.max(this.position.x + this.size.width, p.x);
  	float newY1 = Math.min(this.position.y, p.y);
  	float newY2 = Math.max(this.position.y + this.size.height, p.y);
    return new Rectangle2f(newX1, newY1, newX2 - newX1, newY2 - newY1);
  }
  
  
  public Point2f getCenter()
  {
  	return new Point2f(getCenterX(), getCenterY());
  }
  
  
  public float getCenterX()
  {
  	return position.x + size.width / 2;
  }
  
  
  public float getCenterY()
  {
  	return position.y + size.height / 2;
  }
  
  
}
