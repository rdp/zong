package com.xenoage.zong.core.music;

import com.xenoage.util.math.Fraction;


/**
 * Interface for all classes that are child of a voice
 * and have a duration.
 * 
 * These are chords and rests.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public interface VoiceElement
	extends MusicElement
{
	
	/**
   * Gets the duration of this element.
   */
  public Fraction getDuration();
  
  
  /**
   * Returns this element but with the given duration.
   */
  public VoiceElement withDuration(Fraction duration);
	
}
