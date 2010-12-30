package com.xenoage.zong.app.opengl;

import com.sun.opengl.util.texture.Texture;


/**
 * A timestamped texture consists of a texture
 * and the last access time.
 * 
 * Usually a <code>getTexture</code> call
 * will update the time stamp to the
 * current time.
 * 
 * @author Andreas Wenger
 */
public interface TimestampedTexture
{
  
  /**
   * Returns the texture, or null, if not loaded.
   */
  public Texture getTexture();
  
  
  /**
   * Gets the timestamp of the texture.
   */
  public long getTimeStamp();


}
