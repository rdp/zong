package com.xenoage.zong.data.format;

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
  
  
  public PageFormat()
  {
    //default: DIN A4
    size = new Size2f(210, 297);
    margins = new PageMargins();
  }
  
  
  public PageFormat(Size2f size, PageMargins margins)
  {
    this.size = new Size2f(size);
    this.margins = new PageMargins(margins);
  }
  
  
  /**
   * Creates a new page format as a copy of the given one.
   */
  public PageFormat(PageFormat template)
  {
    this(new Size2f(template.size), new PageMargins(template.margins));
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
      return super.equals(obj);
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
