package com.xenoage.zong.app.symbols;

import com.xenoage.util.math.*;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.renderer.GLGraphicsContext;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.media.opengl.GL;


/**
 * Warning symbol, that can for example be used if other symbols
 * are missing.
 * 
 * It is shown on screen, but not printed.
 *
 * @author Andreas Wenger
 */
public class WarningSymbol
	implements Symbol
{
	
	public static final WarningSymbol instance = new WarningSymbol();
	

  /**
   * Gets the ID of the symbol.
   */
  public String getID()
  {
  	return "warning";
  }
  
  
  /**
   * Draws this symbol on the given Graphics2D instance
   * with the given color at the given position and the given scaling.
   */
  @Override public void draw(Graphics2D g2d, Color color,
    Point2f position, Point2f scaling)
  {
  	//the warning symbol is not printed
  }
  
  
  /**
   * Draws this symbol on the given GLGraphicsContext instance
   * with the given color at the given position and the given scaling.
   */
  @Override public void draw(GLGraphicsContext g, Color color,
    Point2f position, Point2f scaling)
  {
  	GL gl = g.getGL();
  	g.getTextureManager().activateAppTexture(TextureManager.ID_WARNING);
  	gl.glEnable(GL.GL_TEXTURE_2D);
  	gl.glColor4f(1, 1, 1, 1);
  	float d = Math.min(5f * scaling.x, 64) / 2f; //normal size: 5 mm. max size: 64 pix.
  	gl.glBegin(GL.GL_QUADS);
    gl.glTexCoord2f(0, 0); gl.glVertex3f(position.x - d, position.y - d, 0);
    gl.glTexCoord2f(1, 0); gl.glVertex3f(position.x + d, position.y - d, 0);
    gl.glTexCoord2f(1, 1); gl.glVertex3f(position.x + d, position.y + d, 0);
    gl.glTexCoord2f(0, 1); gl.glVertex3f(position.x - d, position.y + d, 0);
    gl.glEnd();
  }
  
  
  /**
   * Gets the minimal bounding rectangle of this symbol.
   */
  public Rectangle2f getBoundingRect()
  {
  	return new Rectangle2f(0, 0, 0, 0);
  }
  
  
  /**
   * Sets the texture version of the symbol.
   */
  public void setTexture(SymbolTexture texture)
  {
  	throw new IllegalStateException("Warning symbol has no symbol texture");
  }
  
  
  /**
   * Gets the texture version of the symbol, or null
   * if there is none.
   */
  public SymbolTexture getTexture()
  {
  	return null;
  }
  
  
  /**
   * Returns DEFAULT_BASELINE, since {@link WarningSymbol}s have no specific baseline.
   */
  public float getBaselineOffset()
  {
  	return DEFAULT_BASELINE;
  }
  
  
  /**
   * Returns DEFAULT_ASCENT, since {@link WarningSymbol}s have no specific ascent.
   */
  public float getAscentHeight()
  {
  	return DEFAULT_ASCENT;
  }
  
  
  /**
   * Returns 0.
   */
  public float getLeftBorder()
  {
  	return 0;
  }
  
  
  /**
   * Returns 0.
   */
  public float getRightBorder()
  {
  	return 0;
  }
  
  
}
