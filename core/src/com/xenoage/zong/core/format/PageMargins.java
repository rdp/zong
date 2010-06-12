package com.xenoage.zong.core.format;


/**
 * Class for page margins in mm:
 * left, right, top and bottom.
 *
 * @author Andreas Wenger
 */
public final class PageMargins
{
  
  public final float left, right, top, bottom;
  
  //default: 20 mm
  public static final PageMargins defaultValue = new PageMargins(20, 20, 20, 20);
  
  
  public PageMargins(float left, float right, float top, float bottom)
  {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }
  
  
  @Override public boolean equals(Object obj)
  {
    if (obj instanceof PageMargins)
    {
      PageMargins other = (PageMargins) obj;
      return (
        other.left == left &&
        other.right == right &&
        other.top == top &&
        other.bottom == bottom);
    }
    else
    {
      return false;
    }
  }

}
