package com.xenoage.zong.renderer.frames;

import javax.media.opengl.GL;

import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * OpenGL renderer for a group frame.
 * 
 * @author Andreas Wenger
 */
public class GLGroupFrameRenderer
  extends GLFrameRenderer
{
  
  private static GLGroupFrameRenderer instance = null;
  
  
  public static GLGroupFrameRenderer getInstance()
  {
    if (instance == null)
      instance = new GLGroupFrameRenderer();
    return instance;
  }
  
  
  private GLGroupFrameRenderer()
  {
  }
  
  
  /**
   * Paints the given GroupFrame with the given OpenGL context.
   * This method is called by the paint-method, the transformations
   * are alread done.
   */
  @Override protected void paintTransformed(Frame frame, GLGraphicsContext context)
  {
    GL gl = context.getGL();
    //if there is a background, draw it
    if (frame.getBackground() != null)
    {
      frame.getBackground().apply(gl);
      float x = frame.getSize().width / 2;
      float y = frame.getSize().height / 2;
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glBegin(GL.GL_QUADS);
      gl.glVertex3f(-x, -y, 0);
      gl.glVertex3f(+x, -y, 0);
      gl.glVertex3f(+x, +y, 0);
      gl.glVertex3f(-x, +y, 0);
      gl.glEnd();
    }
  }


}
