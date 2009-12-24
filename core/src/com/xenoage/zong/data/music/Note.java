package com.xenoage.zong.data.music;

import static com.xenoage.util.exceptions.ThrowableTools.throwNullArg;

import com.xenoage.util.math.Fraction;
import com.xenoage.util.InstanceID;


/**
 * Class for a single note.
 * It may be "standalone" or part of a chord.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class Note
  implements PitchElement
{
  
  //the pitch
  private final Pitch pitch;
  
  //the notehead
  private final Notehead notehead;
  
  private final InstanceID instanceID = new InstanceID();
  
  
  public Note(Pitch pitch)
  {
  	throwNullArg(pitch);
    this.pitch = pitch;
    this.notehead = new Notehead();
  }
  
  public Note(Pitch pitch, Notehead notehead)
  {
	  this.pitch = pitch;
	  this.notehead = notehead;
  }
  

  public Pitch getPitch()
  {
    return pitch;
  }
  
  
  public Notehead getNotehead()
  {
    return notehead;
  }

  
  /**
   * A note has no duration (only a chord has one),
   * so null is returned.
   */
  public Fraction getDuration()
  {
    return null;
  }
  
  
  public InstanceID getInstanceID()
  {
  	return instanceID;
  }
  
  
  /**
   * Returns an array of {@link Note}s from the given {@link Pitch}es.
   */
  public static Note[] createNotes(Pitch... pitches)
  {
  	Note[] ret = new Note[pitches.length];
    for (int i = 0; i < pitches.length; i++)
    {
    	ret[i] = new Note(pitches[i]);
    }
    return ret;
  }
  

}
