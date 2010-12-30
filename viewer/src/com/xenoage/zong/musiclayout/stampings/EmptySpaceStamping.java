package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.util.math.Rectangle2f;
import com.xenoage.zong.renderer.RenderingParams;


/**
 * Layout element for empty space, which
 * means the blank paper.
 *
 * @author Andreas Wenger
 */
public class EmptySpaceStamping
  extends Stamping
{
  
  
  public EmptySpaceStamping(Rectangle2f rectangle)
  {
    super(null, Level.EmptySpace, null, rectangle);
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    //nothing to do
  }
  
  
}
