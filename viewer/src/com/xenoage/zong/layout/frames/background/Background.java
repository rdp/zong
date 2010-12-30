package com.xenoage.zong.layout.frames.background;

import java.awt.Paint;

import javax.media.opengl.GL;


/**
 * Interface for a background of a frame.
 * 
 * @author Andreas Wenger
 */
public interface Background
{
  
  /**
   * Returns the Paint instance of this background.
   */
  public Paint getPaint();
  
  
  /**
   * Applies the background to the given GL context.
   */
  public void apply(GL gl);

}
