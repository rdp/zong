package com.xenoage.zong.app.symbols;

import com.xenoage.util.math.*;
import com.xenoage.zong.app.opengl.OpenGLTools;
import com.xenoage.zong.renderer.GLGraphicsContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;


/**
 * Class for a simple symbol that
 * consists of a single rectangle.
 *
 * @author Andreas Wenger
 */
public class RectSymbol
  implements Symbol
{
  
  private String id;
  private Rectangle2D rectangle;
  
  
  public RectSymbol(String id, Rectangle2D rectangle)
  {
    this.id = id;
    this.rectangle = rectangle;
  }
  
  
  /**
   * Gets the ID of the symbol.
   */
  public String getID()
  {
    return id;
  }
  
  
  /**
   * Draws this symbol on the given Graphics2D instance
   * using the given color at the given position and the given scaling.
   */
  @Override public void draw(Graphics2D g2d, Color color,
    Point2f position, Point2f scaling)
  {
    //always draw veSctor at the moment, ignore quality
    AffineTransform g2dTransform = g2d.getTransform();
    g2d.translate(position.x, position.y);
    g2d.scale(scaling.x, scaling.y);
    g2d.setColor(color);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.fill(rectangle);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_OFF);
    g2d.setTransform(g2dTransform);
  }
  
  
  /**
   * Draws this symbol on the given GLGraphicsContext instance
   * with the given color at the given position and the given scaling.
   */
  @Override public void draw(GLGraphicsContext g, Color color,
    Point2f position, Point2f scaling)
  {
    GL gl = g.getGL();
    OpenGLTools.setColor(gl, color);
    gl.glDisable(GL.GL_TEXTURE_2D);
    gl.glBegin(GL.GL_QUADS);
    float y1 = position.y + scaling.y * (float) rectangle.getMinY();
    float y2 = y1 + scaling.y * (float) rectangle.getHeight();
    float x1 = position.x + scaling.x * (float) rectangle.getMinX();
    float x2 = x1 + scaling.x * (float) rectangle.getWidth();
    gl.glVertex3f(x1, y1, 0);
    gl.glVertex3f(x2, y1, 0);
    gl.glVertex3f(x2, y2, 0);
    gl.glVertex3f(x1, y2, 0);
    gl.glEnd();
  }
  
  
  /**
   * Gets the minimal bounding rectangle of this rectangle.
   */
  public Rectangle2f getBoundingRect()
  {
    return new Rectangle2f(rectangle);
  }
  
  
  /**
   * Sets the texture version of the symbol.
   * This method is ignored.
   */
  public void setTexture(SymbolTexture texture)
  {
  }
  
  
  /**
   * Returns null, because simpl rects need no texture.
   */
  public SymbolTexture getTexture()
  {
    return null;
  }
  
  
  /**
   * Returns DEFAULT_BASELINE, since {@link RectSymbol}s have no specific baseline.
   */
  public float getBaselineOffset()
  {
  	return DEFAULT_BASELINE;
  }
  
  
  /**
   * Returns DEFAULT_ASCENT, since {@link RectSymbol}s have no specific ascent.
   */
  public float getAscentHeight()
  {
  	return DEFAULT_ASCENT;
  }
  
  
  /**
   * Returns the left border of the bounding rectangle.
   */
  public float getLeftBorder()
  {
  	return (float) rectangle.getMinX();
  }
  
  
  /**
   * Returns the right border of the bounding rectangle.
   */
  public float getRightBorder()
  {
  	return (float) rectangle.getMaxX();
  }
  

}
