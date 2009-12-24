package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.VoltaStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenLine;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.util.Units;

import java.awt.Color;


/**
 * Renderer for the {@link VoltaStamping} class.
 *
 * @author Andreas Wenger
 */
public class VoltaStampingRenderer
{


	/**
	 * Paints the given {@link VoltaStamping} using the given rendering parameters.
	 */
  public static void paint(VoltaStamping volta, RenderingParams params)
  {
  	StaffStamping parentStaff = volta.getParentStaff();
    float scaling = params.getScaling();
    
    //horizontal position
    int x1 = Units.mmToPx(volta.getX1() + parentStaff.getPosition().x, scaling);
    int x2 = Units.mmToPx(volta.getX2() + parentStaff.getPosition().x, scaling);
    
    //compute hooks
    boolean hookLeft = volta.isLeftHook();
    boolean hookRight = volta.isRightHook();
    boolean hook = hookLeft || hookRight;
    int hookHeight = 0;
    if (hook)
    {
    	//height of hook is 2 interline spaces
    	hookHeight = Units.mmToPx(parentStaff.getInterlineSpace() * 2, scaling);
    }
    
    //width and color of the line
    Color color = Color.black;
    float width = parentStaff.getLineWidth() * 1.5f; //a little bit thicker than staff line
    int paintWidth;
    
    //compute the horizontal line and color
    int y;
    Color paintColor;
    if (params.getQuality() == RenderingQuality.Screen)
    {
      ScreenStaff ss = parentStaff.getScreenInfo().getScreenStaff(scaling);
      y = Units.mmToPx(parentStaff.getPosition().y, scaling) + ss.getLPPx(volta.getLinePosition());
      ScreenLine screenLine = parentStaff.getScreenInfo().getScreenLine(scaling, width, color);
      paintColor = screenLine.getColor();
      paintWidth = screenLine.getWidthPx();
    }
    else
    {
      y = Units.mmToPx(parentStaff.computeYMm(volta.getLinePosition()), scaling);
      paintColor = color;
      paintWidth = Units.mmToPx(width, scaling);
    }
    
    //draw line and hooks
    params.getRenderTarget().drawLine(
    	new Point2i(x1, y), new Point2i(x2, y), paintColor, paintWidth);
    if (hookLeft)
    {
    	params.getRenderTarget().drawLine(
      	new Point2i(x1, y), new Point2i(x1, y + hookHeight), paintColor, paintWidth);
    }
    if (hookRight)
    {
    	params.getRenderTarget().drawLine(
      	new Point2i(x2, y), new Point2i(x2, y + hookHeight), paintColor, paintWidth);
    }
    
    //draw text
    FormattedText text = volta.getText();
    if (text != null && text.getParagraphsCount() > 0)
    {
    	float textAscent = text.getParagraphs().next().getAscent();
    	int textX = x1 + Units.mmToPx(parentStaff.getInterlineSpace() * 1, scaling);
    	int textY = y + Units.mmToPx(textAscent, scaling);
    	params.getRenderTarget().drawText(volta.getText(),
    		new Point2i(textX, textY), true, 0, scaling);
    }
    
  }
  
  
}
