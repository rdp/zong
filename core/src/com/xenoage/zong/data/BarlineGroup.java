package com.xenoage.zong.data;

import com.xenoage.zong.data.music.barline.BarlineGroupStyle;


/**
 * This is a group of staves with a
 * given barline group style.
 *
 * @author Andreas Wenger
 */
public class BarlineGroup
  extends StavesGroup
{
  
  //style of the barlines
  private BarlineGroupStyle style;
  
  
  /**
   * Creates a new barline group between the given
   * staff indices with the given style.
   */
  BarlineGroup(int startIndex, int endIndex, BarlineGroupStyle style)
  {
    super(startIndex, endIndex);
    this.style = style;
  }


  /**
   * Gets the style of this barline group.
   */
  public BarlineGroupStyle getStyle()
  {
    return style;
  }
  
}
