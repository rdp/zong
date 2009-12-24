package com.xenoage.zong.renderer.frames;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.musiclayout.ScoreFrameLayout;
import com.xenoage.zong.musiclayout.stampings.Stamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.zong.renderer.printer.PrinterRenderTarget;


/**
 * Swing renderer for a score frame.
 * 
 * @author Andreas Wenger
 */
public class SwingScoreFrameRenderer
  extends SwingFrameRenderer
{
  
  private static SwingScoreFrameRenderer instance = null;
  
  
  public static SwingScoreFrameRenderer getInstance()
  {
    if (instance == null)
      instance = new SwingScoreFrameRenderer();
    return instance;
  }
  
  
  private SwingScoreFrameRenderer()
  {
  }
  
  
  /**
   * Paints the given Frame with the given OpenGL context.
   * This method is called by the paint-method, the transformations
   * are alread done.
   */
  @Override protected void paintTransformed(Frame frame,
    SwingGraphicsContext context)
  {
    Graphics2D g2d = context.getGraphics2D();
    
    float w = frame.getSize().width;
    float h = frame.getSize().height;
    
    //if there is a background, draw it
    if (frame.getBackground() != null)
    {
      Paint background = frame.getBackground().getPaint();
      g2d.setPaint(background);
      g2d.fill(new Rectangle2D.Float(-w/2, -h/2, w, h));
    }
    
    //draw musical elements
    ScoreFrame scoreFrame = (ScoreFrame) frame;
    ScoreFrameLayout layout = scoreFrame.getLayout();
    if (layout != null)
    {
      //the coordinates of the layout elements are relative to the upper left
      //corner, so we have to translate them
      g2d.translate(-w / 2, -h / 2);
      //scale the coordinates by a high factor. this results
      //in much better resolution of the painting (TODO: do
      //this for the whole printing?)
      float highScale = 1000;
      g2d.scale(1f / highScale, 1f / highScale);
      float scaling = highScale * context.getScaling() / (720f / 254f); //TIDY
      RenderingParams renderingParams = new RenderingParams(
        PrinterRenderTarget.getInstance(context), scaling, RenderingQuality.Print);
      for (Stamping s : layout.getMusicalStampings())
      {
        s.paint(renderingParams);
      }
    }
  }


}
