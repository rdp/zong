package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.WedgeStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenLine;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.util.Units;

import java.awt.Color;

import javax.media.opengl.GL;


/**
 * Renderer for the {@link WedgeStamping} class.
 *
 * @author Andreas Wenger
 */
public class WedgeStampingRenderer
{


	/**
	 * Paints the given {@link WedgeStamping} using the given rendering parameters.
	 */
  public static void paint(WedgeStamping wedge, RenderingParams params)
  {
  	StaffStamping parentStaff = wedge.getParentStaff();
    float scaling = params.getScaling();
    
    //horizontal position
    int x1Px = Units.mmToPx(wedge.getX1Mm() + parentStaff.getPosition().x, scaling);
    int x2Px = Units.mmToPx(wedge.getX2Mm() + parentStaff.getPosition().x, scaling);
    
    //compute vertical distances at the start and end point
    int d1Px = Units.mmToPx(wedge.getD1Is() * parentStaff.getInterlineSpace(), scaling);
    int d2Px = Units.mmToPx(wedge.getD2Is() * parentStaff.getInterlineSpace(), scaling);
    
    //width and color of the line
    Color color = Color.black;
    float width = parentStaff.getLineWidth(); //like staff line
    int paintWidth;
    
    //compute the horizontal line and color
    int yPx;
    Color paintColor;
    if (params.getQuality() == RenderingQuality.Screen)
    {
      ScreenStaff ss = parentStaff.getScreenInfo().getScreenStaff(scaling);
      yPx = Units.mmToPx(parentStaff.getPosition().y, scaling) + ss.getLPPx(wedge.getYLp());
      ScreenLine screenLine = parentStaff.getScreenInfo().getScreenLine(scaling, width, color);
      paintColor = screenLine.getColor();
      paintWidth = screenLine.getWidthPx();
    }
    else
    {
      yPx = Units.mmToPx(parentStaff.computeYMm(wedge.getYLp()), scaling);
      paintColor = color;
      paintWidth = Units.mmToPx(width, scaling);
    }
    
    //draw lines
    params.getRenderTarget().drawLine(
    	new Point2i(x1Px, yPx - d1Px / 2), new Point2i(x2Px, yPx - d2Px / 2), paintColor, paintWidth);
    params.getRenderTarget().drawLine(
    	new Point2i(x1Px, yPx + d1Px / 2), new Point2i(x2Px, yPx + d2Px / 2), paintColor, paintWidth);
    
  }
  
  
}
