package com.xenoage.util.math;


/**
 * Class for a 2d size.
 *
 * @author Andreas Wenger
 */
public final class Size2f
{
  
  public final float width;
  public final float height;
  
  
  public Size2f(Size2f size)
  {
    this.width = size.width;
    this.height = size.height;
  }
  
  
  public Size2f(Size2i size)
  {
    this.width = size.width;
    this.height = size.height;
  }
  
  
  public Size2f(float width, float height)
  {
    this.width = width;
    this.height = height;
  }
  
  
  public float getArea()
  {
    return width * height;
  }
  
  
  public Size2f add(Size2i size)
  {
  	return new Size2f(this.width + size.width, this.height + size.height);
  }
  
  
  public Size2f scale(float f)
  {
  	return new Size2f(width * f, height * f);
  }
  
  
  public Size2f changeWidth(float width)
  {
  	return new Size2f(width, height);
  }
  
  
  public Size2f changeHeight(float height)
  {
  	return new Size2f(width, height);
  }

  
  @Override public boolean equals(Object obj)
  {
    if (obj instanceof Size2f)
    {
      Size2f size = (Size2f) obj;
      return (width == size.width && height == size.height);
    }
    else
    {
      return super.equals(obj);
    }
  }
  
}
