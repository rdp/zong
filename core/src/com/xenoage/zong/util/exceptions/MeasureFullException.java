package com.xenoage.zong.util.exceptions;

import static com.xenoage.zong.core.music.MP.atBeat;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.MP;


/**
 * This exception is thrown when an element
 * should be added to a measure, but there
 * were not enough beats for it between
 * the insert position and the end bar line.
 * 
 * @author Andreas Wenger
 */
@SuppressWarnings("serial")
public class MeasureFullException
  extends RuntimeException
{
  
  private MP mp;
  private Fraction requestedDuration;
  
  
  public MeasureFullException(MP mp, Fraction requestedDuration)
  {
    this.mp = mp;
    this.requestedDuration = requestedDuration;
  }
  
  
  public MeasureFullException(Fraction beat, Fraction requestedDuration)
  {
    this.mp = atBeat(beat);
    this.requestedDuration = requestedDuration;
  }
  
  
  @Override public String getMessage()
  {
    return "Measure is full. Requested duration = " +
    	requestedDuration + ". MP: " + mp;
  }

}
