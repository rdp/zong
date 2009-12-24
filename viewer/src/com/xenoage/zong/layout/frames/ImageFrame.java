package com.xenoage.zong.layout.frames;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.zong.renderer.frames.GLImageFrameRenderer;
import com.xenoage.zong.renderer.frames.SwingImageFrameRenderer;


/**
 * A image frame is a frame that contains
 * a bitmap image.
 * 
 * @author Andreas Wenger
 */
public class ImageFrame
  extends Frame
{
  
  private String imagePath;
  
  
  /**
   * Creates a new group frame.
   */
  public ImageFrame(Point2f position, Size2f size, String imagePath)
  {
    super(position, size);
    this.imagePath = imagePath;
  }
  
  
  /**
   * Paints this frame with the given OpenGL context.
   */
  @Override public void paint(GLGraphicsContext context)
  {
    //paint this frame
    GLImageFrameRenderer.getInstance().paint(this, context);
  }
  
  
  /**
   * Paints this frame with the given Swing context.
   */
  @Override public void paint(SwingGraphicsContext context)
  {
    //paint this frame
    SwingImageFrameRenderer.getInstance().paint(this, context);
  }


  /**
   * Gets the path of this image.
   */
  public String getImagePath()
  {
    return imagePath;
  }
  

}
