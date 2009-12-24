package com.xenoage.zong.data.music;

import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.key.Key;
import com.xenoage.zong.data.music.key.TraditionalKey;

import java.util.*;


/**
 * A music context provides information
 * about the current musical state.
 * 
 * These are the current clef, the
 * key signature, the number of lines,
 * the pitch of the bottom line and
 * the list of accidentals.
 * 
 * TODO: 8va, 8vb and other things
 *
 * @author Andreas Wenger
 */
public class MusicContext
{
  
	//current clef
  private Clef clef;
  
  //current key signature
  private Key key;
  
  //current accidentals (key: pitch without alter, value: alter)
  private Hashtable<Pitch, Byte> accidentals = new Hashtable<Pitch, Byte>();
  
  
  /**
   * Creates a context with the given clef, key
   * and list of accidentals.
   */
  public MusicContext(Clef clef, Key key, Pitch[] accidentals)
  {
    this.clef = clef;
    this.key = key;
    for (Pitch acc : accidentals)
    {
      this.accidentals.put(acc.cloneWithoutAlter(), acc.getAlter());
    }
  }
  
  
  /**
   * Creates a context with the given clef, key
   * and list of accidentals.
   */
  public MusicContext(Clef clef, Key key, Hashtable<Pitch, Byte> accidentals)
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
    this.clef = new Clef(getDefaultClefType());
    this.key = new TraditionalKey(0);
  }


  public Clef getClef()
  {
    return clef;
  }
  
  
  public void setClef(Clef clef)
  {
    this.clef = clef;
  }
  
  
  public Key getKey()
  {
    return key;
  }
  
  
  public void setKey(Key key)
  {
    this.key = key;
  }
  
  
  /**
   * Gets the accidentals, or an empty hashtable if there
   * are none.
   */
  public Hashtable<Pitch, Byte> getAccidentals()
  {
    return accidentals;
  }
  
  
  public int getLines()
  {
    return 5; //TODO
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
    return clef.computeLinePosition(pitch);
  }

  
  /**
   * Gets the accidental for the given pitch.
   * When no accidental is needed, null is returned.
   */
  public Accidental.Type getAccidentalType(Pitch pitch)
  {
    //look, if this pitch is already set as an accidental
  	Byte alter = accidentals.get(pitch.cloneWithoutAlter());
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
  
  
  /**
   * Gets the default clef type, if none is set.
   */
  private ClefType getDefaultClefType()
  {
    return ClefType.G;
  }
  

}
