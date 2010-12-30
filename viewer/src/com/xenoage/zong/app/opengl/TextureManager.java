package com.xenoage.zong.app.opengl;

import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Hashtable;

import com.sun.opengl.util.texture.Texture;


/**
 * This class manages the OpenGL textures
 * of one GL instance.
 * 
 * The textures are managed in different
 * categories: application textures (always
 * needed, always available) and image textures
 * (some cached, others loaded on demand).
 *
 * @author Andreas Wenger
 */
public class TextureManager
{
  
	//TIDY: enums
  public static final int IDBASE_SYMBOLS = 1000;
  public static final int IDBASE_PAPERS = 2000;
  public static final int IDBASE_DESKTOPS = 3000;
  public static final int IDBASE_SHADOWS = 4000;
  
  public static final int ID_HANDLE_RESIZE = 5000;
  public static final int ID_HANDLE_RESIZE_HOVER = 5001;
  public static final int ID_HANDLE_ROTATE = 5002;
  public static final int ID_HANDLE_ROTATE_HOVER = 5003;
  public static final int ID_WARNING = 5050;
  
  public static final int ID_GUI_ARROW = 6000; //only editor
  public static final int ID_GUI_BUTTON_OPEN = 6100; //only viewer
  public static final int ID_GUI_BUTTON_SAVE = 6101; //only viewer
  public static final int ID_GUI_BUTTON_PRINT = 6102; //only viewer
  public static final int ID_GUI_BUTTON_PLAY_TUTTI = 6103; //only viewer
  public static final int ID_GUI_BUTTONPANEL = 6200; //only viewer
  public static final int ID_GUI_LOGO_VIEWER = 6300; //only viewer
  public static final int ID_GUI_TOOLTIP = 6400;
  public static final int ID_GUI_TOOLTIP_SMALL = 6401;
  
  public static final int TEXT_TEXTURE_SIZE = 512;
  
  //application textures: these are the textures for the symbols,
  //papers, desktops, shadows and so on
  private Hashtable<Integer, Texture> appTextures;
  
  //image frame textures: these are the textures, that contain
  //the images of image frames. since the video memory is limited,
  //only a limited number of textures is loaded at the same time.
  private Hashtable<String, ImageFrameTexture> imageFrameTextures;
  //number of loaded image frame textures
  private int maxImageFrameTextures;
  //when a texture was not rendered the following time in ms,
  //it is obsolete and can be removed from the cache, freeing
  //memory for another image
  private static final long IMAGE_TEXTURE_TIMEOUT = 5000;
  
  
  
  public TextureManager(int maxImageFrameTextures)
  {
    //create hashtable for app textures
    this.appTextures = new Hashtable<Integer, Texture>();
    //create hashtable for image textures
    this.imageFrameTextures = new Hashtable<String, ImageFrameTexture>();
    this.maxImageFrameTextures = maxImageFrameTextures;
  }
  
  
  /**
   * Adds a new application texture that will
   * always be available immediately.
   */
  public void addAppTexture(Texture texture, int textureID)
    throws IllegalArgumentException
  {
    if (appTextures.get(textureID) != null)
    {
      appTextures.remove(textureID).dispose();
    }
    appTextures.put(textureID, texture);
  }
  
  
  /**
   * Gets the application texture with the given ID, or
   * null if not found.
   */
  public Texture getAppTexture(int textureID)
  {
  	return appTextures.get(textureID);
  }
  
  
  /**
   * Activates the application texture with the given ID, or
   * null if not found.
   */
  public void activateAppTexture(int textureID)
  {
    Texture tex = appTextures.get(textureID);
    if (tex != null)
    {
      tex.bind();
    }
  }
  
  
  /**
   * Gets the image frame texture with the given ID, or null.
   * If loaded, it is returned.
   * If not loaded, it is checked if other image frame textures can
   * be replaced. If yes, this texture is loaded and returned.
   * Otherwise null is returned.
   */
  public Texture getImageFrameTexture(String path)
  {
  	return getImageFrameTexture(path, false);
  }
  
  
  /**
   * Gets the image texture with the given ID, or null.
   * If loaded, it is returned.
   * If not loaded, it is checked if other textures can
   * be replaced. If yes, this texture is loaded and returned.
   * Otherwise null is returned.
   * If force is true, the loading of the texture is always
   * done (the oldest image frame texture will be replaced)
   * and null will not be returned.
   */
  public Texture getImageFrameTexture(String path, boolean force)
  {
    ImageFrameTexture tex = imageFrameTextures.get(path);
    //texture loaded?
    if (tex != null)
    {
      //texture is loaded. return it.
      return tex.getTexture();
    }
    else
    {
      //texture is not loaded. try to make room for it.
    	boolean isFree = removeObsoleteImageFrameTexture(force);
    	if (isFree)
    	{
    		//there is space for the texture. load and return it.
    		tex = new ImageFrameTexture(path);
    		imageFrameTextures.put(path, tex);
    		return tex.getTexture();
      }
    	else
    	{
	      //no space found
	      return null;
    	}
    }
  }
  
  
  /**
   * Searches an obsolete image frame texture. If found,
   * it is removed and true is returned. If not found,
   * nothing is changed and false is returned.
   * If the maximum number of loaded image frame textures
   * is not reached yet, true is returned and nothing is done.
   * @param force  if true, the oldest texture will be replaced,
   *               even if it is not obsolete, and true is returned.
   */
  private boolean removeObsoleteImageFrameTexture(boolean force)
  {
  	//maximum number of allowed loaded image frame texture not reached yet?
  	if (imageFrameTextures.size() < maxImageFrameTextures)
  		return true;
  	//find oldest image frame texture
  	long minTime = Long.MAX_VALUE;
  	String minKey = null;
  	Enumeration<String> keys = imageFrameTextures.keys();
    while(keys.hasMoreElements())
    {
    	String key = keys.nextElement();
    	ImageFrameTexture tex = imageFrameTextures.get(key);
    	if (tex.getTimeStamp() < minTime)
    	{
    		minTime = tex.getTimeStamp();
    		minKey = key;
    	}
    }
    if (minKey != null)
    {
    	//if force flag is not set, look if texture is obsolete before deleting it
    	//if force flag is set, delete always
    	if (force || System.currentTimeMillis() - IMAGE_TEXTURE_TIMEOUT > minTime)
    	{
    		imageFrameTextures.remove(minKey).dispose();
    		return true;
    	}
    }
    //could not remove a texture
    return false;
  }
  
  
  /**
   * Loads the given AWT buffered image as an image texture, if
   * not already loaded, and returns it (or null).
   * @param force  if true, the texture is loaded, even when there is no space for it.
   */
  public Texture loadImageFrameTexture(String path, BufferedImage image, boolean force)
  {
    ImageFrameTexture tex = imageFrameTextures.get(path);
    //texture loaded?
    if (tex != null)
    {
      //texture is loaded. return it.
      return tex.getTexture();
    }
    else
    {
      //texture is not loaded. try to make room for it.
    	boolean isFree = removeObsoleteImageFrameTexture(force);
    	if (isFree)
    	{
    		//there is space for the texture. load and return it.
    		tex = new ImageFrameTexture(path, image);
    		imageFrameTextures.put(path, tex);
    		return tex.getTexture();
      }
    	else
    	{
	      //no space found
	      return null;
    	}
    }
  }


}
