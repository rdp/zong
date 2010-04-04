package com.xenoage.zong.renderer.stampings;

import static com.xenoage.zong.core.music.format.SP.sp;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.barline.BarlineGroupStyle;
import com.xenoage.zong.core.music.barline.BarlineRepeat;
import com.xenoage.zong.core.music.barline.BarlineStyle;
import com.xenoage.zong.core.music.format.SP;
import com.xenoage.zong.musiclayout.stampings.BarlineStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenLine;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.util.Units;

import java.awt.Color;
import java.util.List;


/**
 * Renderer for a {@link BarlineStamping}.
 *
 * @author Andreas Wenger
 */
public class BarlineStampingRenderer
{
	
	private static Symbol dotSymbol = null; //TODO: what, if pool changes?
  
  
	/**
	 * Paints the given barline stamping using the given rendering parameters.
	 * 
	 * TIDY
	 */
  public static void paint(BarlineStamping barline, RenderingParams params)
  {
  	float scaling = params.getScaling();
  	
    List<StaffStamping> staves = barline.getStaffLayoutElements();
    float xPosition = barline.getPositionX();
    int xCorrection = 0;
    
    //lines
    BarlineGroupStyle group = barline.getBarlineGroupStyle();
    BarlineStyle style = barline.getBarline().getStyle();
    if (group == null || group == BarlineGroupStyle.Single || group == BarlineGroupStyle.Common)
    {
      //draw single barlines
      for (StaffStamping staff : staves)
      {
      	xCorrection = paintBarline(params, staff, (staff.getLinesCount() - 1) * 2,
          staff, 0, xPosition, style);
      }
    }
    if (group == BarlineGroupStyle.Mensurstrich || group == BarlineGroupStyle.Common)
    {
      //draw barlines between staves
      for (int i = 0; i < staves.size() - 1; i++)
      {
      	xCorrection = paintBarline(params, staves.get(i), 0,
          staves.get(i+1), (staves.get(i+1).getLinesCount() - 1) * 2, xPosition, style);
      }
    }
    
    //repeat dots
    //TODO: xCorrection is the value of the last staff, but this may differ
    //draw repeat dots directly after drawing the corresponding staff!
    BarlineRepeat repeat = barline.getBarline().getRepeat();
    if (repeat == BarlineRepeat.Forward || repeat == BarlineRepeat.Both)
    {
    	paintRepeatDots(staves, xPosition + Units.pxToMm(xCorrection, scaling), 1, params);
    }
    if (repeat == BarlineRepeat.Backward || repeat == BarlineRepeat.Both)
    {
    	paintRepeatDots(staves, xPosition + Units.pxToMm(xCorrection, scaling), -1, params);
    }
  	
  }


  /**
   * Draws repeat dots at the given side (-1 or 1) at the given
   * horizontal position on the given staves.
   */
	private static void paintRepeatDots(List<StaffStamping> staves,
		float xPosition, int side, RenderingParams params)
	{
		for (StaffStamping staff : staves)
		{
			int lp1 = (staff.getLinesCount() + 1) / 2;
			int lp2 = lp1 + 2;
			float x = xPosition + staff.getInterlineSpace() * 1.2f * side;
		  paintRepeatDot(params, staff, sp(x, lp1));
		  paintRepeatDot(params, staff, sp(x, lp2));
		}
	}


  /**
   * Draws a barline with the given style between the given line of the given
   * staff and the other given line of the other
   * given staff, using the given rendering parameters.
   * The horizontal position correction in px is returned.
   * 
   * TIDY TIDY TIDY
   */
  private static int paintBarline(RenderingParams params,
  	StaffStamping staff1, float staff1LP, StaffStamping staff2, float staff2LP,
    float xPosition, BarlineStyle style)
  {
  	float scaling = params.getScaling();
  	Color col = Color.black;
  	float lightMm = staff1.getLineWidth() * 1.5f; //barline a little bit thicker than staff line
  	float heavyMm = lightMm * 3f;
  	float gapMm = lightMm * 1.5f;
  	int lightPx, heavyPx, gapPx;
  	Color lightColor, heavyColor;
  	//screen or printer?
  	if (params.getQuality() == RenderingQuality.Screen)
  	{
  		//screen
  		//light
	  	ScreenLine screenLine = new ScreenLine(lightMm, col, scaling);
	  	lightPx = screenLine.getWidthPx();
	  	lightColor = screenLine.getColor();
	  	//heavy
	  	screenLine = new ScreenLine(heavyMm, col, scaling);
	  	heavyPx = screenLine.getWidthPx();
	  	heavyColor = screenLine.getColor();
	  	//gap (for a better look, use bigger gap at low zoom. tried out.)
	  	float gapPxFloat = Units.mmToPxFloat(gapMm, scaling);
	  	if (gapPxFloat >= 2)
	  		gapPx = Math.round(gapPxFloat);
	  	else if (gapPxFloat >= 1f)
	  		gapPx = 2;
	  	else if (gapPxFloat >= 0.2f)
	  		gapPx = 1;
	  	else
	  		gapPx = 0;
  	}
  	else
  	{
  		//printer
  		//light
	  	lightPx = Units.mmToPx(lightMm, scaling);
	  	lightColor = col;
	  	//heavy
	  	heavyPx = Units.mmToPx(heavyMm, scaling);
	  	heavyColor = col;
	  	//gap
	  	gapPx = Units.mmToPx(gapMm, scaling);
  	}
  	//if on the very left or very right side of the staff, don't center
  	//the barline but place it completely within the staff
  	boolean isLeft = (xPosition <= staff1.getPosition().x + lightMm);
  	boolean isRight = (xPosition >= staff1.getPosition().x + staff1.getLength() - lightMm);
  	int x = Units.mmToPx(xPosition, scaling);
  	int c = 0;
  	//left and right half of the light/heavy/gap, both sides added up to light/heavy/gap
  	//if the width is odd, the right one is the bigger value
  	int ll = lightPx/2;
  	int lr = lightPx - ll;
  	int hl = heavyPx/2;
  	int hr = heavyPx - hl;
  	int gl = gapPx/2;
  	//TEST
		//paintLine(params, staff1, staff1LP + 4, staff2, staff1LP + 1, x - c, 1, Color.blue);
		//paintLine(params, staff1, staff2LP - 1, staff2, staff2LP - 4, x - c, 1, Color.blue);
		//TEST
		//style = BarlineStyle.HeavyHeavy;
    //draw lines dependent on style
		//all correction values have been found by trying out
  	//TODO: Dashed, Dotted
  	switch (style)
  	{
  		case Regular:
  			if (isLeft) {c = ll; x += c;}
  			else if (isRight) {c = -lr + 1; x += c;}
  			paintLine(params, staff1, staff1LP, staff2, staff2LP, x, lightPx, lightColor);
  			break;
  		case Heavy:
  			if (isLeft) {c = hl; x += c;}
  			else if (isRight) {c = -hr + 1; x += c;}
  			paintLine(params, staff1, staff1LP, staff2, staff2LP, x, heavyPx, heavyColor);
  			break;
  		case LightLight:
  			if (isLeft) {c = gl + 2 * ll; x += c;}
  			else if (isRight) {c = -lr + 1 - gl - ll; x += c;}
  			paintLine(params, staff1, staff1LP, staff2, staff2LP, x - gl - ll, lightPx, lightColor);
  			paintLine(params, staff1, staff1LP, staff2, staff2LP, x + gl + ll, lightPx, lightColor);
  			break;
  		case LightHeavy: //heavy is centered (if barline not at the border of the staff)
  			if (isLeft) {c = 2 * ll + gapPx + hl; x += c;}
  			else if (isRight) {c = -hr + 1; x += c;}
  			paintLine(params, staff1, staff1LP, staff2, staff2LP, x - hl - gapPx - ll, lightPx, lightColor);
  			paintLine(params, staff1, staff1LP, staff2, staff2LP, x, heavyPx, heavyColor);
  			break;
  		case HeavyLight: //heavy is centered (if barline not at the border of the staff)
  			if (isLeft) {c = hl; x += c;}
  			else if (isRight) {c = -lr + 1 - hl - gapPx - ll; x += c;}
  			paintLine(params, staff1, staff1LP, staff2, staff2LP, x, heavyPx, heavyColor);
  			paintLine(params, staff1, staff1LP, staff2, staff2LP, x + hl + gapPx + ll, lightPx, lightColor);
  			break;
  		case HeavyHeavy:
  			if (isLeft) {c = gl + 2 * hl; x += c;}
  			else if (isRight) {c = -hr + 1 - gl - hl; x += c;}
  			paintLine(params, staff1, staff1LP, staff2, staff2LP, x - gl - hl, heavyPx, heavyColor);
  			paintLine(params, staff1, staff1LP, staff2, staff2LP, x + gl + hl, heavyPx, heavyColor);
  			break;
  		case None: case Dashed: case Dotted:
  			break;
  	}
  	return c;
  }
  
  
  /**
   * Draws the given line with the given width in px.
   */
  private static void paintLine(RenderingParams params,
  	StaffStamping staff1, float staff1LinePosition,
  	StaffStamping staff2, float staff2LinePosition,
    int xPx, int widthPx, Color color)
  {
  	float scaling = params.getScaling();
  	if (params.getQuality() == RenderingQuality.Screen)
    {
      //render on screen
      ScreenStaff screenStaff1 = staff1.getScreenInfo().getScreenStaff(scaling);
      ScreenStaff screenStaff2 = staff2.getScreenInfo().getScreenStaff(scaling);
      Point2i p1Px = new Point2i(xPx, Units.mmToPx(staff1.getPosition().y, scaling) +
        screenStaff1.getLPPx(staff1LinePosition));
      Point2i p2Px = new Point2i(xPx, Units.mmToPx(staff2.getPosition().y, scaling) +
        screenStaff2.getLPPx(staff2LinePosition));
      params.getRenderTarget().drawLine(p1Px, p2Px,
      	color, widthPx);
    }
    else if (params.getQuality() == RenderingQuality.Print)
    {
      //render with high quality
      Point2i p1Px = new Point2i(xPx, Units.mmToPx(staff1.getPosition().y +
        staff1.getInterlineSpace() *
        (staff1.getLinesCount() - 1 - staff1LinePosition / 2), scaling));
      Point2i p2Px = new Point2i(xPx, Units.mmToPx(staff2.getPosition().y +
        staff2.getInterlineSpace() *
        (staff2.getLinesCount() - 1 - staff2LinePosition / 2), scaling));
      params.getRenderTarget().drawLine(p1Px, p2Px,
        color, widthPx);
    }
  }
  
  
  /**
   * Paints a repeat dot at the given position.
   */
  private static void paintRepeatDot(RenderingParams params, StaffStamping staff, SP position)
  {
  	if (dotSymbol == null)
  	{
  		//we use the note dot as the repeat dot
  		dotSymbol = App.getInstance().getSymbolPool().getSymbol(CommonSymbol.NoteDot);
  	}
  	StaffSymbolStampingRenderer.paint(dotSymbol, Color.black, position, 1, staff, false, params);
  }
  
  
}
