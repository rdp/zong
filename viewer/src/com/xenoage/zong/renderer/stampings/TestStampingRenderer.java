package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.annotations.Demo;
import com.xenoage.util.math.*;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.util.Units;

import java.awt.Color;


/**
 * Renderer for a test stamping,
 * used for testing purposes.
 * 
 * Draws the border of a rectangle with
 * a given color.
 *
 * @author Andreas Wenger
 */
@Demo public class TestStampingRenderer
{


  public static void paint(Point2f position,
    Size2f size, Color color, RenderingParams params)
  {
    float scaling = params.getScaling();
    
    Point2i pNW = new Point2i(
      Units.mmToPx(position.x, scaling),
      Units.mmToPx(position.y, scaling));
    Point2i pSE = new Point2i(
      Units.mmToPx(position.x + size.width, scaling),
      Units.mmToPx(position.y + size.height, scaling));
    Point2i pNE = new Point2i(pSE.x, pNW.y);
    Point2i pSW = new Point2i(pNW.x, pSE.y);
    
    params.getRenderTarget().drawLine(pNW, pNE, color, 1);
    params.getRenderTarget().drawLine(pNE, pSE, color, 1);
    params.getRenderTarget().drawLine(pSE, pSW, color, 1);
    params.getRenderTarget().drawLine(pSW, pNW, color, 1);
  }
  
  
}
