package com.xenoage.zong.app.symbols;

import com.xenoage.util.math.TextureRectangle2f;


/**
 * Class for the texture version of a symbol.
 * 
 * This class contains the ID of the texture
 * and a TextureRectangle2f instance for the
 * texture coordinates (so symbols can share
 * a texture by using just a portion of it).
 *
 * @author Andreas Wenger
 */
public class SymbolTexture
{
  
  private int textureID;
  private TextureRectangle2f texCords;
  
  
  public SymbolTexture(int textureID, TextureRectangle2f texCords)
  {
    this.textureID = textureID;
    this.texCords = texCords;
  }

  
  public int getTextureID()
  {
    return textureID;
  }
  
  
  public TextureRectangle2f getTexCords()
  {
    return texCords;
  }


}
