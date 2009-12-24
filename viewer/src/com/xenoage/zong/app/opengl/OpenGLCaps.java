package com.xenoage.zong.app.opengl;

import com.sun.opengl.util.BufferUtil;
import com.xenoage.util.math.Size2i;

import java.nio.IntBuffer;

import javax.media.opengl.*;


/**
 * This class contains methods to query
 * the capabilities of the used OpenGL
 * implementation.
 *
 * @author Andreas Wenger
 */
public class OpenGLCaps
{
  
  private static GLPbuffer pbuffer = null;
  
  
  /**
   * Finds out the maximum size for a 2D texture.
   * Only lengths with a power of 2 are tested.
   * First the biggest square texture is found, then this
   * method tries to double its width.
   * The biggest possible texture is returned then.
   * @param gl  a valid GL instance, or null, if a pbuffer
   *            should be used (may not work on older machines)
   * @return    the maximum texture size, or 0 if an error
   *            occurs
   */
  public static Size2i getMaxRGBATextureSize(GL gl)
  {
    /* TODO: does not work yet :-(
      java.lang.IllegalArgumentException:
        Illegally formatted version identifier: "null"
    int length = 256;
    //find biggest square texture
    while (isRGBATextureSupported(gl, length, length))
    {
      length *= 2;
    }
    length /= 2;
    //try to double width
    if (isRGBATextureSupported(gl, length * 2, length))
      return new Size2i(length * 2, length);
    else
      return new Size2i(length, length);
    */
    if (gl == null)
      gl = createGL();
    if (gl == null)
      return new Size2i(0, 0);
    IntBuffer intBuffer = BufferUtil.newIntBuffer(1);
    intBuffer.rewind();
    gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, intBuffer);
    releaseGL();
    int length = intBuffer.get(0);
    return new Size2i(length, length);
  }
  
  
  /**
   * Returns true, when a rgba texture with the given size and format
   * is supported.
   *-/
  private static boolean isRGBATextureSupported(GL gl,
    int width, int height)
  {
    IntBuffer intBuffer = BufferUtil.newIntBuffer(1);
    gl.glTexImage2D(GL.GL_PROXY_TEXTURE_2D, 0, GL.GL_RGBA,
      width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, 0); //TODO: correct texture type?
    gl.glGetTexLevelParameteriv(GL.GL_PROXY_TEXTURE_2D, 0,
      GL.GL_TEXTURE_WIDTH, intBuffer);
    return (intBuffer.get(0) > 0);
  } */
  
  
  /**
   * Creates a GL instance of an offscreen buffer.
   * Can be used for methods, that need no visible surface.
   * It may return null on older machines, that do not
   * support pbuffers.
   * After the using the GL instance the method
   * releaseGL() must be called!
   */
  static GL createGL()
  {
    GLDrawableFactory f = GLDrawableFactory.getFactory();
    if (f.canCreateGLPbuffer())
    {
      try
      {
        pbuffer = f.createGLPbuffer(new GLCapabilities(),
          null, 1, 1, null);
        pbuffer.display();
        if (pbuffer.getContext().makeCurrent() != GLContext.CONTEXT_NOT_CURRENT)
          return pbuffer.getGL();
      }
      catch (Exception ex)
      {
        //ex.printStackTrace();
      }
    }
    return null;
  }
  
  
  /**
   * Release the GL instance created by
   * createGL().
   */
  static void releaseGL()
  {
    if (pbuffer != null)
    {
      pbuffer.getContext().release();
      pbuffer = null;
    }
  }
  
  
  private OpenGLCaps()
  {
  }

}
