package com.xenoage.zong.app.opengl;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import com.xenoage.zong.app.App;
import com.xenoage.util.io.IO;


/**
 * An {@link ImageFrameTexture} consists of a texture,
 * its path and the last access time.
 * 
 * @author Andreas Wenger
 */
public class ImageFrameTexture
	implements TimestampedTexture
{
  
  private String path;
  private long timeStamp;
  private Texture texture;
  
  
  /**
   * Creates a new {@link ImageFrameTexture} and loads the
   * texture from the given path.
   */
  public ImageFrameTexture(String path)
  {
    this.path = path;
    load();
    updateTimeStamp();
  }
  
  
  /**
   * Creates a new {@link ImageFrameTexture} and uses the
   * given image as the texture.
   */
  public ImageFrameTexture(String path, BufferedImage image)
  {
    this.path = path;
    load(image);
    updateTimeStamp();
  }
  
  
  public long getTimeStamp()
  {
    return timeStamp;
  }

  
  private void updateTimeStamp()
  {
    this.timeStamp = System.currentTimeMillis();
  }
  
  
  /**
   * Returns the texture.
   */
  public Texture getTexture()
  {
    updateTimeStamp();
    return texture;
  }
  
  
  /**
   * Loads the texture from the known path.
   */
  public void load()
  {
    //waiting cursor
  	// - TODO: better mechanism for waiting cursor! for example, when drawing,
  	// measure the time. when more than 50 ms needed, display waiting cursor
  	// (this way, a waiting cursor is implicitly added for all kinds of long operations)
    //App.getInstance().setCursorWaiting(true);
    //load texture
    try
    {
      texture = TextureIO.newTexture(
        IO.openDataFile(path), true, "png");
    }
    catch (IOException ex)
    {
    }
    //remove waiting cursor
    //App.getInstance().setCursorWaiting(false);
  }
  
  
  /**
   * Loads the texture from the given AWT buffered image.
   */
  public void load(BufferedImage image)
  {
    //waiting cursor
    App.getInstance().setCursorWaiting(true);
    //load texture
    texture = TextureIO.newTexture(image, true);
    //remove waiting cursor
    App.getInstance().setCursorWaiting(false);
  }
  
  
  /**
   * Disposes the used texture to clean memory up faster.
   */
  public void dispose()
  {
  	texture.dispose();
  }


}
