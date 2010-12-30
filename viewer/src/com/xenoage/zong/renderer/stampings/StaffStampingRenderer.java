package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenLine;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.util.Units;

import java.awt.Color;


/**
 * Renderer for a staff stamping.
 *
 * @author Andreas Wenger
 */
public class StaffStampingRenderer
{
  
	
  /**
	 * Paints the given staff stamping using the given rendering parameters.
	 */
  public static void paint(StaffStamping staff, RenderingParams params)
  {
    float scaling = params.getScaling();
    Point2f position = staff.getPosition();
    
    Point2i positionPx = new Point2i(
      Units.mmToPx(position.x, scaling),
      Units.mmToPx(position.y, scaling));
    
    //TODO: custom line width
    float lineWidthMm = staff.getLineWidth();
    
    //compute length in pixel (to minimize rounding errors, compute
    //right position and subtract already computed left position in px)
    //+1, because otherwise the line would stop one pixel before the right position
    int lengthPx = Units.mmToPx(position.x + staff.getLength(), scaling) - positionPx.x + 1;
    
    Color color = Color.black;
    
    if (params.getQuality() == RenderingQuality.Screen)
    {
      //render on screen
      ScreenLine screenLine = staff.getScreenInfo().getScreenLine(scaling, lineWidthMm, color);
      ScreenStaff ss = staff.getScreenInfo().getScreenStaff(scaling);
      positionPx = new Point2i(positionPx.x, positionPx.y + ss.getYOffsetPx());
      if (ss.isSimplifiedStaff())
      {
      	//simplified staff (fill rectangle)
      	color = screenLine.getColor();
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(),
          (int) (0.7f * color.getAlpha()));
        //don't forget the line heights, they belong into the rectangle
        positionPx = positionPx.add(0, -1 * Math.round(ss.getLineHeightPx() / 2));
        params.getRenderTarget().drawSimplifiedStaff(positionPx, lengthPx,
          ss.getHeightPx() + Math.round(ss.getLineHeightPx()), color);
      }
      else
      {
        //normal staff (draw lines)
        params.getRenderTarget().drawStaff(positionPx, lengthPx, staff.getLinesCount(),
          screenLine.getColor(), screenLine.getWidthPx(),
          Math.round(ss.getInterlineSpacePx()));
      }
    }
    else if (params.getQuality() == RenderingQuality.Print)
    {
      //render with high quality
      params.getRenderTarget().drawStaff(positionPx, lengthPx, staff.getLinesCount(),
        color, Units.mmToPx(lineWidthMm, scaling),
        Units.mmToPx(staff.getInterlineSpace(), scaling));
    }
    
  }
  
  
}
