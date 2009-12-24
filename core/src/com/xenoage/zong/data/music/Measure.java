package com.xenoage.zong.data.music;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.format.ScoreFormat;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.key.Key;
import com.xenoage.zong.data.music.time.Time;
import com.xenoage.zong.data.music.transpose.Transpose;
import com.xenoage.zong.data.music.util.DeepCopyCache;
import com.xenoage.zong.data.music.util.Endpoint;
import com.xenoage.util.RAList;
import com.xenoage.util.SortedList;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * Class for a single measure within a staff.
 * 
 * A measure consists of one or more voices
 * representing the musical voices.
 * 
 * 
 * instead of placing them in voice 0. They must be
 * only set in staff 0 of the measure.
 *
 * @author Andreas Wenger
 */
public class Measure
{	

  //the voices (at least one)
  private ArrayList<Voice> voices = new ArrayList<Voice>();
  
  private Staff parentStaff;
  
  
  
  /**
   * Create a new measure with a single voice.
   */
  public Measure(Staff parentStaff)
  {
    this.parentStaff = parentStaff;
    voices.add(new Voice(this));
  }
  
  
  /**
	 * Returns a deep copy of this {@link Measure}, using the given
	 * parent staff and {@link DeepCopyCache}.
	 */
	public Measure deepCopy(Staff parentStaff, DeepCopyCache cache)
	{
		//create measure
		Measure ret = new Measure(parentStaff);
		//copy voices
		for (Voice v : this.voices)
		{
			ret.voices.add(v.deepCopy(this, cache));
		}
		return ret;
	}
  
  
  /**
   * Adds a clef, time signature or key signature to
   * this measure.
   * TODO: yes, there CAN be a clef that belongs to a single
   * voice. this should be discussed with the MusicXML mailing list.
   */
  public void addNoVoiceElement(NoVoiceElement element)
  {
    //by definition these no-voice-elements are placed in voice 0,
    //that is always present.
  	voices.get(0).addNoVoiceElement(element);
  }
  
  
  /**
   * Adds a voice to the measure and returns it.
   */
  public Voice addVoice()
  {
    Voice ret = new Voice(this);
    voices.add(ret);
    return ret;
  }
  
  
  /**
   * Gets the list of voices.
   * TODO: baaaad :-D
   */
  public List<Voice> getVoices()
  {
    return voices;
  }
  
  
  /**
   * Searches backwards for the last clef at or before the given beat.
   * If there is none, null is returned.
   */
  public Clef getClef(Endpoint endpoint, Fraction beat)
  {
    Clef ret = null;
    Fraction pos = Fraction._0;
    for (MusicElement e : voices.get(0).getElements())
    {
      if (e instanceof Clef)
      	ret = (Clef) e;
      pos = pos.add(e.getDuration());
      if (!endpoint.isBeatInInterval(pos, beat))
      	break;
    }
    return ret;
  }
  
  
  /**
   * Searches backwards for the last clef within this measure.
   * If there is none, null is returned.
   */
  public Clef getLastClef()
  {
  	RAList<MusicElement> elements = voices.get(0).getElements();
    for (int iElement = elements.size() - 1; iElement >= 0; iElement--)
    {
    	MusicElement e = elements.get(iElement);
      if (e instanceof Clef)
      	return (Clef) e;
    }
    return null;
  }
  
  
  /**
   * Gets the key signature of this measure, which must
   * be at beat 0. If there is none, null is returned.
   */
  public Key getKey()
  {
    for (MusicElement e : voices.get(0).getElements())
    {
      if (e instanceof Key)
      	return (Key) e;
      if (e.getDuration() != null)
      	return null;
    }
    return null;
  }
  
  
  /**
   * Gets the time signature of this measure, which must
   * be at beat 0. If there is none, null is returned.
   */
  public Time getTime()
  {
    for (MusicElement e : voices.get(0).getElements())
    {
      if (e instanceof Time)
      	return (Time) e;
      if (e.getDuration() != null)
      	return null;
    }
    return null;
  }
  
  
  /**
   * Searches backwards for a MusicElement of the
   * given class within this measure,
   * beginning at the given beat, ending with the first
   * element of the measure.
   * @param beat  the maximum beat, or null for whole measure
   */
  /* @Deprecated @SuppressWarnings("unchecked") private MusicElement computeElementWithinMeasureAt(
    Class elementClass, Fraction beat)
  {
    List<MusicElement> elements = voices.get(0).getMusicElements();
    Fraction pos = new Fraction(0);
    MusicElement ret = null;
    for (MusicElement element : elements)
    {
      if (elementClass.isInstance(element))
      {
        ret = element;
      }
      pos = pos.add(element.getDuration());
      if (beat != null && pos.compareTo(beat) > 0)
        break;
    }
    return ret;
  } */
  
  
  /**
   * Collect the accidentals within this measure (backwards),
   * beginning at the first element of the measure, ending before
   * the given beat.
   * @param beat  the maximum beat
   * @param key   the key that is valid in this measure
   * @return a hashtable with the pitches that have accidentals (without alter)
   *   as keys and their corresponding alter values as values.
   *  TODO: why only voice 0? TEAM
   */
  public Hashtable<Pitch, Byte> getAccidentalsBeforeBeat(Fraction beat, Key key)
  {
    Fraction pos = Fraction._0;
    Hashtable<Pitch, Byte> ret = new Hashtable<Pitch, Byte>();
    for (MusicElement e : voices.get(0).getElements())
    {
    	if (pos.compareTo(beat) >= 0)
        break;
      if (e instanceof Chord)
      {
        Chord chord = (Chord) e;
        for (Note note : chord.getNotes())
        {
          Pitch pitch = note.getPitch();
          Pitch pitchUnaltered = pitch.cloneWithoutAlter();
          //accidental already set?
          Byte oldAccAlter = ret.get(pitchUnaltered);
          if (oldAccAlter != null)
          {
          	//there is already an accidental. only replace it if alter changed.
          	if (pitch.getAlter() != oldAccAlter)
          	{
          		ret.remove(pitchUnaltered);
          		ret.put(pitchUnaltered, pitch.getAlter());
          	}
          }
          else
          {
            //accidental not neccessary because of key?
            if (key.getAlterations()[pitch.getStep()] == pitch.getAlter())
            {
              //ok, we need no accidental here.
            }
            else
            {
              //add accidental
              ret.put(pitchUnaltered, pitch.getAlter());
            }
          }
        }
      }
      pos = pos.add(e.getDuration());
    }
    return ret;
  }
  
  
  /**
   * Gets the parent score of this measure, or null if undefined.
   */
  protected Score getParentScore()
  {
    if (parentStaff != null)
      return parentStaff.getParentScore();
    else
      return null;
  }
  
  
  /**
   * Gets the number of lines of this measure, or 5 if undefined.
   */
  public int getLinesCount()
  {
  	if (parentStaff != null)
      return parentStaff.getLinesCount();
  	else
  		return 5;
  }
  
  
  /**
   * Gets the interline space of this measure in mm, or the
   * program's default value if undefined.
   */
  public float getInterlineSpace()
  {
  	if (parentStaff != null)
      return parentStaff.getInterlineSpace();
  	else
  		return new ScoreFormat().getInterlineSpace();
  }
  
  
  /**
	 * Gets the parent staff.
	 */
	public Staff getStaff()
	{
		return parentStaff;
	}
  
	
	/**
	 * Gets the index of this measure.
	 */
	public int getIndex()
	{
		//TODO: constant time! e.g. use additional hashmap
		return parentStaff.getMeasures().indexOf(this);
	}
	
	
	/**
   * Gets a list of all beats used in this measure, that means
   * all beats where at least one element with a duration greater than 0 begins.
   * Beat 0 is always used.
   */
  public SortedList<Fraction> getUsedBeats()
  {
  	SortedList<Fraction> ret = new SortedList<Fraction>(false);
  	for (Voice voice : voices)
  	{
  		SortedList<Fraction> voiceBeats = voice.getUsedBeats();
  		ret = ret.merge(voiceBeats, false);
  	}
  	return ret;
  }
  
  
  /**
   * Gets the transposition change in this measure. If there is none,
   * null is returned.
   * TODO: currently each transposition change is interpreted as if it was
   * at the beginning of the measure - ok?
   */
  public Transpose getTranspose()
  {
  	for (MusicElement e : voices.get(0).getElements())
  	{
  		if (e instanceof Transpose)
  			return (Transpose) e;
  	}
  	return null;
  }
  
  
  /**
   * Gets the filled beats in this measure, that
   * means, the first beat in this measure where the is no music
   * element following any more.
   */
  public Fraction getFilledBeats()
  {
  	Fraction maxBeat = Fraction._0;
  	for (Voice voice : voices)
  	{
  		Fraction beat = voice.getFilledBeats();
  		if (beat.compareTo(maxBeat) > 0)
  			maxBeat = beat;
  	}
  	return maxBeat;
  }
  
  
}
