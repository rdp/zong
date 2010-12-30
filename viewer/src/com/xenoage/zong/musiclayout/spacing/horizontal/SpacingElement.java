package com.xenoage.zong.musiclayout.spacing.horizontal;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.MusicElement;


/**
 * A spacing element stores the beat and the position (offset)
 * of a given {@link MusicElement} in a layout.
 * 
 * All units are measured in interline spaces.
 *
 * @author Andreas Wenger
 */
public final class SpacingElement
{
  
  private final MusicElement element;
  private final Fraction beat;
  private final float offset;
  
  
  public SpacingElement(MusicElement element, Fraction beat, float offset)
  {
    this.element = element;
    this.beat = beat;
    this.offset = offset;
  }
  
  
  public MusicElement getElement()
  {
    return element;
  }
  
  
  /**
   * Gets the horizontal offset of the element in interline spaces.
   */
  public float getOffset()
  {
    return offset;
  }
  
  
  /**
   * Returns a copy of this {@link SpacingElement}, but using the
   * given the horizontal offset.
   */
  public SpacingElement withOffset(float offset)
  {
    return new SpacingElement(element, beat, offset);
  }
  
  
  public Fraction getBeat()
  {
    return beat;
  }
  
  
  @Override public String toString()
  {
  	return element + " at " + beat + ": " + offset;
  }

}
