package com.xenoage.util.math;


/**
 * Class for a 2d size.
 *
 * @author Andreas Wenger
 */
public final class Size2i
{
  
  public final int width;
  public final int height;
  
  
  public Size2i(int width, int height)
  {
    this.width = width;
    this.height = height;
  }
  
  
  public Size2i(Point2i p)
  {
    this.width = p.x;
    this.height = p.y;
  }
  
  
  public int getArea()
  {
    return width * height;
  }

  
  @Override public boolean equals(Object obj)
  {
    if (obj instanceof Size2i)
    {
      Size2i size = (Size2i) obj;
      return (width == size.width && height == size.height);
    }
    else
    {
      return super.equals(obj);
    }
  }
  
  
  @Override public String toString()
  {
    return width + "x" + height;
  }
  

}
