package com.xenoage.zong.renderer.screen;

import static com.xenoage.util.math.Point2f.p;
import static com.xenoage.zong.app.opengl.OpenGLTools.setColor;

import java.awt.Color;

import javax.media.opengl.GL;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.zong.app.opengl.OpenGLTools;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.beams.DefaultBeamShape;
import com.xenoage.zong.app.symbols.slurs.DefaultSlurShape;
import com.xenoage.zong.app.symbols.slurs.SlurShape;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderTarget;
import com.xenoage.util.MathTools;
import com.xenoage.util.Units;


/**
 * This is the default rendering target for
 * screen rendering with OpenGL.
 *
 * @author Andreas Wenger
 */
public class GLScreenRenderTarget
  implements RenderTarget
{
  
  private static GLScreenRenderTarget instance = null;
  
  //TODO: the idea behind the RenderingParams (in the whole program)
  //is unclear and not used consistently. clean it up!
  private GLGraphicsContext currentGLContext;
  private Point2i currentPageOffsetPx = new Point2i(0, 0);
  
  
  /**
   * Gets the screen renderer for OpenGL.
   */
  public static GLScreenRenderTarget getInstance(GLGraphicsContext currentGLContext)
  {
    if (instance == null)
    {
      instance = new GLScreenRenderTarget();
    }
    instance.currentGLContext = currentGLContext;
    //TEST
    instance.currentPageOffsetPx = new Point2i(0, 0);
    return instance;
  }
  
  
  /**
   * Creates a GLScreenRenderTarget.
   */
  private GLScreenRenderTarget()
  {
  }


  public void drawLine(Point2i p1, Point2i p2, Color color, int lineWidth)
  {
    if (p1.y == p2.y)
    {
      //horizontal line
      int y1 = p1.y - Math.round(lineWidth / 2);
      int y2 = y1 + lineWidth;
      fillRect(new Rectangle2i(p1.x, y1, p2.x - p1.x, y2 - y1), color);
    }
    else if (p1.x == p2.x)
    {
      //vertical line
      int x1 = p1.x - Math.round(lineWidth / 2);
      int x2 = x1 + lineWidth;
      fillRect(new Rectangle2i(x1, p1.y, x2 - x1, p2.y - p1.y), color);
      //TEST
      //System.out.println(x2);
      //System.out.println(x1);
    }
    else
    {
    	//other line
    	//  v3_____
    	//   /     -----_____
    	//  * p1             -----_____v2 
    	// /_____                     /    ^/ vector v
    	// v0    -----____        p2 *    //  
    	//                -----_____/v1
    	Point2f p1ToP2normalized = new Point2f(p2.x - p1.x, p2.y - p1.y).normalize();
    	Point2f v = new Point2f(p1ToP2normalized.y, -1 * p1ToP2normalized.x).scale(lineWidth * 0.5f);
    	GLGraphicsContext g = currentGLContext;
    	GL gl = g.getGL();
      setColor(gl, color);
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glBegin(GL.GL_QUADS);
      int ox = currentPageOffsetPx.x;
      int oy = currentPageOffsetPx.y;
      Point2f v0 = p(ox + p1.x - v.x, oy + p1.y - v.y);
      Point2f v1 = p(ox + p2.x - v.x, oy + p2.y - v.y);
      Point2f v2 = p(ox + p2.x + v.x, oy + p2.y + v.y);
      Point2f v3 = p(ox + p1.x + v.x, oy + p1.y + v.y);
      gl.glVertex3f(v0.x, v0.y, 0);
      gl.glVertex3f(v1.x, v1.y, 0);
      gl.glVertex3f(v2.x, v2.y, 0);
      gl.glVertex3f(v3.x, v3.y, 0);
      gl.glEnd();
      if (!currentGLContext.getAntialiasing() && lineWidth < 4)
      {
      	//there is no antialiasing and the line is thin.
      	//achieve better smoothing with lines on the top and the bottom
      	
      	//set the color of the line
  	  	Color colorLine = new Color(color.getRed(), color.getGreen(), color.getBlue(),
  	  		MathTools.clamp((int) (color.getAlpha() * 0.3f), 0, 255));
  	  	OpenGLTools.setColor(gl, colorLine);
  	  	
  	  	//paint the lines
  		  gl.glEnable(GL.GL_LINE_SMOOTH);
  		  gl.glEnable(GL.GL_BLEND);
  		  gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
  		  gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
  		  gl.glBegin(GL.GL_LINES);
  		  //top line
  		  gl.glVertex3f(v0.x, v0.y, 0);
        gl.glVertex3f(v1.x, v1.y, 0);
  		  //bottom line
        gl.glVertex3f(v2.x, v2.y, 0);
        gl.glVertex3f(v3.x, v3.y, 0);
  		  gl.glEnd();
  		  
  		  gl.glDisable(GL.GL_LINE_SMOOTH);
      }
    }
  }


  public void drawSimplifiedStaff(Point2i pos, int length, int height, Color color)
  {
    GLGraphicsContext g = currentGLContext;
    GL gl = g.getGL();
    setColor(gl, color);
    gl.glDisable(GL.GL_TEXTURE_2D);
    int y1 = currentPageOffsetPx.y + pos.y;
    int y2 = y1 + height;
    int x1 = currentPageOffsetPx.x + pos.x;
    int x2 = x1 + length;
    gl.glBegin(GL.GL_QUADS);
    gl.glVertex3i(x1, y1, 0);
    gl.glVertex3i(x2, y1, 0);
    gl.glVertex3i(x2, y2, 0);
    gl.glVertex3i(x1, y2, 0);
    gl.glEnd();
  }


  public void drawStaff(Point2i pos, int length, int lines, Color color, int lineWidth, int interlineSpace)
  {
    GLGraphicsContext g = currentGLContext;
    GL gl = g.getGL();
    setColor(gl, color);
    for (int i = 0; i < lines; i++)
    {
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glBegin(GL.GL_QUADS);
      int y1 = currentPageOffsetPx.y + pos.y + i * interlineSpace - lineWidth / 2;
      int y2 = y1 + lineWidth;
      gl.glVertex3i(currentPageOffsetPx.x + pos.x, y1, 0);
      gl.glVertex3i(currentPageOffsetPx.x + pos.x + length, y1, 0);
      gl.glVertex3i(currentPageOffsetPx.x + pos.x + length, y2, 0);
      gl.glVertex3i(currentPageOffsetPx.x + pos.x, y2, 0);
      gl.glEnd();
    }
    //TEST
    //System.out.println(currentPageOffsetPx.x + pos.x + length);
    //System.out.println(currentPageOffsetPx.x + pos.x);
  }


  public void drawSymbol(Symbol symbol, Color color, Point2f position, Point2f scaling)
  {
    symbol.draw(currentGLContext, color,
      new Point2f(position.x + currentPageOffsetPx.x,
        position.y + currentPageOffsetPx.y), scaling);
  }


  /**
   * Draws the given AttributedString to the given position.
   * The y-position is the baseline of the first paragraph.
   */
  public void drawText(FormattedText text, Point2i position, boolean yIsBaseline, float width, float viewScaling)
  {
  	GLGraphicsContext g = currentGLContext;
    g.getTextRenderer().drawFormattedText(
    	text, position.x, position.y, yIsBaseline, 0, Units.mmToPxFloat(1, viewScaling), g);
  }


  /**
   * Draws a beam using the given quad and color.
   * @param points  the four points in clockwise order,
   *                beginning with the lower left point
   * @param color   the color of the beam
   * @param interlineSpace  the interline space in px
   */
  public void drawBeam(Point2f[] points, Color color, float interlineSpace)
  {
  	Point2f[] p = new Point2f[]{
  		points[0].add(currentPageOffsetPx),
  		points[1].add(currentPageOffsetPx),
  		points[2].add(currentPageOffsetPx),
  		points[3].add(currentPageOffsetPx)
  	};
  	//TIDY
    new DefaultBeamShape().draw(currentGLContext, color, p, interlineSpace);
  }
  
  
  /**
   * Draws a tie/slur using the given Bézier curve.
   * @param p1      the starting point
   * @param p2      the ending point
   * @param c1      the first control point
   * @param c2      the second control point
   * @param interlineSpace  the interline space in px
   * @param color   the color of the slur
   */
  public void drawCurvedLine(Point2f p1, Point2f p2, Point2f c1, Point2f c2, float interlineSpace, Color color)
  {
    //creates and draws the shape
    SlurShape shape = new DefaultSlurShape();
    shape.setSlur(p1, p2, c1, c2, interlineSpace);
    shape.draw(currentGLContext, color);
  }
  
  
  /**
   * Fills a rectangle.
   * @param rect    the rectangle
   * @param color   the color of the rectangle
   */
  public void fillRect(Rectangle2i rect, Color color)
  {
    GLGraphicsContext g = currentGLContext;
    GL gl = g.getGL();
    setColor(gl, color);
    gl.glDisable(GL.GL_TEXTURE_2D);
    gl.glBegin(GL.GL_QUADS);
    int x1 = currentPageOffsetPx.x + rect.position.x;
    int x2 = x1 + rect.size.width;
    int y1 = currentPageOffsetPx.y + rect.position.y;
    int y2 = y1 + rect.size.height;
    gl.glVertex3f(x1, y1, 0);
    gl.glVertex3f(x2, y1, 0);
    gl.glVertex3f(x2, y2, 0);
    gl.glVertex3f(x1, y2, 0);
    gl.glEnd();
  }
  
  
}
