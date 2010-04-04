package com.xenoage.zong.musiclayout.notations;

import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.musiclayout.spacing.horizontal.ElementWidth;


/**
 * This class contains layout information
 * about a rest.
 *
 * @author Andreas Wenger
 */
public class RestNotation
  implements Notation
{
  
  private Rest element;
  private ElementWidth width;
  
  
  /**
   * Creates a new RestElementLayout for the given
   * chord with the given width.
   */
  public RestNotation(Rest element, ElementWidth width)
  {
    this.element = element;
    this.width = width;
  }
  
  
  public ElementWidth getWidth()
  {
    return width;
  }
  
  
  /**
   * Gets the rest.
   */
  public Rest getMusicElement()
  {
    return element;
  }
  

}
