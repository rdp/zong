package com.xenoage.zong.renderer.frames;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.renderer.SwingGraphicsContext;


/**
 * Swing renderer for a group frame.
 * 
 * @author Andreas Wenger
 */
public class SwingGroupFrameRenderer
  extends SwingFrameRenderer
{
  
  private static SwingGroupFrameRenderer instance = null;
  
  
  public static SwingGroupFrameRenderer getInstance()
  {
    if (instance == null)
      instance = new SwingGroupFrameRenderer();
    return instance;
  }
  
  
  private SwingGroupFrameRenderer()
  {
  }
  
  
  /**
   * Paints the given GroupFrame with the given Swing context.
   * This method is called by the paint-method, the transformations
   * are alread done.
   */
  @Override protected void paintTransformed(Frame frame,
    SwingGraphicsContext context)
  {
    Graphics2D g2d = context.getGraphics2D();
    //if there is a background, draw it
    if (frame.getBackground() != null)
    {
      Paint background = frame.getBackground().getPaint();
      g2d.setPaint(background);
      float x = frame.getSize().width;
      float y = frame.getSize().height;
      g2d.fill(new Rectangle2D.Float(-x/2, -y/2, x, y));
    }
  }


}
