package com.xenoage.zong.layout.frames.background;

import java.awt.Color;
import java.awt.Paint;

import javax.media.opengl.GL;

import com.xenoage.zong.app.opengl.OpenGLTools;


/**
 * A single color background for a frame.
 * 
 * @author Andreas Wenger
 */
public class ColorBackground
  implements Background
{
  
  private Color color;
  
  
  /**
   * Creates a background with the given color.
   */
  public ColorBackground(Color color)
  {
    this.color = color;
  }
  
  
  /**
   * Returns the Paint instance of this background.
   */
  public Paint getPaint()
  {
    return color;
  }
  
  
  /**
   * Applies the background to the given GL context.
   */
  public void apply(GL gl)
  {
    OpenGLTools.setColor(gl, color);
  }
  

}
