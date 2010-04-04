package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.StaffTextStamping;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.core.music.format.SP;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.util.Units;


/**
 * Renderer for {@link StaffTextStamping}.
 *
 * @author Andreas Wenger
 */
public class StaffTextStampingRenderer
{


	/**
	 * Paints the given formatted text using the given information and rendering parameters.
	 */
  public static void paint(FormattedText text, SP position, 
  	StaffStamping parentStaff, RenderingParams params)
  {
    float scaling = params.getScaling();
    
    Point2i positionPx;
    
    //OBSOLETE. always use mathematical correct position. otherwise the text "jumps" to much
    //which looks even more ugly.
    /* if (params.getQuality() == RenderingQuality.Screen)
    {
      ScreenStaff ss = parentStaff.getScreenInfo().getScreenStaff(scaling);
      int yPosition = Units.mmToPx(parentStaff.getPosition().y, scaling) + ss.getLPPx(position.yLp); 
      positionPx = new Point2i(Units.mmToPx(position.xMm, scaling), yPosition);
    }
    else
    { */
      int yPosition = Units.mmToPx(parentStaff.computeYMm(position.yLp), scaling);
      positionPx = new Point2i(Units.mmToPx(position.xMm, scaling), yPosition);
      
    //}
    
    params.getRenderTarget().drawText(text, positionPx, true, 0, scaling);
    
    //TEST
    //params.getRenderTarget().drawLine(positionPx, positionPx.add(5, 5), Color.red, 5);
  }
  
  
}
