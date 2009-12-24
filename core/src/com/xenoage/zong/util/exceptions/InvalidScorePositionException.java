package com.xenoage.zong.util.exceptions;

import com.xenoage.zong.data.ScorePosition;


/**
 * This exception is thrown when an invalid
 * score position is used.
 * 
 * It is a {@link RuntimeException} which needs not to be
 * caught (but it should be wherever it is useful).
 *
 * @author Andreas Wenger
 */
@SuppressWarnings("serial")
public class InvalidScorePositionException
  extends RuntimeException
{
  
  private ScorePosition pos;
  
  
  public InvalidScorePositionException(ScorePosition pos)
  {
    this.pos = pos;
  }
  
  
  @Override public String getMessage()
  {
    return "Invalid score position: " + pos;
  }

}
