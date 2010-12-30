package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.zong.musiclayout.stampings.TextStamping;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.util.Units;


/**
 * Renderer for {@link TextStamping}.
 *
 * @author Andreas Wenger
 */
public class TextStampingRenderer
{


	/**
	 * Paints the given formatted text using the given information and rendering parameters.
	 */
  public static void paint(FormattedText text, Point2f position, RenderingParams params)
  {
    float scaling = params.getScaling();
    
    Point2i positionPx = new Point2i(Units.mmToPx(position.x, scaling),
    	Units.mmToPx(position.y, scaling));
    
    params.getRenderTarget().drawText(text, positionPx, true, 0, scaling);
    
    //TEST
    //params.getRenderTarget().drawLine(positionPx, positionPx.add(5, 5), java.awt.Color.red, 5);
  }
  
  
}
