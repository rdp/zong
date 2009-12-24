package com.xenoage.zong.io.score;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.ChordData;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.NoVoiceElement;
import com.xenoage.zong.data.music.RestData;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.io.score.selections.Selection;
import com.xenoage.zong.util.exceptions.InvalidScorePositionException;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * This class is a convenient interface between different input systems
 * (Keyboard, Mouse, MIDI Keyboard, XML Input) and the {@link Score} class.
 * 
 * While it is more flexible to work with the Score class directly, this class
 * allows more intelligent and easier editing.
 * 
 * It also manages the current selection, which may be of many types (cursor,
 * single element selection, staves selection, ...).
 * 
 * @author Andreas Wenger
 */
public class ScoreInput
{

	//the edited score
	Score score;

	//the current selection, or null
	Selection selection;


	/**
	 * Creates a new input interface for the given {@link Score}.
	 */
	public ScoreInput(Score score)
	{
		this.score = score;
		this.selection = null;
	}


	/**
	 * Gets the score this input interface is working on.
	 */
	public Score getScore()
	{
		return score;
	}


	/**
	 * Sets the current selection. It may also be null, which means that nothing
	 * is selected.
	 */
	public Selection getSelection()
	{
		return selection;
	}


	/**
	 * Sets the current selection. It may also be null, which means that nothing
	 * is selected.
	 */
	public void setSelection(Selection selection)
	{
		this.selection = selection;
	}


	/**
	 * Writes a single-note chord with the given Pitch, depending on the current
	 * selection. If nothing is selected, the element is not written.
	 */
	public void write(Pitch pitch)
		throws InvalidScorePositionException, MeasureFullException, Exception
	{
		write(new Pitch[] { pitch });
	}


	/**
	 * Writes a chord with the given pitches, depending on the current
	 * selection. If nothing is selected, the element is not written.
	 */
	public void write(Pitch[] pitches)
		throws InvalidScorePositionException, MeasureFullException, Exception
	{
		if (selection != null)
		{
			selection.write(pitches);
		}
	}


	/**
	 * Writes a chord.
	 */
	public void write(ChordData chordData)
		throws InvalidScorePositionException, MeasureFullException, Exception
	{
		if (selection != null)
		{
			selection.write(chordData);
		}
	}


	/**
	 * Writes a rest.
	 */
	public void write(RestData restData)
		throws InvalidScorePositionException, MeasureFullException, Exception
	{
		if (selection != null)
		{
			selection.write(restData);
		}
	}


	/**
	 * Writes the given {@link NoVoiceElement}, depending on the current
	 * selection. If nothing is selected, the element is not written.
	 */
	public void write(NoVoiceElement element)
		throws InvalidScorePositionException, MeasureFullException, Exception
	{
		if (selection != null)
			selection.write(element);
	}


	/**
	 * Handles the given exception by just throwing it further. Can be overridden
	 * for error handling that fits the need of your application.
	 */
	public <T extends Exception> void handleException(T ex) throws T
	{
		throw ex;
	}


	/**
	 * Returns the measure with the given index from the given staff, or throws
	 * a InvalidScorePositionException if it does not exist.
	 */
	public Measure getMeasure(int staff, int measure)
		throws InvalidScorePositionException
	{
		ScorePosition pos = new ScorePosition(staff, measure, 0, 0, 0);
		if (score.getStavesCount() - 1 < staff || score.getMeasuresCount() - 1 < measure)
		{
			throw new InvalidScorePositionException(pos);
		}
		else
		{
			return score.getStaff(staff).getMeasures().get(measure);
		}
	}


	/**
	 * Ensures that the measure with the given index exists. If not, the missing
	 * measures are created (empty measures).
	 */
	public void ensureMeasureExists(int measure)
	{
		if (score.getMeasuresCount() - 1 < measure)
		{
			score.addEmptyMeasures(measure - score.getMeasuresCount() + 1);
		}
	}


	/**
	 * Ensures that the given measure has the given voice. If not, the voices up
	 * to the given one are created.
	 */
	public void ensureVoiceExists(Measure measure, int voice)
	{
		while (measure.getVoices().size() - 1 < voice)
		{
			measure.addVoice();
		}
	}


	/**
	 * Gets the {@link Voice} at the given staff, measure and voice.
	 */
	public Voice getVoiceAt(ScorePosition pos) throws InvalidScorePositionException
	{
		return getVoiceAt(pos.getStaff(), pos.getMeasure(), pos.getVoice());
	}


	/**
	 * Gets the {@link Voice} at the given staff, measure and voice.
	 */
	public Voice getVoiceAt(int staff, int measure, int voice)
		throws InvalidScorePositionException
	{
		try
		{
			Measure omeasure = getMeasure(staff, measure);
			ensureVoiceExists(omeasure, voice);
			return omeasure.getVoices().get(voice);
		}
		catch (Exception ex)
		{
			throw new InvalidScorePositionException(new ScorePosition(staff, measure,
				new Fraction(0), voice));
		}
	}

}
