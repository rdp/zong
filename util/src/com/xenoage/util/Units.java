package com.xenoage.util;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;


/**
 * Some functions to convert values into
 * different units.
 *
 * @author Andreas Wenger
 */
public class Units
{
	
	public static final float mmToPx_1_1 = 1 * 72.0f / 25.4f * 1;
	
  
  /**
   * Converts the given mm into pixels,
   * respecting the given scaling factor
   * relative to 72 dpi.
   */
  public static int mmToPx(float mm, float scaling)
  {
    return Math.round(mmToPxFloat(mm, scaling));
  }
  
  
  /**
   * Converts the given pixels into mm,
   * respecting the given scaling factor
   * relative to 72 dpi.
   */
  public static float pxToMm(float px, float scaling)
  {
    return px / 720f * 254f / scaling;
  }
  
  
  /**
   * Converts the given mm into pixels,
   * respecting the given scaling factor
   * relative to 72 dpi.
   */
  public static float mmToPxFloat(float mm, float scaling)
  {
    return mm * 72.0f / 25.4f * scaling;
  }
  
  
  /**
   * Converts the given mm into pixels,
   * respecting the given scaling factor
   * relative to 72 dpi.
   */
  public static Point2i mmToPx(Point2f pMm, float scaling)
  {
    return new Point2i(mmToPx(pMm.x, scaling), mmToPx(pMm.y, scaling));
  }
  
  
  /**
   * Converts the given pixels into mm,
   * respecting the given scaling factor
   * relative to 72 dpi.
   */
  public static Point2f pxToMm(Point2i px, float scaling)
  {
    return new Point2f(pxToMm(px.x, scaling), pxToMm(px.y, scaling));
  }
  
  
  private Units()
  {
  }

}
