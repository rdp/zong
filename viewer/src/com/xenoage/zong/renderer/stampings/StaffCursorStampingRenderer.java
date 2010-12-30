package com.xenoage.zong.renderer.stampings;

import java.awt.Color;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.musiclayout.stampings.StaffCursorStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.util.Units;


/**
 * Renderer for a cursor stamping
 * that belongs to one staff.
 *
 * @author Andreas Wenger
 */
public class StaffCursorStampingRenderer
{


	/**
	 * Paints the given {@link StaffCursorStamping} using the given rendering parameters.
	 */
  public static void paint(StaffCursorStamping cursor, RenderingParams params)
  {
    float viewScaling = params.getScaling();
    StaffStamping parentStaff = cursor.getParentStaff();
    
    int x, y1, y2;
    if (params.getQuality() == RenderingQuality.Screen)
    {
    	int staffY = Units.mmToPx(parentStaff.getPosition().y, viewScaling);
      ScreenStaff ss = parentStaff.getScreenInfo().getScreenStaff(viewScaling);
      //top: one interline space above staff
      y1 = staffY + ss.getLPPx(parentStaff.getLinesCount() * 2); 
      //bottom: one interline space under staff
      y2 = staffY + ss.getLPPx(-2);
    }
    else
    {
      y1 = Units.mmToPx(
        parentStaff.computeYMm(parentStaff.getLinesCount() * 2),
        viewScaling);
      y2 = Units.mmToPx(
        parentStaff.computeYMm(-2),
        viewScaling);
    }
    x = Units.mmToPx(
      parentStaff.getPosition().x + cursor.getPositionX() +
      	cursor.getOffsetSpaces() * parentStaff.getInterlineSpace(),
      viewScaling);
    
    int cursorWidth = Math.max(2, Units.mmToPx(0.5f, viewScaling));
    
    params.getRenderTarget().drawLine(new Point2i(x, y1), new Point2i(x, y2), new Color(0.2f, 0.9f, 0.2f), cursorWidth);

  }
  
  
}
