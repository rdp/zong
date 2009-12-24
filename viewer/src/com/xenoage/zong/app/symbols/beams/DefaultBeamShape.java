package com.xenoage.zong.app.symbols.beams;

import java.awt.Color;

import javax.media.opengl.GL;

import com.xenoage.util.MathTools;
import com.xenoage.util.math.Point2f;
import com.xenoage.zong.app.opengl.OpenGLTools;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Shape of a beam, drawn in the Default style.
 * 
 * This is a beam which is a parallelogram.
 * 
 * @author Andreas Wenger
 */
public class DefaultBeamShape
	implements BeamShape
{
	
	
	/**
	 * Draws the beam with OpenGL, using the given context and color.
	 * The beam is positioned at the given four points in clockwise order,
   * beginning with the lower left point.
   * The current interline space in px must also be given.
	 */
	public void draw(GLGraphicsContext glContext, Color color,
		Point2f[] points, float interlineSpace)
	{
		GL gl = glContext.getGL();
		OpenGLTools.setColor(gl, color);
	  
	  //with the antialising-simulation method the beam appears slightly thicker than without it
  	//so we make the beam 1 px tighter
  	int correction = (!glContext.getAntialiasing() ? 1 : 0);
		
  	//use no texture
  	gl.glDisable(GL.GL_TEXTURE_2D);
  	
	  //fill the beam, but only if either antialiasing is enabled or if
	  //zoom is high enough
	  if (glContext.getAntialiasing() || interlineSpace > 3)
	  {
	  	gl.glBegin(GL.GL_QUADS);
	    //lower left point
	    gl.glVertex3f(points[0].x, points[0].y + correction, 0);
	    //upper left point
	    gl.glVertex3f(points[1].x, points[1].y, 0);
	    //upper right point
	    gl.glVertex3f(points[2].x, points[3].y, 0);
	    //lower right point
	    gl.glVertex3f(points[3].x, points[2].y + correction, 0);
	    gl.glEnd();
	  }
	  
	  //if antialiasing is not working, generate smooth edges by drawing
	  //antialiased lines (works on most machines)
	  if (!glContext.getAntialiasing())
	  {
	  	//set the color of the line
	  	Color colorLine = new Color(color.getRed(), color.getGreen(), color.getBlue(),
	  		MathTools.clamp((int) (color.getAlpha() * interlineSpace * 0.1f), 0, 255));
	  	OpenGLTools.setColor(gl, colorLine);
	  	
	  	//compute and paint the lines
		  gl.glEnable(GL.GL_LINE_SMOOTH);
		  gl.glEnable(GL.GL_BLEND);
		  gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		  gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		  gl.glBegin(GL.GL_LINES);
	    //of course only the top and bottom line, not left or right
		  //bottom line
	    gl.glVertex3f(points[0].x, points[0].y + correction, 0);
	    gl.glVertex3f(points[3].x, points[2].y + correction, 0);
	    //top line
	    gl.glVertex3f(points[1].x, points[1].y, 0);
	    gl.glVertex3f(points[2].x, points[3].y, 0);
	    gl.glEnd();
		  
		  gl.glDisable(GL.GL_LINE_SMOOTH);
	  }
	  
	}
	
	
}
