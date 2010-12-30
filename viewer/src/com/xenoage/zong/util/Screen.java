package com.xenoage.zong.util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;


/**
 * Functions to get the screen resolution,
 * color depth and so on.
 *
 * @author Andreas Wenger
 */
public class Screen
{
  
  private static BufferedImage image = null;
  
  
  /**
   * Gets the screen's graphics configuration.
   */
  public static GraphicsConfiguration getGraphicsConfiguration()
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gs = ge.getDefaultScreenDevice();
    return gs.getDefaultConfiguration();
  }
  
  
  /**
   * Gets a font render context.
   */
  public static FontRenderContext getFontRenderContext()
  {
    if (image == null)
    {
      image = getGraphicsConfiguration().createCompatibleImage(2, 2);
    }
    Graphics2D g2d = (Graphics2D) image.getGraphics();
    return g2d.getFontRenderContext();
  }
  
  
  private Screen()
  {
  }

}
