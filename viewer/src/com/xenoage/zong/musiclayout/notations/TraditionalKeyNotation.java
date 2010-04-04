package com.xenoage.zong.musiclayout.notations;

import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.musiclayout.spacing.horizontal.ElementWidth;


/**
 * This class contains layout information
 * about a traditional key signature.
 *
 * @author Andreas Wenger
 */
public class TraditionalKeyNotation
  implements Notation
{
  
  private TraditionalKey element;
  private ElementWidth width;
  private int linePositionC4;
  private int linePositionMin;
  
  
  /**
   * Creates a new TraditionalKeyElementLayout for the given key.
   */
  public TraditionalKeyNotation(TraditionalKey element, ElementWidth width,
    int linePositionC4, int linePositionMin)
  {
    this.element = element;
    this.width = width;
    this.linePositionC4 = linePositionC4;
    this.linePositionMin = linePositionMin;
  }
  
  
  public ElementWidth getWidth()
  {
    return width;
  }
  
  
  /**
   * Gets the key.
   */
  public TraditionalKey getMusicElement()
  {
    return element;
  }

  
  public int getLinePositionC4()
  {
    return linePositionC4;
  }

  
  public int getLinePositionMin()
  {
    return linePositionMin;
  }
  

}
