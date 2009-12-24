package com.xenoage.zong.renderer.stampings;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.data.music.BracketGroupStyle;
import com.xenoage.zong.musiclayout.stampings.BracketStamping;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.ScreenStaff;
import com.xenoage.util.Units;

import java.awt.Color;


/**
 * Renderer for a bracket stamping.
 * 
 * At the moment braces and square brackets
 * are supported.
 *
 * @author Andreas Wenger
 */
public class BracketStampingRenderer
{
	
	//cached symbols
	//TODO: update, when symbols change
	private static Symbol braceSymbol, bracketLineSymbol, bracketEndSymbol;
	
	static
	{
		braceSymbol = App.getInstance().getSymbolPool().getSymbol("bracket-brace");
		bracketLineSymbol = App.getInstance().getSymbolPool().getSymbol("bracket-bracketline");
    bracketEndSymbol = App.getInstance().getSymbolPool().getSymbol("bracket-bracketend");
	}
	
  
	/*
  private StaffDrawingElement firstStaff;
  private StaffDrawingElement lastStaff;
  private float xPosition;
  private BracketGroupStyle style;
  
  
  
  
  public BracketDrawingElement(
    StaffDrawingElement firstStaff, StaffDrawingElement lastStaff,
    float xPosition, BracketGroupStyle style)
  {
    this.firstStaff = firstStaff;
    this.lastStaff = lastStaff;
    this.xPosition = xPosition;
    this.style = style;
    //TODO: error handling
    if (this.style == BracketGroupStyle.Brace)
    {
      braceSymbol = App.getInstance().getSymbolPool().getSymbol(
        "bracket-brace");
    }
    else if (this.style == BracketGroupStyle.Bracket)
    {
      bracketLineSymbol = App.getInstance().getSymbolPool().getSymbol(
        "bracket-bracketline");
      bracketEndSymbol = App.getInstance().getSymbolPool().getSymbol(
      "bracket-bracketend");
    }
  }
  */
	
  
	/**
	 * Paints the given bracket stamping using the given rendering parameters.
	 */
  public static void paint(BracketStamping bracket, RenderingParams params)
  {
  	BracketGroupStyle style = bracket.getStyle();
    if (style == null || style == BracketGroupStyle.Brace)
    {
      paintBrace(bracket, params);
    }
    else if (style == BracketGroupStyle.Bracket)
    {
      paintBracket(bracket, params);
    }
  }


  /**
   * Draws a brace, using the given bracket stamping and rendering parameters.
   */
  private static void paintBrace(BracketStamping bracket, RenderingParams params)
  {
    float scaling = params.getScaling();
    float interlineSpace = Units.mmToPx(bracket.getFirstStaff().getInterlineSpace(), scaling);
    
    float y1 = getStaffTopY(bracket.getFirstStaff(), params);
    float y2 = getStaffBottomY(bracket.getLastStaff(), params);
    Point2f pCenter = new Point2f(Units.mmToPx(bracket.getPositionX(), scaling),
      Math.round((y1 + y2) / 2));
   
    if (braceSymbol != null)
    {
      float symbolScaling = (y2 - y1) / braceSymbol.getBoundingRect().size.height;
      params.getRenderTarget().drawSymbol(braceSymbol, Color.black, pCenter,
        new Point2f(interlineSpace * 1.2f, symbolScaling));
    }
    
  }
  
  
  /**
   * Draws a bracket, using the given bracket stamping and rendering parameters.
   */
  private static void paintBracket(BracketStamping bracket, RenderingParams params)
  {
    float scaling = params.getScaling();
    float interlineSpace = Units.mmToPx(bracket.getFirstStaff().getInterlineSpace(), scaling);
    
    float y1 = getStaffTopY(bracket.getFirstStaff(), params);
    float y2 = getStaffBottomY(bracket.getLastStaff(), params);
    Point2f p1Px = new Point2f(Units.mmToPxFloat(bracket.getPositionX(), scaling), y1);
   
    if (bracketLineSymbol != null)
    {
      float symbolScaling = (y2 - y1 + 1) /
        bracketLineSymbol.getBoundingRect().size.height;
      params.getRenderTarget().drawSymbol(bracketLineSymbol, Color.black, p1Px,
        new Point2f(interlineSpace, symbolScaling));
      params.getRenderTarget().drawSymbol(bracketEndSymbol, Color.black, p1Px,
        new Point2f(interlineSpace, -interlineSpace));
      p1Px = new Point2f(p1Px.x, y2);
      params.getRenderTarget().drawSymbol(bracketEndSymbol, Color.black, p1Px,
        new Point2f(interlineSpace, interlineSpace));
    }
    
  }
  
  
  /**
   * Gets the vertical position in px of the topmost line
   * of the given staff.
   * TIDY: move into other class?
   */
  private static float getStaffTopY(StaffStamping staff, RenderingParams params)
  {
    float scaling = params.getScaling();
    float ret = Units.mmToPxFloat(staff.getPosition().y, scaling);
    if (params.getQuality() == RenderingQuality.Screen)
    {
      //render on screen
      ScreenStaff screenStaff = staff.getScreenInfo().getScreenStaff(scaling);
      ret += screenStaff.getYOffsetPx();
    }
    return ret;
  }
  
  
  /**
   * Gets the vertical position in px of the lowest line
   * of the given staff.
   * TIDY: move into other class?
   */
  private static float getStaffBottomY(StaffStamping staff, RenderingParams params)
  {
    float scaling = params.getScaling();
    if (params.getQuality() == RenderingQuality.Screen)
    {
      //render on screen
      ScreenStaff screenStaff = staff.getScreenInfo().getScreenStaff(scaling);
      return Units.mmToPx(staff.getPosition().y, scaling) + screenStaff.getLP0Px();
    }
    else if (params.getQuality() == RenderingQuality.Print)
    {
      //render with high quality
      return Units.mmToPxFloat(staff.getPosition().y +
        staff.getInterlineSpace() * (staff.getLinesCount() - 1) +
        staff.getLineWidth(), scaling);
    }
    return 0;
  }
  
  
}
