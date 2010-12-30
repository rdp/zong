package com.xenoage.zong.app.symbols;

import com.xenoage.util.math.*;
import com.xenoage.zong.renderer.GLGraphicsContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.media.opengl.GL;


/**
 * Class for a simple symbol that
 * consists of a single GeneralPath.
 *
 * @author Andreas Wenger
 */
public class PathSymbol
  implements Symbol
{
  
  private String id;
  private GeneralPath path;
  private SymbolTexture texture;
  private Rectangle2f boundingRect;
  private float baselineOffset = DEFAULT_BASELINE;
  private float ascentHeight = DEFAULT_ASCENT;
  private Float leftBorder;
  private Float rightBorder;
  
  
  /**
   * Creates a new path symbol.
   * @param id                  the unique id of the symbol
   * @param path                the GeneralPath of the symbol
   * @param baselineOffset      the vertical offset of the baseline (useful for letters), or null if unknown
   * @param ascentHeight        the ascent height (useful for letters), or null if unknown
   * @param leftBorder          the custom left border (for text symbols), or null
   * @param rightBorder         the custom right border (for text symbols), or null
   */
  public PathSymbol(String id, GeneralPath path,
    Float baselineOffset, Float ascentHeight, Float leftBorder, Float rightBorder)
  {
    this.id = id;
    this.path = path;
    this.texture = null;
    this.boundingRect = new Rectangle2f(path.getBounds2D());
    if (baselineOffset != null)
    	this.baselineOffset = baselineOffset;
    if (ascentHeight != null)
    	this.ascentHeight = ascentHeight;
    this.leftBorder = leftBorder;
    this.rightBorder = rightBorder;
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
   * witht the given color at the given position and the given scaling.
   */
  @Override public void draw(Graphics2D g2d, Color color,
  	Point2f position, Point2f scaling)
  {
    //always draw vector at the moment, ignore quality
    AffineTransform g2dTransform = g2d.getTransform();
    g2d.translate(position.x, position.y);
    g2d.scale(scaling.x, scaling.y);
    g2d.setColor(color);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.fill(path);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_OFF);
    g2d.setTransform(g2dTransform);
  }
  
  
  /**
   * Draws this symbol on the given GLGraphicsContext instance
   * with the given color at the given position and the given scaling.
   */
  @Override  public void draw(GLGraphicsContext g, Color color,
  	Point2f position, Point2f scaling)
  {
  	GL gl = g.getGL();
    if (texture != null)
    {
      //use texture version of the symbol (is much faster)
      g.getTextureManager().activateAppTexture(texture.getTextureID());
      TextureRectangle2f r = texture.getTexCords();
      
      if (color != null)
        gl.glColor4f(color.getRed() / 255f,
          color.getGreen() / 255f, color.getBlue() / 255f, 1);
      else
        gl.glColor4f(1, 0, 0, 1);
      
      gl.glEnable(GL.GL_BLEND);
      gl.glEnable(GL.GL_TEXTURE_2D);
      //TODO: find best filter. perhaps create sharper mipmaps.
      gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
        GL.GL_LINEAR_MIPMAP_LINEAR);
      gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
        GL.GL_LINEAR_MIPMAP_LINEAR);
      
      
      Rectangle2f symbolRect = getBoundingRect();
      symbolRect = symbolRect.scaleX(scaling.x);
      symbolRect = symbolRect.scaleY(scaling.y);
      float x1 = symbolRect.position.x;
      float y1 = symbolRect.position.y;
      float x2 = symbolRect.position.x + symbolRect.size.width;
      float y2 = symbolRect.position.y + symbolRect.size.height;
      if (x2 == x1)
        x2++;
      if (y2 == y1)
        y2++;
      x1 += position.x;
      y1 += position.y;
      x2 += position.x;
      y2 += position.y;
      gl.glBegin(GL.GL_QUADS);
      gl.glTexCoord2f(r.x1,r.y1); gl.glVertex3f(x1, y1, 0);
      gl.glTexCoord2f(r.x2,r.y1); gl.glVertex3f(x2, y1, 0);
      gl.glTexCoord2f(r.x2,r.y2); gl.glVertex3f(x2, y2, 0);
      gl.glTexCoord2f(r.x1,r.y2); gl.glVertex3f(x1, y2, 0);
      gl.glEnd();
    }
    else
    {
    	//draw warning symbol
    	new WarningSymbol().draw(g, color, position, scaling);
    }
  }
  
  
  /**
   * Gets the minimal bounding rectangle of this path symbol.
   */
  public Rectangle2f getBoundingRect()
  {
    return boundingRect;
  }
  
  
  /**
   * Sets the texture version of the symbol.
   */
  public void setTexture(SymbolTexture texture)
  {
    this.texture = texture;
  }
  
  
  /**
   * Gets the texture version of the symbol, or null
   * if there is none.
   */
  public SymbolTexture getTexture()
  {
    return texture;
  }


  /**
   * Gets the general path used by this path symbol.
   */
  public GeneralPath getGeneralPath()
  {
    return path;
  }
  
  
  /**
   * Gets the vertical offset of the baseline (e.g. needed for dynamics letters),
   * or DEFAULT_BASELINE if undefined.
   */
  public float getBaselineOffset()
  {
  	return baselineOffset;
  }
  
  
  /**
   * Gets the height of the ascent (e.g. needed for dynamics letters),
   * or DEFAULT_ASCENT if undefined.
   */
  public float getAscentHeight()
  {
  	return ascentHeight;
  }
  
  
  /**
   * Gets the left border of the symbol. Used for symbols that are often used
   * within texts (like forte or piano). If undefined, the left border of
   * the bounding rect is returned.
   */
  public float getLeftBorder()
  {
  	if (leftBorder != null)
  		return leftBorder;
  	else
  		return boundingRect.position.x;
  }
  
  
  /**
   * Gets the right border of the symbol. Used for symbols that are often used
   * within texts (like forte or piano). If undefined, the right border of
   * the bounding rect is returned.
   */
  public float getRightBorder()
  {
  	if (rightBorder != null)
  		return rightBorder;
  	else
  		return boundingRect.position.x + boundingRect.size.width;
  }
  

}
