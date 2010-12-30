package com.xenoage.zong.renderer.frames;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.ImageFrame;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.util.io.IO;
import com.xenoage.util.logging.Log;


/**
 * Swing renderer for an image frame.
 * 
 * Notice that this renderer is not optimized
 * for performance in any way. It loads an image
 * each time it is painted! This renderer should
 * only be used for printing.
 * 
 * @author Andreas Wenger
 */
public class SwingImageFrameRenderer
  extends SwingFrameRenderer
{
  
  private static SwingImageFrameRenderer instance = null;
  
  
  public static SwingImageFrameRenderer getInstance()
  {
    if (instance == null)
      instance = new SwingImageFrameRenderer();
    return instance;
  }
  
  
  private SwingImageFrameRenderer()
  {
  }
  
  
  /**
   * Paints the given ImageFrame with the given Swing context.
   * This method is called by the paint-method, the transformations
   * are alread done.
   * This method is very slow, because the image is loaded
   * before painting.
   */
  @Override protected void paintTransformed(Frame frame,
    SwingGraphicsContext context)
  {
    Graphics2D g2d = context.getGraphics2D();
    //try to load the texture
    ImageFrame imageFrame = (ImageFrame) frame;
    BufferedImage image = null;
    try
    {
      image = ImageIO.read(IO.openInputStream(imageFrame.getImagePath()));
    }
    catch (IOException ex)
    {
      Log.log(Log.WARNING, this,
        "Could not load image: " + imageFrame.getImagePath());
    }
    //if image could be loaded, paint it
    if (image != null)
    {
      //create transformation, that maps the image
      //to the frame borders
      AffineTransform transform = new AffineTransform();
      float x = frame.getSize().width;
      float y = frame.getSize().height;
      transform.translate(-x/2, -y/2);
      transform.scale(x / image.getWidth(), y / image.getHeight());
      //draw image with this transform
      g2d.drawImage(image, transform, null);
    }
  }


}
