package com.xenoage.zong.data.info;


/**
 * Class for a creator (e.g. of a score).
 *
 * @author Andreas Wenger
 */
public class Creator
{
  
  private String name = null;
  private String type = null;
  
  
  public Creator(String name, String type)
  {
    this.name = name;
    this.type = type;
  }
  
  
  public String getName()
  {
    return name;
  }
  
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  
  public String getType()
  {
    return type;
  }
  
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  
}
