package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.util.math.Shape;
import com.xenoage.zong.musiclayout.stampings.Stamping;
import com.xenoage.zong.renderer.RenderingParams;


/**
 * Mock class for a Stamping.
 *
 * @author Andreas Wenger
 */
public class StampingMock
  extends Stamping
{
  
  public StampingMock(Level level, Shape boundingShape)
  {
    super(null, level, null, boundingShape);
  }
  
  
  @Override public void paint(RenderingParams params)
  {
  }
  

}
