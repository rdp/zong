package com.xenoage.zong.core.music;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.lang.Tuple2.t;
import static com.xenoage.zong.core.music.MP.atVoice;
import static com.xenoage.zong.core.music.util.BeatInterval.At;
import static com.xenoage.zong.core.music.util.BeatInterval.Before;
import static com.xenoage.zong.core.music.util.BeatInterval.BeforeOrAt;

import com.xenoage.pdlib.PMap;
import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.SortedList;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.Note;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.direction.Direction;
import com.xenoage.zong.core.music.key.Key;
import com.xenoage.zong.core.music.util.BeatE;
import com.xenoage.zong.core.music.util.BeatEList;
import com.xenoage.zong.core.music.util.BeatInterval;
import com.xenoage.zong.util.exceptions.IllegalMPException;


/**
 * Measure within a single staff.
 * 
 * A measure consists of one or more voices and a
 * list of clefs, private keys (keys that only apply to
 * this staff), directions and instrument changes..
 *
 * @author Andreas Wenger
 */
public class Measure
{	

  //the voices (at least one)
  private final PVector<Voice> voices;
  
  //clefs, private keys, directions and instrument changes
  private final BeatEList<Clef> clefs;
  private final BeatEList<Key> privateKeys;
  private final BeatEList<Direction> directions;
  private final BeatEList<InstrumentChange> instrumentChanges;
  
  
  /**
   * Create a new {@link Measure}.
   * @param voices             the voices (at least one)
   * @param clefs              list of clefs, or null
   * @param privateKeys        list of private keys (that apply only to this staff), or null
   * @param directions         list of directions, or null
   * @param instrumentChanges  list of instrument changes, or null
   */
  public Measure(PVector<Voice> voices, BeatEList<Clef> clefs,
  	BeatEList<Key> privateKeys, BeatEList<Direction> directions,
  	BeatEList<InstrumentChange> instrumentChanges)
  {
  	if (voices.size() == 0)
  		throw new IllegalArgumentException("A measure must have at least one voice");
    this.voices = voices;
    this.clefs = clefs;
    this.privateKeys = privateKeys;
    this.directions = directions;
    this.instrumentChanges = instrumentChanges;
  }
  
  
  /**
   * Returns a minimal measure with a single voice.
   */
  public static Measure createMinimal()
  {
  	return new Measure(pvec(Voice.empty), null, null, null, null);
  }
  
  
  /**
   * Gets the list of voices.
   */
  public Vector<Voice> getVoices()
  {
    return voices;
  }
  
  
  /**
   * Adds a voice to the measure.
   */
  public Measure plusVoice(Voice voice)
  {
    return new Measure(voices.plus(voice), clefs, privateKeys, directions, instrumentChanges);
  }
  
  
  /**
   * Adds a clef at the given beat. If there is already one, it is replaced
   * and returned (otherwise null).
   */
  public Tuple2<Measure, Clef> withClef(Clef clef, Fraction beat)
  {
  	BeatEList<Clef> clefs = (this.clefs != null ? this.clefs : new BeatEList<Clef>());
  	Tuple2<BeatEList<Clef>, Clef> pClefs = clefs.with(clef, beat);
  	return t(new Measure(voices, pClefs.get1(), privateKeys,
  		directions, instrumentChanges), pClefs.get2());
  }
  
  
  /**
   * Adds a key at the given beat. If there is already one, it is replaced.
   */
  public Tuple2<Measure, Key> withKey(Key key, Fraction beat)
  {
  	BeatEList<Key> privateKeys = (this.privateKeys != null ?
  		this.privateKeys : new BeatEList<Key>());
  	Tuple2<BeatEList<Key>, Key> pPrivateKeys = privateKeys.with(key, beat);
  	return t(new Measure(voices, clefs, pPrivateKeys.get1(),
  		directions, instrumentChanges), pPrivateKeys.get2());
  }
  
  
  /**
   * Adds a direction at the given beat. If there is already one, it is not
   * replaced, since there may be many directions belonging to a single beat.
   */
  public Measure plusDirection(Direction direction, Fraction beat)
  {
  	BeatEList<Direction> directions = (this.directions != null ?
  		this.directions : new BeatEList<Direction>());
  	directions = directions.plus(direction, beat);
  	return new Measure(voices, clefs, privateKeys, directions, instrumentChanges);
  }
  
  
  /**
   * Adds an instrument change at the given beat.
   * If there is already one, it is replaced.
   */
  public Tuple2<Measure, InstrumentChange> withInstrumentChange(
  	InstrumentChange instrumentChange, Fraction beat)
  {
  	BeatEList<InstrumentChange> instrumentChanges = (this.instrumentChanges != null ?
  		this.instrumentChanges : new BeatEList<InstrumentChange>());
  	Tuple2<BeatEList<InstrumentChange>, InstrumentChange> pInstrumentChanges =
  		instrumentChanges.with(instrumentChange, beat);
  	return t(new Measure(voices, clefs, privateKeys,
  		directions, pInstrumentChanges.get1()), pInstrumentChanges.get2());
  }
  
  
  /**
   * Adds the given MeasureElement at the given beat. Dependent on its type,
   * it may replace elements of the same type, which is then returned (otherwise null).
   * See the documentation for the methods working with specific MeasureElements.
   */
  public Tuple2<Measure, ? extends MeasureElement> plusMeasureElement(
  	MeasureElement element, Fraction beat)
  {
  	if (element instanceof Clef)
  	{
  		return withClef((Clef) element, beat);
  	}
  	else if (element instanceof Key)
  	{
  		return withKey((Key) element, beat);
  	}
  	else if (element instanceof Direction)
  	{
  		return t(plusDirection((Direction) element, beat), null);
  	}
  	else if (element instanceof InstrumentChange)
  	{
  		return withInstrumentChange((InstrumentChange) element, beat);
  	}
  	else
  	{
  		throw new IllegalArgumentException("Unknown MeasureElement subclass: " +
  			element.getClass().getName());
  	}
  }
  
  
  /**
   * Collect the accidentals within this measure (backwards),
   * beginning at the given start beat where the given key is valid, ending before or at
   * the given beat (depending on the given interval), looking at all voices.
   * @param beat      the maximum beat (inclusive if exclusive, depending on the interval)
   * @param interval  where to stop looking ({@link BeatInterval#Before} or
   *                    {@link BeatInterval#BeforeOrAt}). {@link BeatInterval#At} is
   *                    handled like {@link BeatInterval#BeforeOrAt}.
   * @param startKey  the key that is valid at the beginning of this measure
   * @return a map with the pitches that have accidentals (without alter)
   *   as keys and their corresponding alter values as values.
   */
  public PMap<Pitch, Byte> getAccidentals(Fraction beat, BeatInterval interval,
  	Fraction startBeat, Key startBeatKey)
  {
  	if (!(interval == Before || interval == BeforeOrAt || interval == At))
		{
			throw new IllegalArgumentException("Illegal interval for this method: " + interval);
		}
  	if (interval == At)
  	{
  		interval = BeforeOrAt;
  	}
  	//when there is a key change, all accidentals are reset. so look for the
  	//last key change before the given beat (if any)
    PMap<Pitch, Byte> ret = new PMap<Pitch, Byte>();
    PMap<Pitch, Fraction> retBeats = new PMap<Pitch, Fraction>();
    for (Voice voice : voices)
    {
	    Fraction pos = startBeat;
	    for (VoiceElement e : voice.getElements())
	    {
	    	if (pos.compareTo(startBeat) < 0)
	    	{
	    		pos = pos.add(e.getDuration());
	    		continue;
	    	}
	    	if (!interval.isInInterval(pos, beat))
	    	{
	        break;
	    	}
	      if (e instanceof Chord)
	      {
	        Chord chord = (Chord) e;
	        for (Note note : chord.getNotes())
	        {
	          Pitch pitch = note.getPitch();
	          Pitch pitchUnaltered = pitch.withoutAlter();
	          //accidental already set?
	          Byte oldAccAlter = ret.get(pitchUnaltered);
	          if (oldAccAlter != null)
	          {
	          	//there is already an accidental. only replace it if alter changed
	          	//and if it is at a later position than the already found one
	          	Fraction existingBeat = retBeats.get(pitch);
	          	if (pitch.getAlter() != oldAccAlter && pos.compareTo(existingBeat) > 0)
	          	{
	          		ret = ret.plus(pitchUnaltered, pitch.getAlter());
	          		retBeats = retBeats.plus(pitchUnaltered, pos);
	          	}
	          }
	          else
	          {
	            //accidental not neccessary because of key?
	            if (startBeatKey.getAlterations()[pitch.getStep()] == pitch.getAlter())
	            {
	              //ok, we need no accidental here.
	            }
	            else
	            {
	              //add accidental
	            	ret = ret.plus(pitchUnaltered, pitch.getAlter());
	            	retBeats = retBeats.plus(pitchUnaltered, pos);
	            }
	          }
	        }
	      }
	      pos = pos.add(e.getDuration());
	    }
    }
    return ret;
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
  
  
  /**
   * Returns the last clef within this measure.
   * If there is none, null is returned.
   */
  public BeatE<Clef> getLastClef()
  {
    if (clefs != null)
    	return clefs.getLast();
    else
    	return null;
  }
	
	
	/**
   * Returns the last clef at or before the given beat.
   * If there is none, null is returned.
   */
  public BeatE<Clef> getLastClef(BeatInterval endpoint, Fraction beat)
  {
  	if (clefs != null)
    	return clefs.getLastBefore(endpoint, beat);
    else
    	return null;
  }
  
  
  /**
   * Returns the last key within this measure.
   * If there is none, null is returned.
   */
  public BeatE<Key> getLastKey()
  {
    if (privateKeys != null)
    	return privateKeys.getLast();
    else
    	return null;
  }
	
	
	/**
   * Returns the last key at or before the given beat.
   * If there is none, null is returned.
   */
  public BeatE<Key> getLastKey(BeatInterval endpoint, Fraction beat)
  {
  	if (privateKeys != null)
    	return privateKeys.getLastBefore(endpoint, beat);
    else
    	return null;
  }
  
  
  /**
   * Gets the voice with the given index, or throws an
   * {@link IllegalMPException} if there is none.
   * Only the voice index of the given position is relevant.
   */
  public Voice getVoice(int index)
  {
  	if (index >= 0 && index <= voices.size())
  		return voices.get(index);
  	else
  		throw new IllegalMPException(atVoice(index));
  }
  
  
  /**
   * Gets the voice with the given index, or throws an
   * {@link IllegalMPException} if there is none.
   * Only the voice index of the given position is relevant.
   */
  public Voice getVoice(MP mp)
  {
  	int index = mp.getVoice();
  	if (index >= 0 && index <= voices.size())
  		return voices.get(index);
  	else
  		throw new IllegalMPException(mp);
  }
  
  
  /**
   * Sets the voice with the given index.
   */
  public Measure withVoice(int index, Voice voice)
  {
  	PVector<Voice> voices = this.voices.with(index, voice);
  	return new Measure(voices, clefs, privateKeys, directions, instrumentChanges);
  }
  
  
}
