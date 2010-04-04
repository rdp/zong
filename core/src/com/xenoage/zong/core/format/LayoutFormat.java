package com.xenoage.zong.core.format;


/**
 * Default formats for a layout.
 *
 * @author Andreas Wenger
 */
public final class LayoutFormat
{

  //page formats
  public enum Side { Left, Right, Both };
  private PageFormat leftPageFormat;
  private PageFormat rightPageFormat;
  
  
  /**
   * Creates a default layout format.
   */
  public LayoutFormat()
  {
    PageFormat pf = new PageFormat();
    leftPageFormat = pf;
    rightPageFormat = pf;
  }
  
  
  /**
   * Creates a new layout format as a copy of the given one.
   */
  public LayoutFormat(LayoutFormat template)
  {
    leftPageFormat = new PageFormat(template.leftPageFormat);
    rightPageFormat = new PageFormat(template.rightPageFormat);
  }
  
  
  /**
   * Creates a new layout format with the given
   * page format for both sides.
   */
  public LayoutFormat(PageFormat pageFormat)
  {
    leftPageFormat = new PageFormat(pageFormat);
    rightPageFormat = new PageFormat(pageFormat);
  }
  
  
  /**
   * Sets the format of the left, right or both pages.
   * @param side    Both, Left or Right 
   * @param format  the new page format
   */
  public void setPageFormat(Side side, PageFormat format)
    throws IllegalArgumentException
  {
    if (format == null)
      throw new IllegalArgumentException("format may not be null!");
    if (side == Side.Left || side == Side.Both)
      leftPageFormat = format;
    if (side == Side.Right || side == Side.Both)
      rightPageFormat = format;
  }
  
  
  /**
   * Gets a copy of the format of the given page.
   * @param side  Left or Right
   */
  public PageFormat getPageFormat(Side side)
    throws IllegalArgumentException
  {
    if (side == Side.Left)
      return new PageFormat(leftPageFormat);
    else if (side == Side.Right)
      return new PageFormat(rightPageFormat);
    else
      throw new IllegalArgumentException("Side must be left or right!");
  }
  
  
  /**
   * Gets a copy of the format of the given page.
   * @param side  Left or Right
   */
  public PageFormat getPageFormat(int pageIndex)
    throws IllegalArgumentException
  {
    if (pageIndex % 2 != 0)
      return new PageFormat(leftPageFormat);
    else
      return new PageFormat(rightPageFormat);
  }
  
  
}
