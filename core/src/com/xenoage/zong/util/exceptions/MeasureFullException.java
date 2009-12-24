package com.xenoage.zong.util.exceptions;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.ScorePosition;


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
  extends Exception
{
  
  private ScorePosition pos;
  private Fraction requestedDuration;
  
  
  public MeasureFullException(ScorePosition pos, Fraction requestedDuration)
  {
    this.pos = pos;
    this.requestedDuration = requestedDuration;
  }
  
  
  public MeasureFullException(Fraction beat, Fraction requestedDuration)
  {
    this.pos = new ScorePosition(1, -1, beat, -1);
    this.requestedDuration = requestedDuration;
  }
  
  
  @Override public String getMessage()
  {
    return "Measure is full. Requested duration = " +
    	requestedDuration + ". Score position: " + pos;
  }

}
