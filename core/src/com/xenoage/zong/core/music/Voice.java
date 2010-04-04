package com.xenoage.zong.core.music;

import static com.xenoage.util.Range.range;
import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.MP.atBeat;
import static com.xenoage.zong.core.music.util.BeatE.beate;
import static com.xenoage.zong.core.music.util.VoiceElementSelection.First;
import static com.xenoage.zong.core.music.util.VoiceElementSelection.Last;
import static com.xenoage.zong.core.music.util.VoiceElementSide.Start;
import static com.xenoage.zong.core.music.util.VoiceElementSide.Stop;

import java.util.LinkedList;

import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.SortedList;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.util.BeatE;
import com.xenoage.zong.core.music.util.BeatInterval;
import com.xenoage.zong.core.music.util.VoiceElementSelection;
import com.xenoage.zong.core.music.util.VoiceElementSide;


/**
 * Voice in a single measure within a single staff.
 * 
 * A voice contains musical elements like notes, rests or directions.
 *
 * @author Andreas Wenger
 */
public class Voice
{

	//the musical elements, sorted by time
	private final PVector<VoiceElement> elements;
	
	public static final Voice empty = new Voice(new PVector<VoiceElement>());


	/**
	 * Creates a new {@link Voice}.
	 * @param elements  the elements, sorted by time
	 */
	public Voice(PVector<VoiceElement> elements)
	{
		this.elements = elements;
	}
	
	
	/**
	 * Gets the {@link PositionedVoiceElement} by the given {@link VoiceElementSelection},
	 * which given {@link VoiceElementSide} is within the given {@link BeatInterval} relative
	 * to the given beat, or null if there is none.
	 */
	public BeatE<VoiceElement> getElement(VoiceElementSelection selection,
		VoiceElementSide side, BeatInterval interval, Fraction beat)
	{
		Fraction curBeat = Fraction._0;
		if (side == Start)
		{
			if (selection == First)
			{
				for (VoiceElement e : elements)
				{
					if (interval.isInInterval(atBeat(curBeat), atBeat(beat)))
						return beate(e, curBeat);
					curBeat = curBeat.add(e.getDuration());
				}
				return null;
			}
			else if (selection == Last)
			{
				BeatE<VoiceElement> ret = null;
				for (VoiceElement e : elements)
				{
					if (interval.isInInterval(curBeat, beat))
						ret = beate(e, curBeat);
					curBeat = curBeat.add(e.getDuration());
				}
				return ret;
			}
		}
		else if (side == Stop)
		{
			if (selection == First)
			{
				for (VoiceElement e : elements)
				{
					curBeat = curBeat.add(e.getDuration());
					if (interval.isInInterval(curBeat, beat))
						return beate(e, curBeat);
				}
				return null;
			}
			else if (selection == Last)
			{
				BeatE<VoiceElement> ret = null;
				for (VoiceElement e : elements)
				{
					curBeat = curBeat.add(e.getDuration());
					if (interval.isInInterval(curBeat, beat))
						ret = beate(e, curBeat);
				}
				return ret;
			}
		}
		throw new IllegalArgumentException("Unknown parameters");
	}


	/**
	 * Gets the list of elements.
	 */
	public Vector<VoiceElement> getElements()
	{
		return elements;
	}
	
	
	/**
	 * Adds the given element at the end of this voice.
	 */
	public Voice plusElement(VoiceElement element)
	{
		return new Voice(elements.plus(element));
	}
	
	
	/**
	 * Adds the given element at the given
	 * position within this voice.
	 */
	public Voice plusElement(int index, VoiceElement element)
	{
		return new Voice(elements.plus(index, element));
	}
	
	
	/**
	 * Replaces the element with the given index by the given one.
	 */
	public Voice replaceElement(int index, VoiceElement element)
	{
		return new Voice(elements.with(index, element));
	}
	
	
	/**
	 * Replaces the given element by the given ones.
	 */
	public Voice replaceElement(VoiceElement oldElement, VoiceElement... newElements)
	{
		int index = elements.indexOf(oldElement);
		if (index > -1)
		{
			PVector<VoiceElement> elements = this.elements.minus(index);
			for (VoiceElement newElement : newElements)
			{
				elements = elements.plus(index, newElement);
				index++;
			}
			return new Voice(elements);
		}
		else
		{
			throw new IllegalArgumentException("Given element is not part of this voice.");
		}
	}
	

  /**
   * Removes the element with the given index.
   */
  public Voice minusElement(int index)
  {
  	return new Voice(elements.minus(index));
  }
  
  
  /**
   * Removes the given element.
   */
  public Voice minusElement(VoiceElement element)
  {
  	return new Voice(elements.minus(element));
  }
	

	/**
	 * Gets the start beat of the given element.
	 * If the element is not in this voice, null is returned.
	 */
	public Fraction getBeat(VoiceElement element)
	{
		Fraction beat = Fraction._0;
		for (VoiceElement e : elements)
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
		Fraction beat = fr(0);
		for (VoiceElement e : elements)
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
		Fraction beat = fr(0);
		for (VoiceElement e : elements)
		{
			if (beat.compareTo(minBeat) >= 0)
				break;
			beat = beat.add(e.getDuration());
		}
		return beat;
	}
	
	
	/**
	 * Inserts the given element at the given beat
	 * (or, if no element ends there, after the element which is
	 * at the given beat).
	 */
	public Voice insertElement(Fraction beat, VoiceElement element)
	{
		Fraction pos = Fraction._0;
		for (int i : range(elements))
		{
			if (beat.compareTo(pos) <= 0)
				return new Voice(elements.plus(i, element));
			pos = pos.add(elements.get(i).getDuration());
		}
		return new Voice(elements.plus(element));
	}


	/**
	 * Returns true, if the given beat is the starting
	 * beat of an element within this voice, beat 0,
	 * or the empty beat behind the last element.
	 */
	public boolean isBeatUsed(Fraction beat)
	{
		//all measures start with beat 0
		if (beat.getNumerator() == 0)
			return true;
		//is there an element at this beat?
		Fraction curBeat = fr(0);
		for (VoiceElement e : elements)
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
	 * Returns true, if this voice contains no elements.
	 */
	public boolean isEmpty()
	{
		return elements.size() == 0;
	}
  
  
  /**
   * Gets the filled beats in this voice, that means, the first beat in this
   * voice where the is no music element following any more.
   */
  public Fraction getFilledBeats()
  {
  	Fraction ret = Fraction._0;
  	for (VoiceElement e : elements)
  		ret = ret.add(e.getDuration());
  	return ret;
  }
  
  
  /**
   * Gets the element at the given beat. If no element starts at exactly
   * the given beat, null is returned.
   */
  public VoiceElement getElementAt(Fraction beat)
  {
  	Fraction currentBeat = Fraction._0;
  	for (VoiceElement e : elements)
  	{
  		int compare = beat.compareTo(currentBeat);
  		if (compare == 0)
  			return e;
  		else if (compare > 0)
  			currentBeat = currentBeat.add(e.getDuration());
  		else
  			break;
  	}
  	return null;
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
  	for (VoiceElement e : elements)
  	{
  		Fraction duration = e.getDuration();
  		if (duration != null && duration.getNumerator() > 0)
  		{
  			if (!currentBeat.equals(ret.getLast()))
  				ret.add(currentBeat);
  			currentBeat = currentBeat.add(duration);
  		}
  	}
  	return ret;
  }
  
  
  /**
   * Gets a list of the elements in this voice, beginning at or after
   * the given start beat and before the given end beat.
   * @param start  where to begin (may also be null to start at the beginning of this measure).
   * @param end    where to end (exclusively) (may be also be null to stop
   *               at the ending of this measure).              
   */
	public LinkedList<VoiceElement> getElementsInRange(Fraction startBeat, Fraction endBeat)
	{
		LinkedList<VoiceElement> ret = new LinkedList<VoiceElement>();
		//collect elements
		Fraction beat = Fraction._0;
		for (VoiceElement e : elements)
		{
			if (endBeat != null && beat.compareTo(endBeat) >= 0)
				break;
			else if (startBeat == null || beat.compareTo(startBeat) >= 0)
				ret.add(e);
			beat = beat.add(e.getDuration());
		}
		return ret;
	}
	
	
}
