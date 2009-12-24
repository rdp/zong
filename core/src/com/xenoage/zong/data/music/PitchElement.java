package com.xenoage.zong.data.music;


/**
 * Interface for elements that have a notated pitch,
 * like notes and rests.
 *
 * @author Andreas Wenger
 */
public interface PitchElement
{
  
  /**
   * Gets the pitch of this element.
   */
  public Pitch getPitch();
  
}
