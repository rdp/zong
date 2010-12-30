package com.xenoage.zong.io.score;

import static com.xenoage.util.math.Fraction.fr;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.io.score.selections.Selection;

import java.util.Observable;


/**
 * This class stores the selected options for the input engine.
 * 
 * There should be only one active instance per application, because the
 * options (like current note duration) is valid for all open scores.
 * 
 * For example, if the user selects half notes, and switches to another score,
 * still half notes should be selected.
 *
 * @author Andreas Wenger
 */
public class ScoreInputOptions
	extends Observable
{
  
  /**
   * The error level: show and log errors, log errors, ignore.
   */
  public enum ErrorLevel
  {
    ShowAndLog, Log, Ignore, Throw;
  }
  private ErrorLevel errorLevel = ErrorLevel.ShowAndLog;
  
  //the current note duration
  private Fraction duration;
  
  //the current octave
  private int octave;
  
  
  //TODO: end-of-bar-mode: stop or continue (divide note and add slur)
  
  
  /**
   * The write mode: Where to write and how to deal with existing elements?
   * Notice, that is is only a hint for the input classes.
   * Each implementation may handle these modes in a different
   * way. See the documentation of the {@link Selection} implementations.
   */
  public enum WriteMode
  {
    /** Overwrite the element after the cursor. */
    Replace,
    /** Add the element to the chord before the cursor. */
    ChordBeforeCursor;
    //TODO: Chord, Insert;
  }
  private WriteMode writeMode = WriteMode.Replace;
  
  
  /**
   * Creates a new instance of ScoreInputOptions.
   */
  public ScoreInputOptions()
  {
    //default duration are quarter notes
    duration = fr(1, 4);
    //default octave is 4
    octave = 4;
  }

  
  /**
   * Gets the currently selected note duration.
   */
  public Fraction getDuration()
  {
    return duration;
  }
  
  
  /**
   * Gets the currently selected octave.
   */
  public int getOctave()
  {
    return octave;
  }
  
  
  /**
   * Gets a copy of the currently selected note duration
   * as a fraction.
   */
  public Fraction getFraction()
  {
    return duration;
  }
  
  
  /**
   * Sets the currently selected note duration.
   */
  public void setDuration(Fraction duration)
  {
    this.duration = duration;
    //notify the observers
    setChanged();
    notifyObservers();
  }


  /**
   * Gets the current error level, i.e. if
   * errors are shown, logged or ignored.
   */
  public ErrorLevel getErrorLevel()
  {
    return errorLevel;
  }


  /**
   * Sets the current error level, i.e. if
   * errors are shown, logged or ignored.
   */
  public void setErrorLevel(ErrorLevel errorLevel)
  {
    this.errorLevel = errorLevel;
    notifyObservers();
  }


  /**
   * Gets the current write mode.
   */
  public WriteMode getWriteMode()
  {
    return writeMode;
  }


  /**
   * Sets the current write mode.
   */
  public void setWriteMode(WriteMode writeMode)
  {
    this.writeMode = writeMode;
    notifyObservers();
  }
  

}
