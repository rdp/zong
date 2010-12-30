package com.xenoage.zong.musiclayout.stampings;

import java.awt.Color;

import com.xenoage.util.annotations.Demo;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.TestStampingRenderer;


/**
 * A {@link TestStamping} is the border of a rectangle
 * at the given position with the given size in a given color.
 *
 * @author Andreas Wenger
 */
@Demo public final class TestStamping
  extends Stamping
{

  private final Point2f position;
  private final Size2f size;
  private final Color color;
  
  
  public TestStamping(Point2f position,
    Size2f size, Color color)
  {
    super(null, Level.Music, null, null);
    this.position = position;
    this.size = size;
    this.color = color;
  }
  
  
  /**
   * Paints this layout element using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    TestStampingRenderer.paint(position, size, color, params);
  }
  
}
