package com.xenoage.zong.renderer.frames;

import javax.media.opengl.GL;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.musiclayout.ScoreFrameLayout;
import com.xenoage.zong.musiclayout.stampings.Stamping;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.RenderingQuality;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.GLScreenRenderTarget;


/**
 * OpenGL renderer for a score frame.
 * 
 * @author Andreas Wenger
 */
public class GLScoreFrameRenderer
  extends GLFrameRenderer
{
  
  private static GLScoreFrameRenderer instance = null;
  
  
  public static GLScoreFrameRenderer getInstance()
  {
    if (instance == null)
      instance = new GLScoreFrameRenderer();
    return instance;
  }
  
  
  private GLScoreFrameRenderer()
  {
  }
  
  
  /**
   * Paints the given frame with the given OpenGL context.
   */
  @Override public void paint(Frame frame, GLGraphicsContext context)
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
    //paint the frame (only background)
    gl.glEnable(GL.GL_BLEND);
    paintTransformed(frame, context);
    //draw stampings
    ScoreFrame scoreFrame = (ScoreFrame) frame;
    ScoreFrameLayout layout = scoreFrame.getLayout();
    if (layout != null)
    {
      //the coordinates of the layout elements are relative to the upper left
      //corner, so we have to translate them
      gl.glTranslatef(-frame.getSize().width / 2, -frame.getSize().height / 2, 0);
      //since we need special drawing methods for staves, we have to undo the scaling
      gl.glScalef(1 / scaling, 1 / scaling, 1);
      
      
      //use target scaling for rendering stampings, but scale to
      //the current scaling (smooth zoom!) using an OpenGL matrix
      float rpTargetScaling = context.getTargetScaling() / (720f / 254f); //TIDY
      RenderingParams renderingParams = new RenderingParams(GLScreenRenderTarget.getInstance(context),
      	rpTargetScaling, RenderingQuality.Screen);
      context.getGL().glPushMatrix();
      float scalingFactor = context.getCurrentScaling() / context.getTargetScaling();
      context.getGL().glScalef(scalingFactor, scalingFactor, 1);
      
      //draw stampings
      //TODO ConcurrentModificationException can happen during playback
      for (Stamping s : layout.getAllStampings())
      {
        s.paint(renderingParams);
      }
      
      //reset OpenGL matrix
      context.getGL().glPopMatrix();
      
    }
    //restore old transformation
    gl.glPopMatrix();
  }
  
  
  /**
   * Paints the given Frame with the given OpenGL context.
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
