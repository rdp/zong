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
  
  
  public PageMargins()
  {
    //default: 20 mm
    left = 20;
    right = 20;
    top = 20;
    bottom = 20;
  }
  
  
  public PageMargins(float left, float right, float top, float bottom)
  {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }
  
  
  public PageMargins(PageMargins template)
  {
    this.left = template.left;
    this.right = template.right;
    this.top = template.top;
    this.bottom = template.bottom;
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
