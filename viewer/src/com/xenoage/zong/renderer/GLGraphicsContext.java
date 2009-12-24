package com.xenoage.zong.renderer;

import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.app.opengl.text.TextRenderer;

import javax.media.opengl.GL;


/**
 * This class contains an instance
 * of an OpenGL graphics context and
 * other data needed for painting
 * on the screen with OpenGL.
 *
 * @author Andreas Wenger
 */
public final class GLGraphicsContext
  implements GraphicsContext
{
  
  private final GL gl;
  private final TextureManager textureManager;
  private final TextRenderer textRenderer;
  
  private final Point2i originOffset;
  private final float targetScaling;
  private final float currentScaling;
  
  private final Rectangle2i windowRect;
  
  private final boolean antialiasing;
  
  
  /**
   * Creates a new OpenGL graphics context.
   * @param gl              the OpenGL context
   * @param textureManager  the texture manager holding all textures
   * @param textRenderer    the text renderer to draw formatted texts
   * @param originOffset    the offset of the origin in px (screen space)
   * @param targetScaling   the scaling factor relative to 72 dpi (convert from mm to px),
   * 												that will be reached at the end of the current zooming operation
   * @param currentScaling  the current scaling factor within a zooming operation
   * @param windowRect      the rectangle of the drawing window
   * @param antialiasing    true, if antialiasing is enabled, else false
   */
  public GLGraphicsContext(GL gl, TextureManager textureManager, TextRenderer textRenderer,
    Point2i originOffset, float targetScaling, float currentScaling, Rectangle2i windowRect,
    boolean antialiasing)
  {
    this.gl = gl;
    this.textureManager = textureManager;
    this.textRenderer = textRenderer;
    this.originOffset = originOffset;
    this.targetScaling = targetScaling;
    this.currentScaling = currentScaling;
    this.windowRect = windowRect;
    this.antialiasing = antialiasing;
  }
  
  
  public GL getGL()
  {
    return gl;
  }

  
  public TextureManager getTextureManager()
  {
    return textureManager;
  }
  
  
  public TextRenderer getTextRenderer()
  {
    return textRenderer;
  }

  
  public Point2i getOriginOffset()
  {
    return originOffset;
  }
  
  
  public GLGraphicsContext changeOriginOffset(Point2i originOffset)
  {
    return new GLGraphicsContext(gl, textureManager, textRenderer,
    	originOffset, targetScaling, currentScaling, windowRect, antialiasing);
  }

  
  /**
   * Gets the scaling factor that will be reached at the end of the current
   * zooming operation.
   */
  public float getTargetScaling()
  {
    return targetScaling;
  }
  
  
  /**
   * Gets the current scaling factor within a zooming operation.
   * This value may change very rapidly. If a scaling is needed for
   * precomputations, use getTargetScaling(), since this value
   * will not change as often.
   */
  public float getCurrentScaling()
  {
    return currentScaling;
  }
  
  
  public Rectangle2i getWindowRect()
  {
    return windowRect;
  }

	
	public boolean getAntialiasing()
	{
		return antialiasing;
	}


}
