package com.xenoage.zong.core.info;


/**
 * Class for a creator (e.g. of a score).
 *
 * @author Andreas Wenger
 */
public final class Creator
{
  
  private final String name;
  private final String type;
  
  
  public Creator(String name, String type)
  {
    this.name = name;
    this.type = type;
  }
  
  
  public String getName()
  {
    return name;
  }
  
  
  public String getType()
  {
    return type;
  }
  
  
}
