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
  
  public StampingMock(int level)
  {
    super(level, null);
  }
  
  
  @Override public void addBoundingShape(Shape boundingShape)
  {
    super.addBoundingShape(boundingShape);
  }
  
  
  @Override public void paint(RenderingParams params)
  {
  }
  

}
