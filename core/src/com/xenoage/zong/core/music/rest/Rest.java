package com.xenoage.zong.core.music.rest;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.VoiceElement;


/**
 * Class for a rest.
 *
 * @author Andreas Wenger
 */
public final class Rest
  implements VoiceElement
{
	
	//the duration of the rest
  private final Fraction duration;
  
  
  public Rest(Fraction duration)
  {
    this.duration = duration;
  }
  
  
  @Override public Fraction getDuration()
  {
    return duration;
  }
  
  
  /**
   * Returns this element but with the given duration.
   */
  @Override public Rest withDuration(Fraction duration)
  {
  	return new Rest(duration);
  }


}
