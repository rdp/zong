package com.xenoage.zong.data.controller;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import com.xenoage.util.RAList;
import com.xenoage.util.SortedList;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.MusicContext;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.data.music.NoVoiceElement;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.Rest;
import com.xenoage.zong.data.music.RestData;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.VoiceElement;
import com.xenoage.zong.data.music.barline.Barline;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.key.Key;
import com.xenoage.zong.data.music.key.TraditionalKey;
import com.xenoage.zong.data.music.time.SenzaMisura;
import com.xenoage.zong.data.music.time.Time;
import com.xenoage.zong.data.music.util.Endpoint;
import com.xenoage.zong.data.music.util.PositionedVoiceElement;
import com.xenoage.zong.util.exceptions.InvalidScorePositionException;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * This class provides basic read and write operations for
 * a score.
 * 
 * These methods are safe, i.e. the data within the score
 * should remain consistent, whatever is done. This is not the case,
 * if the music classes are modified directly. Thus, it is
 * strongly recommended to use this class whenever possible.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class ScoreController
{

	private Score score;

	@SuppressWarnings("unchecked") private final LinkedList<Class> noVoiceElementsOrder;


	/**
	 * Creates a new {@link ScoreController} for the given {@link Score}.
	 */
	@SuppressWarnings("unchecked") public ScoreController(Score score)
	{
		this.score = score;
		//order of NoVoiceElements: always barline, clef, key, time.
		this.noVoiceElementsOrder = new LinkedList<Class>();
		this.noVoiceElementsOrder.add(Barline.class);
		this.noVoiceElementsOrder.add(Clef.class);
		this.noVoiceElementsOrder.add(Key.class);
		this.noVoiceElementsOrder.add(Time.class);
	}


	/**
	 * Writes the given {@link VoiceElement} at the given {@link ScorePosition}.
	 * If it overlaps with other elements, these are deleted and gaps are
	 * filled with rests.
	 * If the staff does not exist or the given beat does not fit to the
	 * time signature, an {@link InvalidScorePositionException} is
	 * thrown. If the measure or voice does not exist, it is created.
	 * If there is not enough space for the given element within the measure,
	 * a {@link MeasureFullException} is thrown.
	 */
	public void writeAt(VoiceElement voiceElement, ScorePosition position)
		throws InvalidScorePositionException, MeasureFullException
	{
		//the beats where we write the element and where it stops
		Fraction writeBeat = position.getBeat();
		Fraction writeStopBeat = writeBeat.add(voiceElement.getDuration());

		//is there enough space for the new element?
		Fraction measureBeats = getTimeAtOrBefore(position.getStaff(), position.getMeasure())
			.getBeatsPerMeasure();
		if (measureBeats != null && writeStopBeat.compareTo(measureBeats) > 0)
		{
			throw new MeasureFullException(position, voiceElement.getDuration());
		}

		//find the index and beat of the last element starting before or at the
		//current beat, that has a duration (i.e., do not overwrite non-duration
		//elements like key signature or time signature at that position)
		int startElementIndex = 0;
		Fraction startElementBeat = new Fraction(0);
		Voice voice = score.getVoice(position.getStaff(), position.getMeasure(), position
			.getVoice());
		for (MusicElement e : voice.getElements())
		{
			Fraction pos = startElementBeat.add(e.getDuration());
			if (pos.compareTo(writeBeat) <= 0)
			{
				startElementBeat = pos;
				startElementIndex++;
			}
			else
			{
				break;
			}
		}

		//find the last element affected by the duration of the new element
		int endElementIndex = startElementIndex;
		Fraction endElementBeat = startElementBeat;
		for (int i = endElementIndex; i < voice.getElements().size(); i++)
		{
			MusicElement e = voice.getElements().get(i);
			if (endElementBeat.compareTo(writeStopBeat) < 0)
			{
				endElementBeat = endElementBeat.add(e.getDuration());
				endElementIndex++;
			}
			else
			{
				break;
			}
		}
		endElementIndex--;

		//delete the elements between the startElementIndex and the
		//endElement index
		for (int i = endElementIndex; i >= startElementIndex; i--)
		{
			voice.removeElementAt(i);
		}

		//create a rest between the startElementBeat and the writeBeat,
		//if there is a gap now
		int insertIndex = startElementIndex;
		if (writeBeat.compareTo(startElementBeat) > 0)
		{
			Fraction gap = writeBeat.sub(startElementBeat);
			Rest rest = new Rest(new RestData(gap));
			voice.addVoiceElement(insertIndex, rest);
			insertIndex++;
		}

		//add the new element
		voiceElement.setVoice(voice);
		voice.addElement(insertIndex, voiceElement);
		insertIndex++;

		//create a rest between the writeStopBeat and the endElementBeat,
		//if there is a gap now
		if (endElementBeat.compareTo(writeStopBeat) > 0)
		{
			Fraction gap = endElementBeat.sub(writeStopBeat);
			Rest rest = new Rest(new RestData(gap));
			voice.addVoiceElement(insertIndex, rest);
			insertIndex++;
		}
	}


	/**
	 * Writes the given {@link NoVoiceElement} at the given {@link ScorePosition}.
	 * If there is a {@link NoVoiceElement} of the same type (clef, key, time, barline)
	 * at the given beat, it is overwritten, otherwise it is added in the correct order.
	 * If the staff does not exist or the given beat does not fit to the
	 * time signature, an {@link InvalidScorePositionException} is
	 * thrown. If the measure or voice does not exist, it is created.
	 * If gaps appear before or after the written element because of a romoved
	 * {@link VoiceElement}, they are filled with rests.
	 * 
	 * TODO: ensure right order of clef, key signature and so on
	 */
	public void writeAt(NoVoiceElement noVoiceElement, ScorePosition position)
		throws InvalidScorePositionException
	{
		//is there enough space for the new element?
		Fraction writeBeat = position.getBeat();
		Fraction measureBeats = getTimeAtOrBefore(position.getStaff(), position.getMeasure())
			.getBeatsPerMeasure();
		if (measureBeats != null && writeBeat.compareTo(measureBeats) > 0)
		{
			throw new InvalidScorePositionException(position);
		}

		//simplest case: voice is not used up to the given beat
		Voice voice = score.getVoice(position.getStaff(), position.getMeasure(), position
			.getVoice());
		Fraction gap = writeBeat.sub(voice.getFilledBeats());
		if (gap.compareTo(Fraction._0) > 0)
		{
			voice.addRest(gap);
			voice.addNoVoiceElement(noVoiceElement);
			return;
		}

		//look if we can place the element at the
		//given beat without replacing a VoiceElement (i.e. there is
		//no intersection with a duration-element)
		int elementToRemoveIndex = -1;
		Fraction elementToRemoveStartBeat = writeBeat;
		Fraction elementToRemoveStopBeat = writeBeat;
		Fraction currentBeat = Fraction._0;
		RAList<MusicElement> elements = voice.getElements();
		boolean isBeatFree = writeBeat.equals(Fraction._0);
		for (int i = 0; i < elements.size(); i++)
		{
			MusicElement e = elements.get(i);
			currentBeat = currentBeat.add(e.getDuration());
			if (currentBeat.equals(writeBeat))
			{
				isBeatFree = true;
				if (e instanceof NoVoiceElement
					&& hasSameNoVoiceElementType((NoVoiceElement) e, noVoiceElement))
				{
					//we have found a NoVoiceElement of the same type at the given beat.
					//replace it and we are finished.
					voice.setElement(i, noVoiceElement);
					//TODO: sort NoVoiceElements
					return;
				}
			}
			else if (currentBeat.compareTo(writeBeat) > 0)
			{
				//we have gone too far. insert it, if possible, between this element and the last one.
				if (isBeatFree)
				{
					voice.addElement(i, noVoiceElement);
					//TODO: sort NoVoiceElements
					return;
				}
				else
				{
					//beat is not free. we have to remove this element.
					elementToRemoveIndex = i;
					elementToRemoveStopBeat = currentBeat;
					elementToRemoveStartBeat = elementToRemoveStopBeat.sub(e.getDuration());
					break;
				}
			}
		}

		//the given beat is not "free" (we have to remove the VoiceElement
		//intersecting it (elementToRemoveIndex), and, if necessary, fill with rests
		//before and after the element) or at the end of the elements list.
		Fraction gapBefore = writeBeat.sub(elementToRemoveStartBeat);
		Fraction gapAfter = elementToRemoveStopBeat.sub(writeBeat);
		if (elementToRemoveIndex > -1)
		{
			voice.removeElementAt(elementToRemoveIndex);
			voice.addElement(elementToRemoveIndex, noVoiceElement);
		}
		else
		{
			voice.addElement(noVoiceElement);
		}
		if (gapBefore.compareTo(new Fraction(0)) > 0)
		{
			Rest rest = new Rest(new RestData(gapBefore));
			rest.setVoice(voice);
			voice.addElement(elementToRemoveIndex, rest);
			elementToRemoveIndex++;
		}
		if (gapAfter.compareTo(new Fraction(0)) > 0)
		{
			Rest rest = new Rest(new RestData(gapAfter));
			rest.setVoice(voice);
			voice.addElement(elementToRemoveIndex + 1, rest);
		}
	}


	/**
	 * Gets the {@link VoiceElement} before the given position (also over measure
	 * boundaries) together with the {@link ScorePosition} where it starts,
	 * or null, if there is none (begin of score or everything is empty)
	 * If a {@link VoiceElement} starts at the given beat, the preceding {@link VoiceElement}
	 * is returned. If the given beat is within a {@link VoiceElement}, not that
	 * element but the {@link VoiceElement} before that element is returned.
	 * If the given {@link ScorePosition} is invalid, an {@link InvalidScorePositionException}
	 * is thrown.
	 */
	public PositionedVoiceElement getVoiceElementBefore(ScorePosition position)
		throws InvalidScorePositionException
	{
		//find the last voice element starting before the current beat
		//in the given measure
		Voice voice = score.getVoice(position.getStaff(), position.getMeasure(), position
			.getVoice());
		Fraction pos = Fraction._0;
		int foundIndex = -1;
		Fraction foundPos = Fraction._0;
		for (int i = 0; i < voice.getElements().size(); i++)
		{
			if (pos.compareTo(position.getBeat()) < 0)
			{
				MusicElement e = voice.getElements().get(i);
				Fraction posEnd = pos.add(e.getDuration());
				if (posEnd.compareTo(position.getBeat()) > 0)
				{
					break;
				}
				else if (e instanceof VoiceElement)
				{
					foundIndex = i;
					foundPos = pos;
				}
				pos = pos.add(e.getDuration());
			}
			else
			{
				break;
			}
		}
		if (foundIndex > -1)
		{
			return new PositionedVoiceElement((VoiceElement) voice.getElements()
				.get(foundIndex), new ScorePosition(position.getStaff(), position.getMeasure(),
				foundPos, position.getVoice()));
		}
		//no result in this measure. loop through the preceding measures.
		for (int iMeasure = position.getMeasure() - 1; iMeasure >= 0; iMeasure--)
		{
			PositionedVoiceElement pve = score.getVoice(position.getStaff(), iMeasure,
				position.getVoice())
				.getLastPositionedVoiceElement(
					new ScorePosition(position.getStaff(), iMeasure, Fraction._0, position
						.getVoice()));
			if (pve != null)
				return pve;
		}
		//no voice element before that beat.
		return null;
	}


	/**
	 * Gets the {@link VoiceElement} at the given position
	 * together with the {@link ScorePosition} where it starts,
	 * or null, if there is none.
	 * The element beginning at or around the given beat is returned.
	 * If the given {@link ScorePosition} is invalid, an {@link InvalidScorePositionException}
	 * is thrown.
	 */
	public PositionedVoiceElement getVoiceElementAt(ScorePosition position)
		throws InvalidScorePositionException
	{
		Voice voice = score.getVoice(position.getStaff(), position.getMeasure(), position
			.getVoice());
		//find the last voice element starting before or at the current beat
		//in the given measure
		Fraction pos = Fraction._0;
		int foundIndex = -1;
		Fraction foundPos = Fraction._0;
		int i;
		for (i = 0; i < voice.getElements().size(); i++)
		{
			if (pos.compareTo(position.getBeat()) <= 0)
			{
				MusicElement e = voice.getElements().get(i);
				if (e instanceof VoiceElement)
				{
					foundIndex = i;
					foundPos = pos;
				}
				pos = pos.add(e.getDuration());
			}
			else
			{
				break;
			}
		}
		//special case (not covered with the above loop):
		//if it is the last beat of the voice, no element was hit
		if (i == voice.getElements().size() && pos.equals(position.getBeat()))
		{
			foundIndex = -1;
		}
		if (foundIndex > -1)
		{
			return new PositionedVoiceElement((VoiceElement) voice.getElements()
				.get(foundIndex), new ScorePosition(position.getStaff(), position.getMeasure(),
				foundPos, position.getVoice()));
		}
		else
		{
			return null;
		}
	}


	/**
	 * Gets the {@link VoiceElement} after the given position (also over measure
	 * boundaries) together with the {@link ScorePosition} where it starts,
	 * or null, if there is none (end of score or everything is empty)
	 * If a {@link VoiceElement} starts at the given beat, the following {@link VoiceElement}
	 * is returned. If the given beat is within a {@link VoiceElement}, not that
	 * element but the {@link VoiceElement} after that element is returned.
	 * If the given {@link ScorePosition} is invalid, an {@link InvalidScorePositionException}
	 * is thrown.
	 */
	public PositionedVoiceElement getVoiceElementAfter(ScorePosition position)
		throws InvalidScorePositionException
	{
		//find the first voice element starting after the current beat
		//in the given measure
		Voice voice = score.getVoice(position.getStaff(), position.getMeasure(), position
			.getVoice());
		Fraction pos = Fraction._0;
		for (int i = 0; i < voice.getElements().size(); i++)
		{
			MusicElement e = voice.getElements().get(i);
			if (pos.compareTo(position.getBeat()) <= 0)
			{
				pos = pos.add(e.getDuration());
			}
			else if (e instanceof VoiceElement)
			{
				return new PositionedVoiceElement((VoiceElement) voice.getElements().get(i),
					new ScorePosition(position.getStaff(), position.getMeasure(), pos, position
						.getVoice()));
			}
		}
		//no result in this measure. loop through the following measures.
		for (int iMeasure = position.getMeasure() + 1; iMeasure >= 0; iMeasure--)
		{
			PositionedVoiceElement pve = score.getVoice(position.getStaff(), iMeasure,
				position.getVoice())
				.getFirstPositionedVoiceElement(
					new ScorePosition(position.getStaff(), iMeasure, Fraction._0, position
						.getVoice()));
			if (pve != null)
				return pve;
		}
		//no voice element after that beat.
		return null;
	}


	/**
	 * Gets the {@link NoVoiceElement}s at the given position, or an empty
	 * array if there are none.
	 * If the given {@link ScorePosition} is invalid, an {@link InvalidScorePositionException}
	 * is thrown.
	 */
	public LinkedList<NoVoiceElement> getNoVoiceElementsAt(ScorePosition position)
		throws InvalidScorePositionException
	{
		Voice voice = score.getVoice(position.getStaff(), position.getMeasure(), 0);
		LinkedList<NoVoiceElement> ret = new LinkedList<NoVoiceElement>();
		Fraction beat = Fraction._0;
		for (MusicElement e : voice.getElements())
		{
			int compare = beat.compareTo(position.getBeat());
			if (compare == 0 && e instanceof NoVoiceElement)
			{
				ret.add((NoVoiceElement) e);
			}
			else if (compare > 0)
			{
				break;
			}
		}
		return ret;
	}


	/**
	 * Removes the {@link NoVoiceElement}s at the given position, which are
	 * instance of the given class (like {@link Clef}, {@link Key}, {@link Time}...).
	 * If the given {@link ScorePosition} is invalid, an {@link InvalidScorePositionException}
	 * is thrown.
	 */
	@SuppressWarnings("unchecked") public void removeNoVoiceElementsAt(
		ScorePosition position, Class type) throws InvalidScorePositionException
	{
		Voice voice = score.getVoice(position.getStaff(), position.getMeasure(), 0);
		voice.removeNoVoiceElementsAt(position.getBeat(), type);
	}


	/**
	 * Gets the {@link MusicContext} at the given position.
	 * If the given {@link ScorePosition} is invalid, an {@link InvalidScorePositionException}
	 * is thrown.
	 * 
	 * If an accidental appears at the given beat, it is not
	 * used for the context, because the meaning of this method
	 * is to list all accidentals that are defined before the given beat.
	 * 
	 * Calling this method can be quite expensive, so call only when neccessary.
	 */
	public MusicContext getMusicContextAt(ScorePosition position)
		throws InvalidScorePositionException
	{
		Clef clef = getClef(position, Endpoint.AtOrBefore);
		Key key = getKeyAtOrBefore(position.getStaff(), position.getMeasure());
		Measure measure = score.getMeasure(position.getStaff(), position.getMeasure());
		Hashtable<Pitch, Byte> accidentals = measure.getAccidentalsBeforeBeat(position
			.getBeat(), key);
		return new MusicContext(clef, key, accidentals);
	}


	/**
	 * Gets the {@link MusicContext} directly before the given position.
	 * If the given {@link ScorePosition} is invalid, an {@link InvalidScorePositionException}
	 * is thrown.
	 * 
	 * Calling this method can be quite expensive, so call only when neccessary.
	 */
	public MusicContext getMusicContextBefore(ScorePosition position)
		throws InvalidScorePositionException
	{
		Clef clef = getClef(position, Endpoint.Before);
		//keys are only at the beginning of a measure. thus, if the given beat is 0,
		//we must look only at the preceding measures
		Key key = null;
		if (position.getBeat().equals(Fraction._0))
			key = getKeyAtOrBefore(position.getStaff(), position.getMeasure() - 1);
		else
			key = getKeyAtOrBefore(position.getStaff(), position.getMeasure());
		Measure measure = score.getMeasure(position.getStaff(), position.getMeasure());
		Hashtable<Pitch, Byte> accidentals = measure.getAccidentalsBeforeBeat(position
			.getBeat(), key);
		return new MusicContext(clef, key, accidentals);
	}


	/**
	 * Gets the last {@link Clef} that is defined before (or at,
	 * dependent on the given {@link Endpoint}) the given
	 * {@link ScorePosition}, also over measure boundaries. If there is
	 * none, a default g clef is returned.
	 */
	public Clef getClef(ScorePosition position, Endpoint endpoint)
		throws InvalidScorePositionException
	{
		//begin with the given measure. if there is one, return it.
		Measure measure = score.getMeasure(position.getStaff(), position.getMeasure());
		Clef ret = measure.getClef(endpoint, position.getBeat());
		if (ret != null)
			return ret;
		//search in the preceding measures
		for (int iMeasure = position.getMeasure() - 1; iMeasure >= 0; iMeasure--)
		{
			measure = score.getMeasure(position.getStaff(), iMeasure);
			ret = measure.getLastClef();
			if (ret != null)
				return ret;
		}
		//no clef found. return default clef.
		return new Clef(ClefType.G);
	}


	/**
	 * Gets the last {@link Key} that is defined at the measure
	 * with the given index or before within the given staff. If there is
	 * none, a default C major time signature is returned.
	 */
	public Key getKeyAtOrBefore(int staffIndex, int measureIndex)
		throws InvalidScorePositionException
	{
		//search for last key
		for (int iMeasure = measureIndex; iMeasure >= 0; iMeasure--)
		{
			Measure measure = score.getMeasure(staffIndex, iMeasure);
			Key ret = measure.getKey();
			if (ret != null)
				return ret;
		}
		//no key found. return default key.
		return new TraditionalKey(0);
	}


	/**
	 * Gets the last {@link Time} that is defined at the measure
	 * with the given index or before within the given staff. If there is
	 * none, a default {@link SenzaMisura} time signature is returned.
	 */
	public Time getTimeAtOrBefore(int staffIndex, int measureIndex)
		throws InvalidScorePositionException
	{
		//search for last time
		for (int iMeasure = measureIndex; iMeasure >= 0; iMeasure--)
		{
			Measure measure = score.getMeasure(staffIndex, iMeasure);
			Time time = measure.getTime();
			if (time != null)
				return time;
		}
		//no key found. return default time.
		return new SenzaMisura();
	}


	/**
	 * Returns true, if the given {@link NoVoiceElement}s have the same
	 * type: clef, time, key, barline.
	 */
	@SuppressWarnings("unchecked") public boolean hasSameNoVoiceElementType(
		NoVoiceElement e1, NoVoiceElement e2)
	{
		Class[] interfaces = new Class[] { Clef.class, Time.class, Key.class, Barline.class };
		for (Class i : interfaces)
		{
			if (i.isInstance(e1))
				return i.isInstance(e2);
		}
		return false;
	}


	/**
	 * Gets the number of beats in the given measure column.
	 * If a time signature is defined (in the first staff), its beats are returned.
	 * If the time signature is unknown (senza-misura), the beats of the
	 * voice with the most beats are returned.
	 */
	public Fraction getMeasureBeats(int measureIndex)
	{
		Fraction ret = getTimeAtOrBefore(0, measureIndex).getBeatsPerMeasure();
		if (ret == null)
		{
			for (Measure measure : score.getMeasureColumn(measureIndex))
			{
				for (Voice voice : measure.getVoices())
				{
					Fraction usedBeats = voice.getFilledBeats();
					if (ret == null || usedBeats.compareTo(ret) > 0)
					{
						ret = usedBeats;
					}
				}
			}
		}
		return ret;
	}


	/**
	 * Gets the used beats within the whole score.
	 * For each measure, a sorted list of beats is returned.
	 */
	public ArrayList<SortedList<Fraction>> getUsedBeats()
	{
		ArrayList<SortedList<Fraction>> ret = new ArrayList<SortedList<Fraction>>(score
			.getMeasuresCount());
		for (int iColumn = 0; iColumn < score.getMeasuresCount(); iColumn++)
		{
			SortedList<Fraction> columnBeats = new SortedList<Fraction>(false);
			for (int iStaff = 0; iStaff < score.getStavesCount(); iStaff++)
			{
				columnBeats = columnBeats.merge(score.getMeasure(iStaff, iColumn).getUsedBeats(),
					false);
			}
			ret.add(columnBeats);
		}
		return ret;
	}


	/**
	 * Clips a {@link ScorePosition} to a measure. If the {@link ScorePosition} is befor or at the
	 *  beginning of the measure with measureIndex, the first beat is returned. If it is after the
	 *  measure, the last beat is returned. If the ScorePosition is in the measure, this 
	 *  position is returned.
	 * @param measureIndex			the index of the measure that to which the scorePosition is clipped
	 * @param scorePosition			the position which is clipped
	 * @return
	 */
	public ScorePosition clipToMeasure(int measureIndex, ScorePosition scorePosition)
	{
		Fraction beat = null;
		if (scorePosition.getMeasure() < measureIndex)
		{
			beat = Fraction._0;
		}
		else if (measureIndex == scorePosition.getMeasure())
		{
			beat = scorePosition.getBeat();
		}
		else
		{
			beat = getMeasureBeats(measureIndex);
		}
		return new ScorePosition(scorePosition.getStaff(), measureIndex, beat, scorePosition
			.getVoice());
	}


}
