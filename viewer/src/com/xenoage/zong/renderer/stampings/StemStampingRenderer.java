package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.StemStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenLine;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.util.Units;

import java.awt.Color;


/**
 * Renderer for a stem stamping.
 * 
 * A stem has a notehead position and an end position
 * and is slightly thinner than a staff line.
 *
 * @author Andreas Wenger
 */
public class StemStampingRenderer
{


	/**
	 * Paints the given stem stamping using the given rendering parameters.
	 */
  public static void paint(StemStamping stem, RenderingParams params)
  {
    float scaling = params.getScaling();
    StaffStamping parentStaff = stem.getParentStaff();
    float positionX = stem.getPositionX();
    float noteheadLinePosition = stem.getNoteheadLinePosition();
    float endLinePosition = stem.getEndLinePosition();
    
    float lineWidthMm = parentStaff.getLineWidth(); //TODO: stem is thinner
    Point2i p1Px = new Point2i(
      Units.mmToPx(positionX - lineWidthMm / 2, scaling),
      Units.mmToPx(parentStaff.getPosition().y, scaling));
    Point2i p2Px = new Point2i(
      Units.mmToPx(positionX + lineWidthMm / 2, scaling), p1Px.y);
    Color color = Color.black;
    
    //shorten stem a little bit at the notehead - TODO: looks good. is code ok?
    if (endLinePosition > noteheadLinePosition)
      noteheadLinePosition += 0.2f;
    else
      noteheadLinePosition -= 0.2f;
    
    if (params.getQuality() == RenderingQuality.Screen)
    {
      //render on screen or print
      ScreenLine screenLine = new ScreenLine(lineWidthMm, color, scaling);
      ScreenStaff screenStaff = parentStaff.getScreenInfo().getScreenStaff(scaling);
      p1Px = new Point2i(p1Px.x, p1Px.y + screenStaff.getLPPx(noteheadLinePosition));
      p2Px = new Point2i(p2Px.x, p2Px.y + screenStaff.getLPPx(endLinePosition));
      int width = screenLine.getWidthPx(); //ensure same width for each stem in this staff
      params.getRenderTarget().fillRect(
        new Rectangle2i(p1Px.x, p1Px.y, width, p2Px.y - p1Px.y),
        /* TEST Color.green */ screenLine.getColor());
    }
    else if (params.getQuality() == RenderingQuality.Print)
    {
      //render with high quality
    	p1Px = new Point2i(p1Px.x, Units.mmToPx(parentStaff.computeYMm(noteheadLinePosition), scaling));
      p2Px = new Point2i(p2Px.x, Units.mmToPx(parentStaff.computeYMm(endLinePosition), scaling));
      params.getRenderTarget().fillRect(
        new Rectangle2i(p1Px.x, p1Px.y, p2Px.x - p1Px.x, p2Px.y - p1Px.y),
        color);
    }
    
  }
  
  
}
