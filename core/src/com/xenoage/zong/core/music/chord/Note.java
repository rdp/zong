package com.xenoage.zong.core.music.chord;

import static com.xenoage.pdlib.IVector.ivec;
import static com.xenoage.util.NullTools.throwNullArg;

import com.xenoage.pdlib.IVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.Pitch;


/**
 * Class for a single note.
 * Within a score, it is always part of a chord.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class Note
{
  
  private final Pitch pitch;
  
  
  public Note(Pitch pitch)
  {
  	throwNullArg(pitch);
    this.pitch = pitch;
  }
  

  public Pitch getPitch()
  {
    return pitch;
  }
  
  
  /**
   * A note has no duration (only a chord has one),
   * so null is returned.
   */
  public Fraction getDuration()
  {
    return null;
  }
  
  
  /**
   * Returns a list of {@link Note}s from the given {@link Pitch}es.
   */
  public static IVector<Note> createNotes(Pitch... pitches)
  {
  	Note[] ret = new Note[pitches.length];
    for (int i = 0; i < pitches.length; i++)
    {
    	ret[i] = new Note(pitches[i]);
    }
    return ivec(ret);
  }
  
  
  /**
   * Returns a list of {@link Note}s from the given {@link Pitch}es.
   */
  public static IVector<Note> createNotes(Vector<Pitch> pitches)
  {
  	Note[] ret = new Note[pitches.size()];
    for (int i = 0; i < pitches.size(); i++)
    {
    	ret[i] = new Note(pitches.get(i));
    }
    return ivec(ret);
  }
  
  
  @Override public String toString()
  {
  	return "note" + pitch.toString();
  }


}
