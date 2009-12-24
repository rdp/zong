package com.xenoage.zong.io.score.selections;

import com.xenoage.zong.data.music.ChordData;
import com.xenoage.zong.data.music.NoVoiceElement;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.RestData;
import com.xenoage.zong.data.music.VoiceElement;
import com.xenoage.zong.util.exceptions.InvalidScorePositionException;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * Interface for selections.
 * 
 * A simple cursor, a list of elements
 * or staves and many other things may
 * form a selection.
 *
 * @author Andreas Wenger
 */
public interface Selection
{
  
  /**
   * Invoked when a key has been pressed. 
   * Each type of selection may react differently.
   */
  //TODO no. not here. public void keyPressed(ScoreKeyEvent e);
  
  
  /**
   * Writes the given pitches, e.g. as a chord.
   * Each type of selection may have a different
   * behavior.
   */
  public void write(Pitch[] pitches)
    throws InvalidScorePositionException, MeasureFullException, Exception;
  
  
  /**
   * Writes the {@link ChordData}.
   * Each type of selection may have a different
   * behavior.
   */
  public void write(ChordData chordData)
    throws InvalidScorePositionException, MeasureFullException, Exception;
  
  
  /**
   * Writes the {@link RestData}.
   * Each type of selection may have a different
   * behavior.
   */
  public void write(RestData restData)
    throws InvalidScorePositionException, MeasureFullException, Exception;
  
  
  /**
   * Writes the given {@link NoVoiceElement} element.
   * Each type of selection may have a different
   * behavior.
   */
  public void write(NoVoiceElement element)
    throws InvalidScorePositionException, Exception;
  
  
  /**
   * Writes the given {@link VoiceElement} element.
   * Each type of selection may have a different
   * behavior.
   */
  public void write(VoiceElement element)
    throws InvalidScorePositionException, MeasureFullException, Exception;
  

}
