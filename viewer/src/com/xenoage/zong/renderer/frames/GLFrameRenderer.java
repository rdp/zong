package com.xenoage.zong.renderer.frames;

import javax.media.opengl.GL;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Abstract base class for frame renderers working with OpenGL.
 * 
 * @author Andreas Wenger
 */
public abstract class GLFrameRenderer
{
  
  /**
   * Paints the given frame with the given OpenGL context.
   */
  public void paint(Frame frame, GLGraphicsContext context)
  {
    GL gl = context.getGL();
    //backup old transformation
    gl.glPushMatrix();
    //apply translation: origin offset in screen space
    gl.glTranslatef(context.getOriginOffset().x, context.getOriginOffset().y, 0);
    //apply scaling
    float scaling = context.getCurrentScaling();
    gl.glScalef(scaling, scaling, 1);
    //apply translation: absolute position in layout space
    Point2f pos = frame.getAbsolutePosition();
    gl.glTranslatef(pos.x, pos.y, 0);
    //apply rotation
    gl.glRotatef(-frame.getAbsoluteRotation(), 0, 0, 1);
    //paint the frame
    paintTransformed(frame, context);
    //restore old transformation
    gl.glPopMatrix();
  }
  
  
  /**
   * Paint the given frame with the given OpenGL context.
   * This method is called by the paint-method, the transformations
   * are alread done.
   */
  protected abstract void paintTransformed(Frame frame, GLGraphicsContext context);
  
  

}
