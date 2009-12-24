package com.xenoage.zong.data.music;

import com.xenoage.util.InstanceID;
import com.xenoage.util.RAList;
import com.xenoage.util.SortedList;
import com.xenoage.util.iterators.It;
import com.xenoage.util.iterators.ReverseIterator;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.key.Key;
import com.xenoage.zong.data.music.time.Time;
import com.xenoage.zong.data.music.util.DeepCopyCache;
import com.xenoage.zong.data.music.util.PositionedVoiceElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Class for a voice in a single measure
 * within a single stave.
 * 
 * A voice can contain musical elements
 * like notes, rests or directions.
 *
 * @author Andreas Wenger
 */
public class Voice
{

	//the parent measure of this voice
	private Measure measure;
	//the musical elements, sorted by time
	private ArrayList<MusicElement> elements = new ArrayList<MusicElement>();
	
	//the unique ID of this voice, used for hashing
	private final InstanceID instanceID = new InstanceID();


	/**
	 * Creates a new voice for the given {@link Measure}.
	 */
	public Voice(Measure parentMeasure)
	{
		this.measure = parentMeasure;
	}
	
	
	/**
	 * Returns a deep copy of this voice, using the given
	 * parent measure and {@link DeepCopyCache}.
	 */
	public Voice deepCopy(Measure parentMeasure, DeepCopyCache cache)
	{
		//create voice
		Voice ret = new Voice(parentMeasure);
		//copy element
		for (MusicElement e : this.elements)
		{
			if (e instanceof VoiceElement)
				ret.elements.add(((VoiceElement)e).deepCopy(ret, cache));
			else if (e instanceof NoVoiceElement)
				ret.elements.add(((NoVoiceElement)e).deepCopy(ret, cache));
		}
		return ret;
	}
	

	/**
	 * Adds the given {@link VoiceElement} at the end of this voice.
	 * This method should be avoided if possible, use
	 * the ScoreController class instead.
	 * The element is added, regardless if there is space left
	 * for it or not.
	 */
	public void addVoiceElement(VoiceElement element)
	{
		if (element.getVoice() != this)
			element.setVoice(this); //create backward link to this voice
		elements.add(element);
	}
	
	
	/**
	 * Adds the given {@link VoiceElement} at the given
	 * position within this voice.
	 * This method should be avoided if possible, use
	 * the ScoreController class instead.
	 * The element is added, regardless if there is space left
	 * for it or not.
	 */
	public void addVoiceElement(int index, VoiceElement element)
	{
		if (element.getVoice() != this)
			element.setVoice(this); //create backward link to this voice
		elements.add(index, element);
	}
	
	
	/**
   * Adds a clef, time signature or key signature to
   * this voice.
   */
  public void addNoVoiceElement(NoVoiceElement element)
  {
  	if (element.getVoice() != this)
			element.setVoice(this); //create backward link to this voice
  	elements.add(element);
  }


	/**
	 * Gets the unmodifiable list of musical elements.
	 */
	public RAList<MusicElement> getElements()
	{
		return new RAList<MusicElement>(elements);
	}
	
	
	/**
	 * Changes the given element at position i.
	 * Call this method only by the {@link ScoreController}.
	 */
	public void setElement(int index, MusicElement element)
	{
		element.setVoice(this);
		elements.set(index, element);
	}

	
	/**
	 * Adds the given element at the given position.
	 * Call this method only by the {@link ScoreController}.
	 */
	public void addElement(int index, MusicElement element)
	{
		element.setVoice(this);
		elements.add(index, element);
	}
	
	
	/**
	 * Adds the given element at the end of the list.
	 * Call this method only by the {@link ScoreController}.
	 */
	public void addElement(MusicElement element)
	{
		element.setVoice(this);
		elements.add(element);
	}
	

	/**
	 * Gets the start beat of the given element.
	 * If the element is not in this voice, null is returned.
	 */
	public Fraction getBeat(MusicElement element)
	{
		Fraction beat = Fraction._0;
		for (MusicElement e : elements)
		{
			if (e == element)
				return beat;
			else
				beat = beat.add(e.getDuration());
		}
		return null;
	}


	/**
	 * Gets the last used beat less than or equal the given one.
	 * If there are no elements, 0 is returned.
	 */
	public Fraction getLastUsedBeat(Fraction maxBeat)
	{
		Fraction beat = new Fraction(0);
		for (MusicElement e : elements)
		{
			Fraction pos = beat.add(e.getDuration());
			if (pos.compareTo(maxBeat) > 0)
				break;
			else
				beat = pos;
		}
		return beat;
	}


	/**
	 * Gets the first used beat greater than or equal the given one.
	 * If there are no elements, 0 is returned.
	 */
	public Fraction getFirstUsedBeat(Fraction minBeat)
	{
		Fraction beat = new Fraction(0);
		for (MusicElement e : elements)
		{
			if (beat.compareTo(minBeat) >= 0)
				break;
			beat = beat.add(e.getDuration());
		}
		return beat;
	}


	/**
	 * Returns true, if the given beat is the starting
	 * beat of an element within this voice, beat 0,
	 * or the empty beat behind the last element.
	 */
	public Boolean isBeatUsed(Fraction beat)
	{
		//all measures start with beat 0
		if (beat.getNumerator() == 0)
			return true;
		//is there an element at this beat?
		Fraction curBeat = new Fraction(0);
		for (MusicElement e : elements)
		{
			if (beat.equals(curBeat))
				return true;
			curBeat = curBeat.add(e.getDuration());
		}
		//first unused (empty) beat
		if (beat.equals(curBeat))
			return true;
		return false;
	}


	/**
	 * Gets the interline space of this voice in mm.
	 */
	public float getInterlineSpace()
	{
		return measure.getInterlineSpace();
	}


	/**
	 * Gets the number of lines of this measure in mm.
	 */
	public int getLinesCount()
	{
		return measure.getLinesCount();
	}


	/**
	 * Returns the parent {@link Measure} of the {@link Voice}
	 */
	public Measure getMeasure()
	{
		return measure;
	}
	
	
	/**
	 * Replaces the given element with the other given one.
	 * TODO: check if measure duration is still correct and other things.
	 */
	public void replaceElement(MusicElement oldElement, MusicElement newElement)
	{
		int index = elements.indexOf(oldElement);
		if (index > -1)
		{
			elements.set(index, newElement);
		}
		else
		{
			throw new IllegalArgumentException("Given MusicElement is not part of this Voice.");
		}
	}
	
	
	/**
	 * Adds a rest to this voice.
	 * TODO: divide into multiple rests if neccessary (whole, half, quarter, eighth, dotted, ...)
	 */
	public Rest addRest(Fraction duration)
	{
		Rest rest = new Rest(new RestData(duration));
	  addVoiceElement(rest);
	  return rest;
	}


	/**
	 * Adds a note to this voice and returns it.
	 * TIDY: remove
	 */
	public Chord addNote(Pitch pitch, Fraction duration)
	{
	  Chord chord = new Chord(new ChordData(new Note(pitch), duration));
	  addVoiceElement(chord);
	  return chord;
	}
	
	
	/**
	 * Adds a note to this voice and returns it.
	 * TIDY: remove
	 */
	public Chord addNote(Pitch pitch, Fraction duration, Articulation[] articulations)
	{
	  Chord chord = new Chord(new ChordData(new Note[]{new Note(pitch)}, duration, articulations));
	  addVoiceElement(chord);
	  return chord;
	}
  
  
  /**
   * Gets the filled beats in this voice, that
   * means, the first beat in this voice where the is no music
   * element following any more.
   */
  public Fraction getFilledBeats()
  {
  	Fraction ret = Fraction._0;
  	for (MusicElement e : elements)
  		ret = ret.add(e.getDuration());
  	return ret;
  }
  
  
  /**
   * Gets the first {@link VoiceElement} in this voice, or null
   * if there is none, together with its position (using the given base position).
   */
  public PositionedVoiceElement getFirstPositionedVoiceElement(ScorePosition basePosition)
  {
  	Fraction beat = Fraction._0;
  	for (MusicElement e : elements)
  	{
  		if (e instanceof VoiceElement)
  		{
  			return new PositionedVoiceElement((VoiceElement) e,
    			new ScorePosition(basePosition.getStaff(), basePosition.getMeasure(), beat, basePosition.getVoice()));
  		}
  		beat = beat.add(e.getDuration());
  	}
  	return null;
  }
  
  
  /**
	 * Gets the last {@link VoiceElement} in this voice, or null
	 * if there is none.
	 */
	public VoiceElement getLastVoiceElement()
	{
		for (MusicElement e : new ReverseIterator<MusicElement>(elements))
		{
			if (e instanceof VoiceElement)
				return (VoiceElement) e;
		}
		return null;
	}
	
	
	/**
   * Gets the last {@link VoiceElement} in this voice, or null
   * if there is none, together with its position (using the given base position).
   */
  public PositionedVoiceElement getLastPositionedVoiceElement(ScorePosition basePosition)
  {
  	Fraction beat = Fraction._0;
  	int foundIndex = -1;
  	Fraction foundBeat = Fraction._0;
  	for (int i = 0; i < elements.size(); i++)
  	{
  		MusicElement e = elements.get(i);
  		if (e instanceof VoiceElement)
  		{
  			foundIndex = i;
  			foundBeat = beat;
  		}
  		beat = beat.add(e.getDuration());
  	}
  	if (foundIndex > -1)
  		return new PositionedVoiceElement((VoiceElement) elements.get(foundIndex),
  			new ScorePosition(basePosition.getStaff(), basePosition.getMeasure(), foundBeat, basePosition.getVoice()));
  	else
  		return null;
  }
  
  
  /**
   * Removes the {@link MusicElement} with the given index.
   * If it is a chord, its beam is completely removed.
   */
  public void removeElementAt(int index)
  {
  	MusicElement e = elements.remove(index);
  	if (e instanceof Chord)
  	{
  		Chord chord = (Chord) e;
  		if (chord.getBeamWaypoint() != null)
  		{
  			chord.getBeamWaypoint().getBeam().remove();
  		}
  	}
  }
  
  
  /**
	 * Removes the {@link NoVoiceElement}s at the given beat, which are
	 * instance of the given class (like {@link Clef}, {@link Key}, {@link Time}...).
	 */
  @SuppressWarnings("unchecked") public void removeNoVoiceElementsAt(Fraction beat, Class type)
	{
		Fraction pos = Fraction._0;
  	for (int i = 0; i < elements.size(); i++)
  	{
  		MusicElement e = elements.get(i);
  		if (beat.equals(pos) && type.isInstance(e))
  		{
  			elements.remove(i);
  			i--;
  		}
  		pos = pos.add(e.getDuration());
  	}
	}
  
  
  /**
   * Gets the {@link InstanceID} of this object, used
   * for hashmapping.
   */
  public InstanceID getInstanceID()
  {
  	return instanceID;
  }
  
  
  /**
   * Gets a list of all beats used in this voice, that means
   * all beats where at least one element with a duration greater than 0 begins.
   * Beat 0 is always used.
   */
  public SortedList<Fraction> getUsedBeats()
  {
  	SortedList<Fraction> ret = new SortedList<Fraction>(false);
  	Fraction currentBeat = Fraction._0;
  	ret.add(currentBeat);
  	for (MusicElement element : elements)
  	{
  		if (element.getDuration() != null && element.getDuration().toFloat() > 0)
  		{
  			if (currentBeat != Fraction._0)
  				ret.add(currentBeat);
  			currentBeat = currentBeat.add(element.getDuration());
  		}
  	}
  	return ret;
  }
  
  
  /**
   * Gets a list of the elements in this measure, beginning at
   * the given score position (but not before this measure) and ending
   * at the given score position (but not after this measure). The staff and
   * the voice of the given score positions is ignored.
   * @param start  where to begin (may be also before this measure to start
   *               at the beginning of this measure, or may also be null also
   *               to start at the beginning of this measure. if it is after
   *               this measure, the iterator will be empty).
   * @param end    where to end (exclusively) (may be also after this measure to stop
   *               at the ending of this measure, or may also be null also
   *               to stop at the ending of this measure. if it is before
   *               this measure, the iterator will be emoty).              
   */
	public List<MusicElement> getElementsInRange(ScorePosition start, ScorePosition end)
	{
		LinkedList<MusicElement> ret = new LinkedList<MusicElement>();
		int thisMeasure = measure.getIndex();
		Fraction startBeat = start.getBeat();
		Fraction endBeat = end.getBeat();
		//start measure
		if (start.getMeasure() > thisMeasure)
			return ret;
		else if (start.getMeasure() < thisMeasure)
			startBeat = Fraction._0;
		//end measure
		if (end.getMeasure() < thisMeasure)
			return ret;
		else if (end.getMeasure() > thisMeasure)
			endBeat = null;
		//collect elements (TODO: be lazy instead?)
		Fraction beat = Fraction._0;
		for (MusicElement e : elements)
		{
			if (endBeat != null && beat.compareTo(endBeat) >= 0)
				break;
			else if (beat.compareTo(startBeat) >= 0)
				ret.add(e);
			beat = beat.add(e.getDuration());
		}
		return ret;
	}
	
}
