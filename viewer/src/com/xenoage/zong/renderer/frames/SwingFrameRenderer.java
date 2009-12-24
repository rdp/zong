package com.xenoage.zong.renderer.frames;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.util.Units;


/**
 * Abstract base class for frame renderers working with Swing.
 * These renderers are not optimized for performance, since
 * they should be used for printing only.
 * 
 * @author Andreas Wenger
 */
public abstract class SwingFrameRenderer
{
  
  /**
   * Paints the given frame with the given Swing context.
   */
  public void paint(Frame frame, SwingGraphicsContext context)
  {
    Graphics2D g2d = context.getGraphics2D();
    //backup old transformation
    AffineTransform oldTransform = g2d.getTransform();
    //apply scaling
    double scaling = Units.mmToPxFloat(context.getScaling(), 1);
    g2d.scale(scaling, scaling);
    //apply translation: absolute position in layout space
    Point2f pos = frame.getAbsolutePosition();
    g2d.translate(pos.x, pos.y);
    //apply rotation
    g2d.rotate(-frame.getAbsoluteRotation() * Math.PI / 180f);
    
    //DEMO
    /*
    g2d.setColor(java.awt.Color.green);
    g2d.setStroke(new java.awt.BasicStroke(1));
    float w = frame.getSize().width;
    float h = frame.getSize().height;
    g2d.draw(new java.awt.geom.Rectangle2D.Float(-w/2, -h/2, w, h));
    //*/
    
    //paint the frame
    paintTransformed(frame, context);
    
    //restore old transformation
    g2d.setTransform(oldTransform);
  }
  
  
  /**
   * Paint the given frame with the given Swing context.
   * This method is called by the paint-method, the transformations
   * are alread done.
   */
  protected abstract void paintTransformed(Frame frame,
    SwingGraphicsContext context);
  
  

}
