package com.xenoage.zong.core.music;

import com.xenoage.pdlib.PMap;
import com.xenoage.zong.core.music.chord.Accidental;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.clef.ClefType;
import com.xenoage.zong.core.music.key.Key;
import com.xenoage.zong.core.music.key.TraditionalKey;


/**
 * A music context provides information about the current musical state.
 * 
 * These are the current clef, the key signature,
 * the pitch of the bottom line and the list of accidentals.
 *
 * @author Andreas Wenger
 */
public class MusicContext
{
  
	//current clef
  private final Clef clef;
  
  //current key signature
  private final Key key;
  
  //current accidentals (key: pitch without alter, value: alter)
  private final PMap<Pitch, Byte> accidentals;
  
  
  /**
   * Creates a context with the given clef, key
   * and list of accidentals.
   */
  public MusicContext(Clef clef, Key key, Pitch[] accidentals)
  {
    this.clef = clef;
    this.key = key;
    PMap<Pitch, Byte> accidentalsMap = new PMap<Pitch, Byte>();
    for (Pitch acc : accidentals)
    {
    	accidentalsMap = accidentalsMap.plus(acc.withoutAlter(), acc.getAlter());
    }
    this.accidentals = accidentalsMap;
  }
  
  
  /**
   * Creates a context with the given clef, key
   * and list of accidentals.
   */
  public MusicContext(Clef clef, Key key, PMap<Pitch, Byte> accidentals)
  {
    this.clef = clef;
    this.key = key;
    this.accidentals = accidentals;
  }
  
  
  /**
   * Creates a simple context with a g-clef,
   * C major and no accidentals.
   */
  public MusicContext()
  {
    this.clef = new Clef(ClefType.G);
    this.key = new TraditionalKey(0);
    this.accidentals = new PMap<Pitch, Byte>();
  }


  public Clef getClef()
  {
    return clef;
  }
  
  
  public Key getKey()
  {
    return key;
  }
  
  
  /**
   * Gets the accidentals, or an empty map if there are none.
   */
  public PMap<Pitch, Byte> getAccidentals()
  {
    return accidentals;
  }
  
  
  /**
   * Computes and returns the line position of the given pitch,
   * that is the vertical offset of the note in half-spaces
   * from the bottom line. Some examples:
   * <ul>
   *   <li>0: note is on the bottom line</li>
   *   <li>-2: note is on the first lower leger line</li>
   *   <li>5: note is between the 3rd and 4th line from below</li>
   * </ul>
   */
  public int computeLinePosition(Pitch pitch)
  {
    return clef.getType().computeLinePosition(pitch);
  }

  
  /**
   * Gets the accidental for the given pitch.
   * When no accidental is needed, null is returned.
   */
  public Accidental.Type getAccidentalType(Pitch pitch)
  {
    //look, if this pitch is already set as an accidental
  	Byte alter = accidentals.get(pitch.withoutAlter());
    if (alter != null)
    {
    	if (alter == pitch.getAlter())
    	{
	    	//we need no accidental
	    	return null;
    	}
	    else
	    {
    		//we need to show the accidental
    		return Accidental.Type.getType(pitch.getAlter());
      }
    }
    //look, if this alteration is already set in the key signature
    if (key.getAlterations()[pitch.getStep()] == pitch.getAlter())
    {
    	//we need no accidental
      return null;
    }
    else
    {
	    //we need to show the accidental
	    return Accidental.Type.getType(pitch.getAlter());
    }
  }
  

}
