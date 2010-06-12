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
  
  //cue size
  private final boolean cue;
  
  
  public Rest(Fraction duration, boolean cue)
  {
    this.duration = duration;
    this.cue = cue;
  }
  
  
  public Rest(Fraction duration)
  {
    this(duration, false);
  }
  
  
  @Override public Fraction getDuration()
  {
    return duration;
  }
  
  
  @Override public Rest withDuration(Fraction duration)
  {
  	return new Rest(duration, cue);
  }
  
  
  public boolean isCue()
  {
  	return cue;
  }
  
  
  public Rest withCue(boolean cue)
  {
  	return new Rest(duration, cue);
  }

  
  @Override public String toString()
  {
  	return "rest(dur:" + duration + ")";
  }

}
