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
  private final PageFormat leftPageFormat;
  private final PageFormat rightPageFormat;
  
  public static final LayoutFormat defaultValue = new LayoutFormat(
  	PageFormat.defaultValue, PageFormat.defaultValue);
  
  
  /**
   * Creates a new layout format with the given
   * page format for both sides.
   */
  public LayoutFormat(PageFormat leftPageFormat, PageFormat rightPageFormat)
  {
    this.leftPageFormat = leftPageFormat;
    this.rightPageFormat = rightPageFormat;
  }
  
  
  /**
   * Sets the format of the left, right or both pages.
   * @param side    Both, Left or Right 
   * @param format  the new page format
   */
  public LayoutFormat withPageFormat(Side side, PageFormat format)
    throws IllegalArgumentException
  {
  	if (format == null)
      throw new IllegalArgumentException("format may not be null!");
  	PageFormat leftPageFormat = this.leftPageFormat;
  	PageFormat rightPageFormat = this.rightPageFormat;
    if (side == Side.Left || side == Side.Both)
      leftPageFormat = format;
    if (side == Side.Right || side == Side.Both)
      rightPageFormat = format;
    return new LayoutFormat(leftPageFormat, rightPageFormat);
  }
  
  
  /**
   * Gets the format of the given page.
   * @param side  Left or Right
   */
  public PageFormat getPageFormat(Side side)
    throws IllegalArgumentException
  {
    if (side == Side.Left)
      return leftPageFormat;
    else if (side == Side.Right)
      return rightPageFormat;
    else
      throw new IllegalArgumentException("Side must be left or right!");
  }
  
  
  /**
   * Gets the format of the given page.
   * @param pageIndex  the 0-based index of the page
   */
  public PageFormat getPageFormat(int pageIndex)
    throws IllegalArgumentException
  {
    if (pageIndex % 2 != 0)
      return leftPageFormat;
    else
      return rightPageFormat;
  }
  
  
}
