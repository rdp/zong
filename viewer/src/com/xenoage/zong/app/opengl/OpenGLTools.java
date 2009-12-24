package com.xenoage.zong.app.opengl;

import java.awt.Color;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2f;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.util.math.Size2f;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.renderer.GLGraphicsContext;


/**
 * Some useful methods to work with OpenGL.
 *
 * @author Andreas Wenger
 */
public class OpenGLTools
{
  
  
  /**
   * Sets the given color to the given GL context.
   */
  public static void setColor(GL gl, Color color)
  {
    if (color != null)
      gl.glColor4f(color.getRed() * 0.00390625f,
        color.getGreen() * 0.00390625f, color.getBlue() * 0.00390625f,
        color.getAlpha() * 0.00390625f);
    else
      gl.glColor3f(0, 0, 0);
  }
  
  
  /**
	 * Paints a texture within an axis-aligned rectangle.
	 * @param texture   the texture to draw
	 * @param destRect  the destination rectangle in score panel coordinates (px)
	 * @param color     the color to draw the texture. Use white for original texture.
	 * @param context   the OpenGL context
	 */
	public static void drawImage(Texture texture, Rectangle2i destRect, Color color,
		GLGraphicsContext context)
	{
		drawImage(texture, new Rectangle2f(0, 0, 1, 1), destRect, color, context);
	}
	
	
	/**
	 * Paints a part of a texture within an axis-aligned rectangle.
	 * @param texture   the texture to draw
	 * @param textureCoords  OpenGL texture coordinates
	 * @param destRect  the destination rectangle in score panel coordinates (px)
	 * @param color     the color to draw the texture. Use white for original texture.
	 * @param context   the OpenGL context
	 */
	public static void drawImage(Texture texture, Rectangle2f textureCoords,
		Rectangle2i destRect, Color color, GLGraphicsContext context)
	{
		//activate texture
  	GL gl = context.getGL();
  	gl.glEnable(GL.GL_BLEND);
    gl.glEnable(GL.GL_TEXTURE_2D);
    texture.bind();
    //set color
    OpenGLTools.setColor(gl, color);
    //draw quad
    Point2i pos = destRect.position;
    Size2i size = destRect.size;
    Point2f texpos = textureCoords.position;
    Size2f texsize = textureCoords.size;
  	gl.glBegin(GL.GL_QUADS);
    gl.glTexCoord2f(texpos.x, texpos.y);
    gl.glVertex3f(pos.x, pos.y, 0);
    gl.glTexCoord2f(texpos.x + texsize.width, texpos.y);
    gl.glVertex3f(pos.x + size.width, pos.y, 0);
    gl.glTexCoord2f(texpos.x + texsize.width, texpos.y + texsize.height);
    gl.glVertex3f(pos.x + size.width, pos.y + size.height, 0);
    gl.glTexCoord2f(texpos.x, texpos.y + texsize.height);
    gl.glVertex3f(pos.x, pos.y + size.height, 0);
    gl.glEnd();
	}
  

}
