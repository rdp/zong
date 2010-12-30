package com.xenoage.zong.core.format;

import com.xenoage.util.math.Size2f;


/**
 * Class for a page format, containing
 * width and height in mm and margins.
 *
 * @author Andreas Wenger
 */
public final class PageFormat
{
  
  private final Size2f size;
  private final PageMargins margins;
  
  //default: DIN A4
  public static final PageFormat defaultValue = new PageFormat(
  	new Size2f(210, 297), PageMargins.defaultValue);
  
  
  public PageFormat(Size2f size, PageMargins margins)
  {
    this.size = size;
    this.margins = margins;
  }

  
  public Size2f getSize()
  {
    return size;
  }


  public PageMargins getMargins()
  {
    return margins;
  }
  
  
  @Override public boolean equals(Object obj)
  {
    if (obj instanceof PageFormat)
    {
      PageFormat other = (PageFormat) obj;
      return (other.size.equals(size) &&
        other.margins.equals(margins));
    }
    else
    {
      return false;
    }
  }
  
  
  /**
   * Gets the usable width of the page. This is the
   * horizontal size minus the left and right margin.
   */
  public float getUseableWidth()
  {
    return size.width - margins.left - margins.right;
  }
  
  
  /**
   * Gets the usable height of the page. This is the
   * vertical size minus the top and bottom margin.
   */
  public float getUseableHeight()
  {
    return size.height - margins.top - margins.bottom;
  }
  
  
  @Override public String toString()
  {
    return "PageFormat: (" + size.width + " x " + size.height + "),(" +
      margins.left + ", " + margins.right + ", " +
      margins.top + ", " + margins.bottom + ")";
  }

}
