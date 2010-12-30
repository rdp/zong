package com.xenoage.zong.renderer.stampings;

import java.awt.Color;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.SystemCursorStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.util.Units;


/**
 * Renderer for a {@link SystemCursorStamping}.
 *
 * @author Andreas Wenger
 */
public class SystemCursorStampingRenderer
{


	/**
	 * Paints the given {@link SystemCursorStamping} using the given rendering parameters.
	 */
  public static void paint(SystemCursorStamping cursor, RenderingParams params)
  {
    float viewScaling = params.getScaling();
    StaffStamping topStaff = cursor.getTopStaff();
    StaffStamping bottomStaff = cursor.getBottomStaff();
    
    int x, y1, y2;
    x = Units.mmToPx(topStaff.getPosition().x + cursor.getPositionX(), viewScaling);
    
    if (params.getQuality() == RenderingQuality.Screen)
    {
    	int staffY = Units.mmToPx(topStaff.getPosition().y, viewScaling);
      ScreenStaff ss = topStaff.getScreenInfo().getScreenStaff(viewScaling);
      //top staff: top line
      y1 = staffY + ss.getLPPx(topStaff.getLinesCount() * 2); 
      //bottom staff: bottom line
      staffY = Units.mmToPx(bottomStaff.getPosition().y, viewScaling);
      ss = bottomStaff.getScreenInfo().getScreenStaff(viewScaling);
      y2 = staffY + ss.getLPPx(-2);
    }
    else
    {
      y1 = Units.mmToPx(topStaff.computeYMm(topStaff.getLinesCount() * 2), viewScaling);
      y2 = Units.mmToPx(bottomStaff.computeYMm(-2), viewScaling);
    }
    
    int cursorWidth = Math.max(2, Units.mmToPx(0.5f, viewScaling));
    
    params.getRenderTarget().drawLine(new Point2i(x, y1), new Point2i(x, y2), new Color(0.2f, 0.2f, 0.9f), cursorWidth);

  }
  
  
}
