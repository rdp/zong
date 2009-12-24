package com.xenoage.zong.data.info;


/**
 * Class for rights (e.g. of a score).
 *
 * @author Andreas Wenger
 */
public class Rights
{
  
  private String text = null;
  private String type = null;
  
  
  public Rights(String name, String type)
  {
    this.text = name;
    this.type = type;
  }
  
  
  public String getText()
  {
    return text;
  }
  
  
  public void setText(String text)
  {
    this.text = text;
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
