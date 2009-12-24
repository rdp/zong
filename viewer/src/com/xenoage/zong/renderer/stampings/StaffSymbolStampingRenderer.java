package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.data.music.format.SP;
import com.xenoage.util.Units;

import java.awt.Color;


/**
 * Renderer for symbol stampings
 * that belong to a staff.
 *
 * @author Andreas Wenger
 */
public class StaffSymbolStampingRenderer
{


	/**
	 * Paints the given symbol using the given information and rendering parameters.
	 */
  public static void paint(Symbol symbol, Color color, SP position, 
  	float scaling, StaffStamping parentStaff, boolean mirrorV,
  	RenderingParams params)
  {
    float viewScaling = params.getScaling();
    float symbolScaling = Units.mmToPxFloat(
      scaling * parentStaff.getInterlineSpace(), viewScaling);
    
    Point2f positionPx;
    if (params.getQuality() == RenderingQuality.Screen)
    {
      ScreenStaff ss = parentStaff.getScreenInfo().getScreenStaff(viewScaling);
      float yPosition = Units.mmToPx(parentStaff.getPosition().y, viewScaling) +
      	ss.getLPPxCorrected(position.yLp);
      positionPx = new Point2f(
        Units.mmToPxFloat(position.xMm, viewScaling), yPosition);
      symbolScaling *= ss.getHeightScaling();
      
      /*
      //TEST
      int x = Units.mmToPx(positionX, viewScaling);
      int yOffset = ss.getYOffsetPx();
      int yTopLine = Units.mmToPx(
        parentStaff.getPosition().y + parentStaff.getLineWidth() / 2,
        viewScaling) + yOffset;
      for (int i = 0; i < 2; i++)
	      params.getRenderTarget().drawLine(
	      	new Point2i(x + i, yTopLine),
	      	new Point2i(x + i, yTopLine + ss.getHeightPx()), Color.red, 1); */
    }
    else
    {
      float yPosition = Units.mmToPxFloat(
        parentStaff.computeYMm(position.yLp),
        viewScaling);
      positionPx = new Point2f(
        Units.mmToPx(position.xMm, viewScaling), yPosition);
    }
    
    params.getRenderTarget().drawSymbol(
      symbol, (color != null ? color : Color.black), positionPx,
      new Point2f(symbolScaling, (mirrorV ? -1 : 1) * symbolScaling));
  }
  
  
}
