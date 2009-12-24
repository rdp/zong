package com.xenoage.zong.renderer.frames;

import java.awt.Color;

import javax.media.opengl.GL;

import com.xenoage.zong.app.opengl.OpenGLTools;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.TextFrame;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * OpenGL renderer for a text frame.
 * 
 * @author Andreas Wenger
 */
public class GLTextFrameRenderer
  extends GLFrameRenderer
{
  
  private static GLTextFrameRenderer instance = null;
  
  
  public static GLTextFrameRenderer getInstance()
  {
    if (instance == null)
      instance = new GLTextFrameRenderer();
    return instance;
  }
  
  
  private GLTextFrameRenderer()
  {
  }
  
  
  /**
	 * Paints the given TextFrame with the given OpenGL context.
	 * This method is called by the paint-method, the transformations
	 * are alread done.
	 * 
	 * TIDY: needs tidying, especially the texture manager.
	 * use "text frame change version number" instead of its size to
	 * find out if something has changed?
	 */
	@Override protected void paintTransformed(Frame frame, GLGraphicsContext context)
	{
	  
		GL gl = context.getGL();
		TextFrame textFrame = (TextFrame) frame;
		
		float x = frame.getSize().width / 2;
	  float y = frame.getSize().height / 2;
		
	  FormattedText text = textFrame.getTextWithLineBreaks();
	  if (text.getParagraphsCount() > 0)
	  {
	    context.getTextRenderer().drawFormattedText(text, -x, -y, false, 2 * x, 1, context);
		}
    else
    {
      //text frame has no content. show it with gray background
    	//so that the user notices that it exists.
      gl.glDisable(GL.GL_TEXTURE_2D);
      //fill frame with light gray
      OpenGLTools.setColor(gl, Color.lightGray);
      gl.glBegin(GL.GL_QUADS);
      gl.glVertex3f(-x, -y, 0);
      gl.glVertex3f(+x, -y, 0);
      gl.glVertex3f(+x, +y, 0);
      gl.glVertex3f(-x, +y, 0);
      gl.glEnd();
      //border in gray
      OpenGLTools.setColor(gl, Color.gray);
      gl.glBegin(GL.GL_LINE_LOOP);
      gl.glVertex3f(-x, -y, 0);
      gl.glVertex3f(+x, -y, 0);
      gl.glVertex3f(+x, +y, 0);
      gl.glVertex3f(-x, +y, 0);
      gl.glEnd();
      //diagonals cross in gray
      gl.glBegin(GL.GL_LINES);
      gl.glVertex3f(-x, -y, 0);
      gl.glVertex3f(+x, +y, 0);
      gl.glVertex3f(+x, -y, 0);
      gl.glVertex3f(-x, +y, 0);
      gl.glEnd();
    }
	}


}
