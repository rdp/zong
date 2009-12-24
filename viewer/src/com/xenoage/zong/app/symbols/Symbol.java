package com.xenoage.zong.app.symbols;

import com.xenoage.util.math.*;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.RenderingQuality;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Interface for all musical symbol images,
 * like noteheads, clefs or ornaments.
 *
 * @author Andreas Wenger
 */
public interface Symbol
{
	
	public static float DEFAULT_BASELINE = 0;
	public static float DEFAULT_ASCENT = 2f;
	

  /**
   * Gets the ID of the symbol.
   */
  public String getID();
  
  
  /**
   * Draws this symbol on the given Graphics2D instance
   * with the given color at the given position and the given scaling.
   */
  public void draw(Graphics2D g2d, Color color, Point2f position, Point2f scaling);
  
  
  /**
   * Draws this symbol on the given GLGraphicsContext instance
   * with the given color at the given position and the given scaling..
   */
  public void draw(GLGraphicsContext g, Color color, Point2f position, Point2f scaling);
  
  
  /**
   * Gets the minimal bounding rectangle of this symbol.
   */
  public Rectangle2f getBoundingRect();
  
  
  /**
   * Sets the texture version of the symbol.
   */
  public void setTexture(SymbolTexture texture);
  
  
  /**
   * Gets the texture version of the symbol, or null
   * if there is none.
   */
  public SymbolTexture getTexture();
  
  
  /**
   * Gets the vertical offset of the baseline (e.g. needed for dynamics letters),
   * if known (otherwise DEFAULT_BASELINE should be returned).
   */
  public float getBaselineOffset();
  
  
  /**
   * Gets the height of the ascent (e.g. needed for dynamics letters),
   * if known (otherwise DEFAULT_ASCENT should be returned).
   */
  public float getAscentHeight();
  
  
  /**
   * Gets the left border of the symbol. Used for symbols that are often used
   * within texts (like forte or piano). If undefined, the left border of
   * the bounding rect is returned.
   */
  public float getLeftBorder();
  
  
  /**
   * Gets the right border of the symbol. Used for symbols that are often used
   * within texts (like forte or piano). If undefined, the right border of
   * the bounding rect is returned.
   */
  public float getRightBorder();
  
  
}
