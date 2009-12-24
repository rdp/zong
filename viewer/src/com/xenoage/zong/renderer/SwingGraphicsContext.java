package com.xenoage.zong.renderer;

import java.awt.Graphics2D;


/**
 * This class contains an instance
 * of a Swing graphics context.
 *
 * @author Andreas Wenger
 */
public class SwingGraphicsContext
  implements GraphicsContext
{
  
  private Graphics2D g;
  private float scaling;
  
  
  public SwingGraphicsContext(Graphics2D g, float scaling)
  {
    this.g = g;
    this.scaling = scaling;
  }
  
  
  public Graphics2D getGraphics2D()
  {
    return g;
  }

  
  public float getScaling()
  {
    return scaling;
  }

}
