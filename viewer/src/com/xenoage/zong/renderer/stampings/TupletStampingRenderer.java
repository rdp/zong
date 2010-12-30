package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.TupletStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenLine;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.util.Units;

import java.awt.Color;


/**
 * Renderer for the {@link TupletStamping} class.
 *
 * @author Andreas Wenger
 */
public class TupletStampingRenderer
{


	/**
	 * Paints the given {@link TupletStamping} using the given rendering parameters.
	 */
  public static void paint(TupletStamping tuplet, RenderingParams params)
  {
  	StaffStamping parentStaff = tuplet.getParentStaff();
    float scaling = params.getScaling();
    
    //horizontal position
    int x1px = Units.mmToPx(tuplet.getX1mm(), scaling);
    int x2px = Units.mmToPx(tuplet.getX2mm(), scaling);
    
    //height of hook is 1 IS
    int hookHeightPx = Units.mmToPx(parentStaff.getInterlineSpace(), scaling);
    
    //width and color of the line
    Color color = Color.black;
    float width = parentStaff.getLineWidth() * 1.5f; //a little bit thicker than staff line
    int paintWidth;
    
    //compute the horizontal line and color
    int y1px, y2px;
    Color paintColor;
    if (params.getQuality() == RenderingQuality.Screen)
    {
      ScreenStaff ss = parentStaff.getScreenInfo().getScreenStaff(scaling);
      y1px = Units.mmToPx(parentStaff.getPosition().y, scaling) + ss.getLPPx(tuplet.getY1lp());
      y2px = Units.mmToPx(parentStaff.getPosition().y, scaling) + ss.getLPPx(tuplet.getY2lp());
      ScreenLine screenLine = parentStaff.getScreenInfo().getScreenLine(scaling, width, color);
      paintColor = screenLine.getColor();
      paintWidth = screenLine.getWidthPx();
    }
    else
    {
    	y1px = Units.mmToPx(parentStaff.computeYMm(tuplet.getY1lp()), scaling);
    	y2px = Units.mmToPx(parentStaff.computeYMm(tuplet.getY2lp()), scaling);
      paintColor = color;
      paintWidth = Units.mmToPx(width, scaling);
    }
    
    //compute gap for text
    FormattedText text = tuplet.getText();
    float gapMm = 0;
    float textMm = 0;
    if (text != null && text.getParagraphsCount() > 0)
    {
    	textMm = text.getParagraphs().next().getWidthMm();
    	gapMm = textMm * 2;
    }
    
    //draw line and hooks
    if (gapMm > 0)
    {
    	//two lines, when there is text in between
    	int gapPx = Units.mmToPx(gapMm, scaling);
    	int xGapLPx = (x2px + x1px) / 2 - gapPx / 2;
    	int xGapRPx = xGapLPx + gapPx;
    	int gapVerticalPx = (int) (1f * gapPx / (x2px - x1px) * (y2px - y1px));
    	int yGapLPx = (y2px + y1px) / 2 - gapVerticalPx / 2;
    	int yGapRPx = yGapLPx + gapVerticalPx;
    	params.getRenderTarget().drawLine(
      	new Point2i(x1px, y1px), new Point2i(xGapLPx, yGapLPx), paintColor, paintWidth);
    	params.getRenderTarget().drawLine(
      	new Point2i(xGapRPx, yGapRPx), new Point2i(x2px, y2px), paintColor, paintWidth);
    }
    else
    {
    	//no gap
    	params.getRenderTarget().drawLine(
      	new Point2i(x1px, y1px), new Point2i(x2px, y2px), paintColor, paintWidth);
    }
    //hooks
  	params.getRenderTarget().drawLine(
    	new Point2i(x1px, y1px), new Point2i(x1px, y1px + hookHeightPx * (tuplet.getY1lp() < 0 ? -1 : 1)), paintColor, paintWidth);
  	params.getRenderTarget().drawLine(
    	new Point2i(x2px, y2px), new Point2i(x2px, y2px + hookHeightPx * (tuplet.getY2lp() < 0 ? -1 : 1)), paintColor, paintWidth);
    
    //draw text
    if (text != null && text.getParagraphsCount() > 0)
    {
    	float textAscent = text.getParagraphs().next().getAscent();
    	int textPx = Units.mmToPx(textMm, scaling);
    	int textX = (x1px + x2px) / 2 - textPx / 2;
    	int textY = (y1px + y2px) / 2 + Units.mmToPx(textAscent, scaling) / 2;
    	params.getRenderTarget().drawText(tuplet.getText(),
    		new Point2i(textX, textY), true, 0, scaling);
    }
    
  }
  
  
}
