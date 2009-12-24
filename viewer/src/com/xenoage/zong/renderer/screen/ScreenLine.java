package com.xenoage.zong.renderer.screen;

import com.xenoage.util.MathTools;
import com.xenoage.util.Units;

import java.awt.Color;


/**
 * This class helps drawing nice
 * lines on the screen.
 * 
 * Because the screen works with integer
 * coordinates, no lines thinner than 1px
 * can be drawn.
 * 
 * This class computes the best possible
 * display of lines on the screen: It ensures
 * that each line is at least 1 px wide, but
 * is drawn with some transparency to
 * fake the "thinner"-effect.
 *
 * @author Andreas Wenger
 */
public class ScreenLine
{
  
  private int widthPxResult;
  private Color colorResult;
  
  
  /**
   * Creates a ScreenLine with the given width in mm,
   * the given color and the given scaling.
   */
  public ScreenLine(float width, Color color, float scaling)
  {
    float widthPxFloat = Units.mmToPxFloat(width, scaling);
    widthPxResult = MathTools.clampMin(Math.round(widthPxFloat), 1);
    colorResult = new Color(
      color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    if (widthPxFloat < 1)
    {
      colorResult = new Color(
        colorResult.getRed(), colorResult.getGreen(), colorResult.getBlue(),
        (int) (colorResult.getAlpha() * widthPxFloat));
    }
  }
  
  
  /**
   * Gets the width in px,
   * that fits best to the screen.
   */
  public int getWidthPx()
  {
    return widthPxResult;
  }
  
  
  /**
   * Gets the Color,
   * that fits best to the screen.
   */
  public Color getColor()
  {
    return colorResult;
  }

}
