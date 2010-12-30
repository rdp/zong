package com.xenoage.zong.core.info;


/**
 * Class for rights.
 *
 * @author Andreas Wenger
 */
public final class Rights
{
  
  private final String text;
  private final String type;
  
  
  public Rights(String name, String type)
  {
    this.text = name;
    this.type = type;
  }
  
  
  public String getText()
  {
    return text;
  }
  
  
  public String getType()
  {
    return type;
  }
  
  
}
