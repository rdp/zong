package com.xenoage.zong.app.opengl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.media.opengl.GL;

import org.junit.Test;


/**
 * Test cases for the TextureManager.
 *
 * @author Andreas Wenger
 */
public class TextureManagerTest
{
  
  /**
   * Tests the getImageTexture-method.
   * TODO: http://www.javagaming.org/forums/index.php?topic=17276.msg135762
   */
  @Test public void getImageTexture()
  {
    //create GL frame to initialize OpenGL
    GL gl = OpenGLCaps.createGL();
    if (gl == null)
    {
      //unable to create a GL context. This is ok, but we can not test the method.
      return;
    }
    //create texture manager and allow 4 loaded image textures
    TextureManager texMan = new TextureManager(4);
    //get the first 4 textures. They must be loaded and returned.
    for (int i = 1; i <= 4; i++)
    {
      assertNotNull(texMan.getImageFrameTexture(getImagePath(i)));
    }
    //the next 3 ones are not loaded.
    for (int i = 5; i <= 7; i++)
    {
      assertNull(texMan.getImageFrameTexture(getImagePath(i)));
    }
  }
  
  
  private String getImagePath(int index)
  {
    return "data/test/images/flag" + index + ".png";
  }

}
