package com.xenoage.zong.data;

import com.xenoage.zong.data.music.BracketGroupStyle;


/**
 * This is a group of staves with a
 * given bracket style and a name (TODO).
 *
 * @author Andreas Wenger
 */
public class BracketGroup
  extends StavesGroup
{
  
  //style of the bracket
  private BracketGroupStyle style;
  
  
  BracketGroup(int startIndex, int endIndex,
    BracketGroupStyle style)
  {
    super(startIndex, endIndex);
    this.style = style;
  }
  
  
  /**
   * Gets the style of this bracket group.
   */
  public BracketGroupStyle getStyle()
  {
    return style;
  }
  
}
