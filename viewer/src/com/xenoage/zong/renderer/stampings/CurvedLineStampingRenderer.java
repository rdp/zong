package com.xenoage.zong.renderer.stampings;

import java.awt.Color;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.musiclayout.stampings.CurvedLineStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.util.Units;


/**
 * Renderer for a {@link CurvedLineStamping}.
 *
 * @author Andreas Wenger
 */
public class CurvedLineStampingRenderer
{

	
	/**
	 * Paints the given {@link CurvedLineStamping} using the given rendering parameters.
	 */
  public static void paint(CurvedLineStamping slur, RenderingParams params)
  {
    float scaling = params.getScaling();
    StaffStamping parentStaff = slur.getParentStaff();
    
    //compute absolute coordinates in px
    float p1xPx = Units.mmToPxFloat(slur.getPoint1().xMm, scaling);
    float p2xPx = Units.mmToPxFloat(slur.getPoint2().xMm, scaling);
    float c1xPx = p1xPx + Units.mmToPxFloat(slur.getControl1().xMm, scaling);
    float c2xPx = p2xPx + Units.mmToPxFloat(slur.getControl2().xMm, scaling);
    float p1yPx = 0, p2yPx = 0, c1yPx = 0, c2yPx = 0;
    if (params.getQuality() == RenderingQuality.Screen)
    {
      float staffYPos = Units.mmToPxFloat(parentStaff.getPosition().y, scaling);
      ScreenStaff screenStaff = parentStaff.getScreenInfo().getScreenStaff(scaling);
      float bottomLinePx = staffYPos + screenStaff.getLP0Px();
      float isPx = screenStaff.getInterlineSpacePx();
      p1yPx = bottomLinePx - isPx * slur.getPoint1().yLp / 2;
      p2yPx = bottomLinePx - isPx * slur.getPoint2().yLp / 2;
      c1yPx = p1yPx - isPx * slur.getControl1().yLp / 2;
      c2yPx = p2yPx - isPx * slur.getControl2().yLp / 2;
    } 
    else if (params.getQuality() == RenderingQuality.Print)
    {
    	p1yPx = Units.mmToPxFloat(parentStaff.computeYMm(slur.getPoint1().yLp), scaling);
      p2yPx = Units.mmToPxFloat(parentStaff.computeYMm(slur.getPoint2().yLp), scaling);
      c1yPx = Units.mmToPxFloat(parentStaff.computeYMm(slur.getPoint1().yLp + slur.getControl1().yLp), scaling);
      c2yPx = Units.mmToPxFloat(parentStaff.computeYMm(slur.getPoint2().yLp + slur.getControl2().yLp), scaling);
    }
    
    Point2f p1 = new Point2f(p1xPx, p1yPx);
    Point2f p2 = new Point2f(p2xPx, p2yPx);
    Point2f c1 = new Point2f(c1xPx, c1yPx);
    Point2f c2 = new Point2f(c2xPx, c2yPx);
    
    Color color = Color.BLACK;
    
    /* //TEST
    Point2i lastPoint = new Point2i(MathTools.bezier(p1, p2, c1, c2, 0));
    for (int i = 1; i <= iterations; i++)
    {
    	float t = 1f * i / iterations;
    	float width = 0.7f + (0.5f - Math.abs(t - 0.5f)) * 2.5f;
    	Point2i p = new Point2i(MathTools.bezier(p1, p2, c1, c2, t));
      params.getRenderTarget().drawLine(lastPoint, p, color, MathTools.clampMin((int) (scaling * width), 1));
      lastPoint = p;
    } */
    
    float isPx = Units.mmToPxFloat(parentStaff.getInterlineSpace(), scaling);
    
    params.getRenderTarget().drawCurvedLine(p1, p2, c1, c2, isPx, color);
    
  }
  
  
}
