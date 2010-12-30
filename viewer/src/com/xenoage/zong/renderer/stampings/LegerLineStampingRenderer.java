package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.musiclayout.stampings.LegerLineStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenLine;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.util.Units;

import java.awt.Color;


/**
 * Renderer for a leger line stamping.
 * 
 * Leger lines belong to a staff. They have
 * a horizontal position around which they
 * are centered. They are 2 spaces long.
 *
 * @author Andreas Wenger
 */
public class LegerLineStampingRenderer
{


	/**
	 * Paints the given leger line stamping using the given rendering parameters.
	 */
  public static void paint(LegerLineStamping legerLine, RenderingParams params)
  {
  	StaffStamping parentStaff = legerLine.getParentStaff();
  	float linePosition = legerLine.getLinePosition();
  	float positionX = legerLine.getPositionX();
  	
    float scaling = params.getScaling();
    int interlineSpacePx = Units.mmToPx(
      parentStaff.getInterlineSpace(), scaling);
    int width = (int)(1.6f * interlineSpacePx);
    Point2i p1Px = new Point2i(
      Units.mmToPx(positionX, scaling) - width/2,
      Units.mmToPx(parentStaff.getPosition().y, scaling));
    Point2i p2Px = new Point2i(
      p1Px.x + width,
      p1Px.y);
    float lineWidthMm = parentStaff.getLineWidth();
    Color color = Color.black;
    
    if (params.getQuality() == RenderingQuality.Screen)
    {
      //render on screen or print
      ScreenLine screenLine = new ScreenLine(lineWidthMm, color, scaling);
      ScreenStaff screenStaff = parentStaff.getScreenInfo().getScreenStaff(scaling);
      p1Px = new Point2i(p1Px.x, p1Px.y + screenStaff.getLPPx(linePosition));
      p2Px = new Point2i(p2Px.x, p1Px.y);
      params.getRenderTarget().drawLine(p1Px, p2Px,
        screenLine.getColor(), screenLine.getWidthPx());
    }
    else if (params.getQuality() == RenderingQuality.Print)
    {
      //render with high quality
    	p1Px = new Point2i(p1Px.x, Units.mmToPx(parentStaff.computeYMm(linePosition), scaling));
      p2Px = new Point2i(p2Px.x, p1Px.y);
      int lineWidthPx = Units.mmToPx(lineWidthMm, scaling);
      params.getRenderTarget().drawLine(p1Px, p2Px,
        color, lineWidthPx);
    }
    
  }
  
  
}
