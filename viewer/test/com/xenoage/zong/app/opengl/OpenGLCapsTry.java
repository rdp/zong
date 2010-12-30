package com.xenoage.zong.app.opengl;

import com.xenoage.util.math.Size2i;

import javax.media.opengl.*;
import javax.swing.JLabel;


/**
 * Test cases for a OpenGLCaps class.
 *
 * @author Andreas Wenger
 */
public class OpenGLCapsTry
  extends GLTestFrame
{
 
	
  public void testGetMaxRGBATextureSize()
  {
    Size2i size = OpenGLCaps.getMaxRGBATextureSize(null);
    System.out.println("Max texture size: " + size.width + " x " + size.height);
  }
  
  
  public void testGetMaxRGBATextureSize(GL gl)
  {
    Size2i size = OpenGLCaps.getMaxRGBATextureSize(gl);
    add(new JLabel("Max texture size: " + size.width + " x " + size.height));
    pack();
  }
  
  
  public static void main(String args[])
  {
    OpenGLCapsTry frm = new OpenGLCapsTry();
    frm.setVisible(true);
  }
  
  
  @Override public void init(GLAutoDrawable d)
  {
    testGetMaxRGBATextureSize(d.getGL());
  }

}
