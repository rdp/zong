package com.xenoage.zong.renderer.frames;

import java.awt.Color;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.xenoage.zong.app.opengl.OpenGLTools;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.ImageFrame;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * OpenGL renderer for an image frame.
 * 
 * @author Andreas Wenger
 */
public class GLImageFrameRenderer
  extends GLFrameRenderer
{
  
  private static GLImageFrameRenderer instance = null;
  
  
  public static GLImageFrameRenderer getInstance()
  {
    if (instance == null)
      instance = new GLImageFrameRenderer();
    return instance;
  }
  
  
  private GLImageFrameRenderer()
  {
  }
  
  
  /**
   * Paints the given ImageFrame with the given OpenGL context.
   * This method is called by the paint-method, the transformations
   * are alread done.
   */
  @Override protected void paintTransformed(Frame frame, GLGraphicsContext context)
  {
    GL gl = context.getGL();
    ImageFrame imageFrame = (ImageFrame) frame;
    //try to get the texture
    String imagePath = imageFrame.getImagePath();
    Texture texture = context.getTextureManager().getImageFrameTexture(imagePath);
    float x = frame.getSize().width / 2;
    float y = frame.getSize().height / 2;
    if (texture != null)
    {
      //texture is loaded: use it
      OpenGLTools.setColor(gl, Color.white);
      gl.glEnable(GL.GL_TEXTURE_2D);
      texture.bind();
      gl.glEnable(GL.GL_BLEND);
      gl.glBegin(GL.GL_QUADS);
      gl.glTexCoord2f(0, 1); gl.glVertex3f(-x, +y, 0); 
      gl.glTexCoord2f(1, 1); gl.glVertex3f(+x, +y, 0); 
      gl.glTexCoord2f(1, 0); gl.glVertex3f(+x, -y, 0); 
      gl.glTexCoord2f(0, 0); gl.glVertex3f(-x, -y, 0);
      gl.glEnd();
      gl.glDisable(GL.GL_BLEND);
    }
    else
    {
      //texture is not loaded
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
