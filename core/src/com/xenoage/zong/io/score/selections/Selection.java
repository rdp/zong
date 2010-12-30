package com.xenoage.zong.io.score.selections;

import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.ColumnElement;
import com.xenoage.zong.core.music.MeasureElement;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.core.music.VoiceElement;
import com.xenoage.zong.io.score.ScoreInputOptions;
import com.xenoage.zong.util.exceptions.IllegalMPException;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * Interface for selections.
 * 
 * A simple cursor, a list of elements or staves and many
 * other things may form a selection.
 * 
 * A selection always contains a score. When one of the
 * write methods is called, the selection is transformed in
 * some way, maybe dependent on the given {@link ScoreInputOptions},
 * and returned. Of course also its score may be
 * transformed, and it can be retrieved by calling the
 * {@link #getScore()} method.
 *
 * @author Andreas Wenger
 */
public interface Selection
{
	
	
	/**
	 * Gets the score behind the selection.
	 */
	public Score getScore();
	
	
	/**
	 * Sets the score behind the selection without changing
	 * the currently selected are (if possible).
	 */
	public Selection withScore(Score score);
  
  
  /**
   * Writes the given pitches, e.g. as a chord.
   * Each type of selection may have a different behavior.
   */
  public Selection write(Pitch[] pitches, ScoreInputOptions options)
    throws IllegalMPException, MeasureFullException;
  
  
  /**
   * Writes the given {@link ColumnElement}.
   * Each type of selection may have a different behavior.
   */
  public Selection write(ColumnElement element)
    throws IllegalMPException, MeasureFullException;
  
  
  /**
   * Writes the given {@link ColumnElement}.
   * Each type of selection may have a different behavior.
   */
  public Selection write(ColumnElement element, ScoreInputOptions options)
    throws IllegalMPException, MeasureFullException;
  
  
  /**
   * Writes the given {@link VoiceElement}.
   * Each type of selection may have a different behavior.
   */
  public Selection write(VoiceElement element)
    throws IllegalMPException, MeasureFullException;
  
  
  /**
   * Writes the given {@link VoiceElement}.
   * Each type of selection may have a different behavior.
   */
  public Selection write(VoiceElement element, ScoreInputOptions options)
    throws IllegalMPException, MeasureFullException;
  
  
  /**
   * Writes the given {@link MeasureElement} element.
   * Dependent on its type, it may replace elements of the same type.
   * Each type of selection may have a different
   * behavior.
   */
  public Selection write(MeasureElement element)
    throws IllegalMPException;
  
  
  /**
   * Writes the given {@link MeasureElement} element.
   * Dependent on its type, it may replace elements of the same type.
   * Each type of selection may have a different
   * behavior.
   */
  public Selection write(MeasureElement element, ScoreInputOptions options)
    throws IllegalMPException;

}
