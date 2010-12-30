package com.xenoage.util.math;


/**
 * Class for a 2d point.
 *
 * @author Andreas Wenger
 */
public final class Point2f
{
  
  public final float x;
  public final float y;
  
  
  public Point2f(Size2f size)
  {
    this.x = size.width;
    this.y = size.height;
  }
  
  
  public Point2f(float x, float y)
  {
    this.x = x;
    this.y = y;
  }
  
  
  public Point2f(float xy)
  {
    this.x = xy;
    this.y = xy;
  }
  
  
  public static Point2f p(float x, float y)
  {
    return new Point2f(x, y);
  }
  
  
  public Point2f add(Point2f p)
  {
  	return new Point2f(x + p.x, y + p.y);
  }
  
  
  public Point2f add(Point2i p)
  {
  	return new Point2f(x + p.x, y + p.y);
  }
  
  
  public Point2f add(Size2f s)
  {
  	return new Point2f(x + s.width, y + s.height);
  }
  
  
  public Point2f add(float x, float y)
  {
  	return new Point2f(this.x + x, this.y + y);
  }
  
  
  public Point2f sub(Point2f p)
  {
  	return new Point2f(x - p.x, y - p.y);
  }
  
  
  public Point2f sub(Size2f s)
  {
  	return new Point2f(x - s.width, y - s.height);
  }
  
  
  public Point2f sub(float x, float y)
  {
  	return new Point2f(this.x - x, this.y - y);
  }
  
  
  public Point2f scale(float s)
  {
  	return new Point2f(x * s, y * s);
  }
  
  
  public Point2f changeX(float x)
  {
  	return new Point2f(x, y);
  }
  
  
  public Point2f changeY(float y)
  {
  	return new Point2f(x, y);
  }
  
  
  public float length()
  {
  	return (float) (Math.sqrt(x * x + y * y));
  }
  
  
  public Point2f normalize()
  {
  	double length = Math.sqrt(x * x + y * y);
  	return new Point2f((float) (x / length), (float) (y / length));
  }
  
  
  public float dotProduct(Point2f p)
  {
  	return x * p.x + y * p.y;
  }
  
  
  /**
   * Rotates this point around another one.
   * @param around   the point to rotate around
   * @param radians  the angle in radians
   */
  public Point2f rotate(Point2f around, float radians)
  {
  	float x = this.x - around.x;
  	float y = this.y - around.y;
  	float cos = (float) Math.cos(radians);
  	float sin = (float) Math.sin(radians);
  	float xr = x * cos - y * sin;
  	float yr = y * cos + x * sin;
  	return p(xr + around.x, yr + around.y);
  }
  
  
  @Override public String toString()
  {
    return x + ", " + y;
  }
  
  
  @Override public boolean equals(Object o)
  {
  	if (o instanceof Point2f)
  	{
  		Point2f p = (Point2f) o;
  		return (x == p.x && y == p.y);
  	}
  	else
  	{
  		return false;
  	}
  }



}
