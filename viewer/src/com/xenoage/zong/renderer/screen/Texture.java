package com.xenoage.zong.renderer.screen;

import com.xenoage.zong.util.Screen;
import com.xenoage.util.io.IO;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


/**
 * Class for a texture, e.g. for
 * paper or desktop background.
 *
 * @author Andreas Wenger
 */
public class Texture
{
  
  private Paint paint = Color.gray;
  

  public Texture(String file)
  {
    try
    {
      BufferedImage tex = ImageIO.read(IO.openDataFile(file));
      GraphicsConfiguration gc = Screen.getGraphicsConfiguration();
      BufferedImage texComp =
        gc.createCompatibleImage(tex.getWidth(), tex.getHeight());
      Graphics2D gTexComp = texComp.createGraphics();
      gTexComp.drawImage(tex, 0, 0, null);
      gTexComp.dispose();
      paint = new TexturePaint(texComp,
        new Rectangle(tex.getWidth(), tex.getHeight()));
    }
    catch (Exception ex)
    {
    }
  }

  
  public Paint getPaint()
  {
    return paint;
  }
  
}
