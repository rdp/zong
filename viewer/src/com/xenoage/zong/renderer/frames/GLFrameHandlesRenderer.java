package com.xenoage.zong.renderer.frames;

import javax.media.opengl.GL;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.FrameHandle;
import com.xenoage.zong.layout.frames.FrameHandle.HandlePosition;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * OpenGL renderer for the adjustment handles
 * of a frame.
 * 
 * These are the handles at NW, N, NE, E,
 * SE, S, SW and W, as well as a dashed
 * border line and a rotation handle
 * above the N handle.
 * 
 * @author Andreas Wenger
 */
public class GLFrameHandlesRenderer
  extends GLFrameRenderer
{
  
  private static GLFrameHandlesRenderer instance = null;
  
  private float[] colorBorder1;
  private float[] colorBorder1Hover;
  private float[] colorBorder2;
  private float[] colorBorder2Hover;
  
  
  public static GLFrameHandlesRenderer getInstance()
  {
    if (instance == null)
      instance = new GLFrameHandlesRenderer();
    return instance;
  }
  
  
  private GLFrameHandlesRenderer()
  {
    colorBorder1 = new float[]{0.2f, 0.4f, 1f};
    colorBorder1Hover = new float[]{1f, 0.4f, 0.2f};
    colorBorder2 = new float[]{1f, 1f, 1f};
    colorBorder2Hover = new float[]{1f, 0.9f, 0.8f};
  }
  
  
  /**
   * Paints the handles of the given Frame with the given OpenGL context.
   * This method is called by the paint-method, the transformations
   * are alread done.
   */
  @Override protected void paintTransformed(Frame frame, GLGraphicsContext context)
  {
    GL gl = context.getGL();
    float scaling = context.getCurrentScaling();
    HandlePosition selectedHandle = frame.getSelectedHandle();
    
    //compute coordinates
    float s = (FrameHandle.SIZE_IN_PX / 2) / scaling;
    float x = frame.getSize().width / 2;
    float y = frame.getSize().height / 2;
    float rotY = -y - (FrameHandle.DISTANCE_ROTATIONHANDLE_IN_PX / scaling);
    
    //draw border lines in first border color
    gl.glDisable(GL.GL_TEXTURE_2D);
    gl.glColor3fv(selectedHandle == HandlePosition.Move ? colorBorder1Hover : colorBorder1, 0);
    drawBorder(gl, x, y, rotY);
    
    //draw dashed border lines in second border color
    gl.glPushAttrib(GL.GL_LINE_BIT);
    gl.glLineStipple(4, (short) 0xAAAA);
    gl.glEnable(GL.GL_LINE_STIPPLE);
    gl.glColor3fv(selectedHandle == HandlePosition.Move ? colorBorder2Hover : colorBorder2, 0);
    drawBorder(gl, x, y, rotY);
    gl.glDisable(GL.GL_LINE_STIPPLE);
    gl.glPopAttrib();
    
    //draw resize handles
    float rhd = 4 / scaling; //resize handle distance
    float scale = 1;
    Point2f pos[] = new Point2f[8];
    pos[0] = new Point2f( 0,       -y - rhd); //N
    pos[1] = new Point2f(+x + rhd, -y - rhd); //NE
    pos[2] = new Point2f(+x + rhd,  0      ); //E
    pos[3] = new Point2f(+x,       +y + rhd); //SE
    pos[4] = new Point2f( 0,       +y + rhd); //S
    pos[5] = new Point2f(-x - rhd, +y + rhd); //SW
    pos[6] = new Point2f(-x - rhd,  0      ); //W
    pos[7] = new Point2f(-x - rhd, -y - rhd); //NW
    
    gl.glColor3f(1, 1, 1);
    gl.glEnable(GL.GL_BLEND);
    gl.glEnable(GL.GL_TEXTURE_2D);
    for (int i = 0; i < 8; i++)
    {
      
      if (selectedHandle == getHandle(i))
      {
        context.getTextureManager().activateAppTexture(TextureManager.ID_HANDLE_RESIZE_HOVER);
        scale = 1.2f;
      }
      else
      {
        context.getTextureManager().activateAppTexture(TextureManager.ID_HANDLE_RESIZE);
        scale = 1;
      }
      
      gl.glPushMatrix();
      gl.glTranslatef(pos[i].x, pos[i].y, 0);
      gl.glRotatef(i * 45f, 0, 0, 1);
      gl.glBegin(GL.GL_QUADS);
      gl.glTexCoord2f(0, 1); gl.glVertex3f(-s * scale, +s * scale, 0); 
      gl.glTexCoord2f(1, 1); gl.glVertex3f(+s * scale, +s * scale, 0); 
      gl.glTexCoord2f(1, 0); gl.glVertex3f(+s * scale, -s * scale, 0); 
      gl.glTexCoord2f(0, 0); gl.glVertex3f(-s * scale, -s * scale, 0);
      gl.glEnd();
      gl.glPopMatrix();
    }
    
    //draw rotation handle
    if (selectedHandle == HandlePosition.Rotation)
    {
      context.getTextureManager().activateAppTexture(TextureManager.ID_HANDLE_ROTATE_HOVER);
      scale = 1.2f;
    }
    else
    {
      context.getTextureManager().activateAppTexture(TextureManager.ID_HANDLE_ROTATE);
      scale = 1;
    }
    gl.glPushMatrix();
    gl.glTranslatef(0, rotY, 0);
    gl.glBegin(GL.GL_QUADS);
    gl.glTexCoord2f(0, 1); gl.glVertex3f(-s * scale, +s * scale, 0); 
    gl.glTexCoord2f(1, 1); gl.glVertex3f(+s * scale, +s * scale, 0); 
    gl.glTexCoord2f(1, 0); gl.glVertex3f(+s * scale, -s * scale, 0); 
    gl.glTexCoord2f(0, 0); gl.glVertex3f(-s * scale, -s * scale, 0);
    gl.glEnd();
    gl.glPopMatrix();
    
    gl.glDisable(GL.GL_BLEND);
    
  }


  /**
   * Draws the border lines.
   * @param gl    the GL context
   * @param x     the half width of the frame
   * @param y     the half height of the frame
   * @param rotY  the y-coordinate of the rotation point
   */
  private void drawBorder(GL gl, float x, float y, float rotY)
  {
    //draw frame border
    gl.glBegin(GL.GL_LINE_LOOP);
    gl.glVertex3f(-x, +y, 0); 
    gl.glVertex3f(+x, +y, 0); 
    gl.glVertex3f(+x, -y, 0); 
    gl.glVertex3f(-x, -y, 0);
    gl.glEnd();
    
    //draw line to rotation handle
    gl.glBegin(GL.GL_LINES);
    gl.glVertex3f(0, -y, 0); 
    gl.glVertex3f(0, rotY, 0);
    gl.glEnd();
  }

  
  private HandlePosition getHandle(int index)
  {
    switch (index)
    {
      case 0: return HandlePosition.N;
      case 1: return HandlePosition.NE;
      case 2: return HandlePosition.E;
      case 3: return HandlePosition.SE;
      case 4: return HandlePosition.S;
      case 5: return HandlePosition.SW;
      case 6: return HandlePosition.W;
      case 7: return HandlePosition.NW;
      default: return null;
    }
  }

}
