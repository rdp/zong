package com.xenoage.zong.io.score.selections;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.*;
import com.xenoage.zong.data.music.*;
import com.xenoage.zong.io.score.ScoreInput;
import com.xenoage.zong.util.exceptions.InvalidScorePositionException;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * Cursor for a score document.
 * 
 * It contains the current position within the score and provides many methods
 * to write or remove elements and move the cursor.
 * 
 * If the move flag is set, the cursor jumps to the end of written elements
 * instead of staying at its old position.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class Cursor
	implements Selection
{

	//parent score input class
	ScoreInput input;

	//current position of the cursor
	ScorePosition pos;

	//move with input?
	boolean moveFlag;


	public Cursor(ScoreInput input, ScorePosition pos, boolean moveFlag)
	{
		if (input == null || pos == null)
			throw new IllegalArgumentException("input and pos may not be null");
		input.ensureVoiceExists(input.getMeasure(pos.getStaff(), pos.getMeasure()), pos.getVoice());
		this.input = input;
		this.pos = pos;
		this.moveFlag = moveFlag;
	}


	/**
	 * Gets the score this cursor belongs to.
	 */
	public Score getScore()
	{
		return input.getScore();
	}


	/**
	 * Gets the current position of the cursor.
	 */
	public ScorePosition getScorePosition()
	{
		return pos;
	}


	/**
	 * Invoked when a key has been pressed.
	 */
//TODO no. not here. public void keyPressed(ScoreKeyEvent e)
/*
		App.getInstance().getKeyboardScoreInput().keyPressed(input, e);
	} */


	/**
	 * Unsupported.
	 */
	public void write(Pitch[] pitches)
		throws InvalidScorePositionException, MeasureFullException, Exception
	{
		throw new UnsupportedOperationException("Unsupported. Use the PitchCursor of the viewer project.");
	}


	/**
	 * Writes the {@link ChordData}.
	 */
	@Deprecated
	public void write(ChordData chordData)
		throws InvalidScorePositionException, MeasureFullException, Exception
	{
		Chord chord = new Chord(chordData);
		write(chord);
	}


	/**
	 * Writes the {@link RestData}.
	 */
	@Deprecated
	public void write(RestData restData)
		throws InvalidScorePositionException, MeasureFullException, Exception
	{
		Rest rest = new Rest(restData);
		write(rest);
	}


	/**
	 * Writes the given {@link VoiceElement} at the current position and moves
	 * the cursor forward according to the duration of the element.
	 * 
	 * This method overwrites the elements overlapping the current cursor
	 * position (but not {@link NoVoiceElement}s like key or time signatures at
	 * the cursor position) and overlapping the current cursor position plus the
	 * duration of the given element.
	 * 
	 * Thus, if gaps appear before or after the written element, they are filled
	 * with rests.
	 */
	public void write(VoiceElement element)
		throws InvalidScorePositionException, MeasureFullException
	{
		try
		{
			// create the voice, if needed
			input.ensureVoiceExists(input.getScore().getMeasure(pos.getStaff(),
				pos.getMeasure()), pos.getVoice());

			// write the element
			input.getScore().getController().writeAt(element, pos);

			// if move flag is set, move cursor forward, also over measure
			// boundaries
			if (moveFlag && element.getDuration() != null)
			{
				Fraction newBeat = pos.getBeat().add(element.getDuration());
				// if this beat is behind the end of the measure, jump into
				// the next measure
				Fraction measureBeats = input.getScore().getController().getTimeAtOrBefore(pos.getStaff(), pos.getMeasure()).getBeatsPerMeasure();
				if (measureBeats != null && newBeat.compareTo(measureBeats) >= 0)
				{
					// begin new measure
					input.ensureMeasureExists(pos.getMeasure() + 1);
					pos = new ScorePosition(pos.getStaff(), pos.getMeasure() + 1,
						new Fraction(0, 1), pos.getVoice());
				}
				else
				{
					// stay in the current measure
					pos = new ScorePosition(pos.getStaff(), pos.getMeasure(), newBeat,
						pos.getVoice());
				}
			}
		}
		catch (InvalidScorePositionException ex)
		{
			input.handleException(ex);
		}
		catch (MeasureFullException ex)
		{
			input.handleException(ex);
		}
	}


	/**
	 * Writes the given {@link NoVoiceElement} at the current position.
	 * 
	 * This method overwrites the {@link VoiceElement} overlapping the current
	 * cursor position. If there is already a {@link NoVoiceElement} of the same
	 * type, it is overwritten too.
	 * 
	 * If gaps appear before or after the written element because of a romoved
	 * {@link VoiceElement}, they are filled with rests.
	 */
	public void write(NoVoiceElement element)
		throws InvalidScorePositionException
	{
		try
		{
			// create the voice, if needed
			input.ensureVoiceExists(input.getScore().getMeasure(pos.getStaff(),
				pos.getMeasure()), pos.getVoice());

			// write the element
			input.getScore().getController().writeAt(element, pos);
		}
		catch (InvalidScorePositionException ex)
		{
			input.handleException(ex);
		}
	}

	/**
	 * Returns whether the cursor is moved when the write method is executed
	 */
	public boolean isMoveFlag()
	{
		return moveFlag;
	}

	/**
	 * Sets whether the cursor is moved when the write method is executed
	 */
	public void setMoveFlag(boolean moveFlag)
	{
		this.moveFlag = moveFlag;
	}


}
