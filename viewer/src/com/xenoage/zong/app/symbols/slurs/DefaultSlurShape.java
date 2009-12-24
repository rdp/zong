package com.xenoage.zong.app.symbols.slurs;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import javax.media.opengl.GL;

import com.xenoage.util.MathTools;
import com.xenoage.util.math.Point2f;
import com.xenoage.zong.app.opengl.OpenGLTools;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Shape of a slur, drawn in the Default style.
 * 
 * This is a slur which is thicker in the middle
 * and has sharp ends.
 * 
 * @author Andreas Wenger
 */
public class DefaultSlurShape
	implements SlurShape
{
	
	private float interlineSpace = 0;
	private Point2f p1top = null;
	private Point2f p2top = null;
	private Point2f c1top = null;
	private Point2f c2top = null;
	private Point2f p1bottom = null;
	private Point2f p2bottom = null;
	private Point2f c1bottom = null;
	private Point2f c2bottom = null;
	
	
	/**
   * Creates the shape of a slur, using the given Bézier curve.
   * @param p1      the starting point in px
   * @param p2      the ending point in px
   * @param c1      the first control point in px
   * @param c2      the second control point in px
   * @param interlineSpace  the interline space in px
   */
  public void setSlur(Point2f p1, Point2f p2, Point2f c1, Point2f c2,
  	float interlineSpace)
  {
  	//TODO: the following is only a rough estimate, not an exact formula!!
  	//we want the slur to have a height of only about 30% to 40% in the middle,
  	//but the following is no formula to compute that in a correct way.
  	
  	this.interlineSpace = interlineSpace;
  	//height at the end points
    float startHeight = 0.1f * interlineSpace;
  	//maximum width in the middle: 0.3 interline spaces
    float maxHeight = 0.3f * interlineSpace;
    
    float s = startHeight/2;
    float m = maxHeight/2;
    
  	this.p1top = p1.add(0, s);
  	this.p2top = p2.add(0, s);
  	this.c1top = c1.add(0, s + m);
  	this.c2top = c2.add(0, s + m);
  	
  	this.p1bottom = p1.add(0, -s);
  	this.p2bottom = p2.add(0, -s);
  	this.c1bottom = c1.add(0, -(s + m));
  	this.c2bottom = c2.add(0, -(s + m));
  }
	
	
	/**
	 * Gets the {@link Shape} of the slur, needed for printing.
	 */
	public Shape getShape()
	{
		float cap = interlineSpace / 4;
    GeneralPath shape = new GeneralPath();
    shape.moveTo(p1top.x, p1top.y);
    //bezier curve from p1top to p2top
    shape.curveTo(c1top.x, c1top.y, c2top.x, c2top.y, p2top.x, p2top.y);
    //cap at p2
    Point2f capDir = new Point2f(p2top.x - c2top.x, p2top.y - c2top.y).normalize().scale(cap);
    shape.curveTo(p2top.x + capDir.x, p2top.y + capDir.y,
    	p2bottom.x + capDir.x, p2bottom.y + capDir.y, p2bottom.x, p2bottom.y);
    //bezier curve back from p2bottom to p1bottom
    shape.curveTo(c2bottom.x, c2bottom.y, c1bottom.x, c1bottom.y, p1bottom.x, p1bottom.y);
    //cap at p1
    capDir = new Point2f(p1top.x - c1top.x, p1top.y - c1top.y).normalize().scale(cap);
    shape.curveTo(p1bottom.x + capDir.x, p1bottom.y + capDir.y,
    	p1top.x + capDir.x, p1top.y + capDir.y, p1top.x, p1top.y);
    shape.closePath();
    return shape;
	}
	
	
	/**
	 * Draws the slur with OpenGL, using the given context and color.
	 */
	public void draw(GLGraphicsContext glContext, Color color)
	{
		GL gl = glContext.getGL();
		OpenGLTools.setColor(gl, color);
		
		//number of iterations: one per 10 pixel, but at least 6
	  int steps = Math.max((int) MathTools.distance(p1top, p2top) / 10, 6);
	  
	  //with the antialising-simulation method the slur appears slightly thicker than without it
  	//so we make the slur 1 px tighter
  	int correction = (!glContext.getAntialiasing() ? 1 : 0);
		
  	//use no texture
  	gl.glDisable(GL.GL_TEXTURE_2D);
  	
	  //fill the slur, but only if either antialiasing is enabled or if
	  //zoom is high enough
	  if (glContext.getAntialiasing() || interlineSpace > 3)
	  {
			//compute and paint the quads
		  //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE); //TEST
		  gl.glBegin(GL.GL_QUADS);
		  
		  Point2f lastTop = MathTools.bezier(p1top, p2top, c1top, c2top, 0);
		  Point2f lastBottom = MathTools.bezier(p1bottom, p2bottom, c1bottom, c2bottom, 0);
		  for (int i = 1; i <= steps; i++)
		  {
		  	//compute new points
		  	Point2f top = MathTools.bezier(p1top, p2top, c1top, c2top, 1f * i / steps);
			  Point2f bottom = MathTools.bezier(p1bottom, p2bottom, c1bottom, c2bottom, 1f * i / steps);
		    //paint lower left point
			  gl.glVertex3f(lastBottom.x, lastBottom.y + correction, 0);
		    //paint upper left point
		    gl.glVertex3f(lastTop.x, lastTop.y, 0);
		    //paint upper right point
		    gl.glVertex3f(top.x, top.y, 0);
		    //paint lower right point
		    gl.glVertex3f(bottom.x, bottom.y + correction, 0);
		    //reuse points for next step
		    lastTop = top;
		    lastBottom = bottom;
		  }
		  gl.glEnd();
		  //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL); //TEST
	  }
	  
	  //if antialiasing is not working, generate smooth edges by drawing
	  //antialiased lines (works on most machines)
	  if (!glContext.getAntialiasing())
	  {
	  	//set the color of the line
	  	Color colorLine = new Color(color.getRed(), color.getGreen(), color.getBlue(),
	  		MathTools.clamp((int) (color.getAlpha() * interlineSpace * 0.2f), 0, 255));
	  	OpenGLTools.setColor(gl, colorLine);
	  	
	  	//compute and paint the lines
		  gl.glEnable(GL.GL_LINE_SMOOTH);
		  gl.glEnable(GL.GL_BLEND);
		  gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		  gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		  gl.glBegin(GL.GL_LINES);
		  
		  Point2f lastTop = MathTools.bezier(p1top, p2top, c1top, c2top, 0);
		  Point2f lastBottom = MathTools.bezier(p1bottom, p2bottom, c1bottom, c2bottom, 0);
		  for (int i = 1; i <= steps; i++)
		  {
		  	//compute new points
		  	Point2f top = MathTools.bezier(p1top, p2top, c1top, c2top, 1f * i / steps);
			  Point2f bottom = MathTools.bezier(p1bottom, p2bottom, c1bottom, c2bottom, 1f * i / steps);
		    //paint lower left point
		    gl.glVertex3f(lastBottom.x, lastBottom.y + correction, 0);
		    //paint lower right point
		    gl.glVertex3f(bottom.x, bottom.y + correction, 0);
		    //paint upper left point
		    gl.glVertex3f(lastTop.x, lastTop.y, 0);
		    //paint upper right point
		    gl.glVertex3f(top.x, top.y, 0);
		    //reuse points for next step
		    lastTop = top;
		    lastBottom = bottom;
		  }
		  gl.glEnd();
		  
		  gl.glDisable(GL.GL_LINE_SMOOTH);
	  }
	  
	}
	
	
}
