package com.xenoage.zong.io.score.selections;

import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.ChordData;
import com.xenoage.zong.data.music.Note;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.VoiceElement;
import com.xenoage.zong.io.score.ScoreInputOptions;
import com.xenoage.zong.io.score.ViewerScoreInput;
import com.xenoage.zong.io.score.ScoreInputOptions.WriteMode;
import com.xenoage.zong.util.exceptions.InvalidScorePositionException;
import com.xenoage.zong.util.exceptions.MeasureFullException;


/**
 * Implementation of a {@link Cursor} that also supports writing
 * pitches.
 * 
 * @author Andreas Wenger
 */
public class PitchCursor
	extends Cursor
{
	
	ViewerScoreInput viewerInput;
	
	
	public PitchCursor(ViewerScoreInput input, ScorePosition pos, boolean moveFlag)
	{
		super(input, pos, moveFlag);
		this.viewerInput = input;
	}
	

	@Override public void write(Pitch[] pitches)
		throws InvalidScorePositionException, MeasureFullException, Exception
	{
		write(pitches, null);
	}


	/**
	 * Writes the given pitches as a chord. The position and overwrite mode
	 * depends on the current write mode.
	 */
	public void write(Pitch[] pitches, WriteMode writemode)
		throws InvalidScorePositionException, MeasureFullException, Exception
	{
		ScoreInputOptions options = viewerInput.getOptions();
		WriteMode wm;
		if (writemode != null)
		{
			wm = writemode;
		}
		else
		{
			wm = options.getWriteMode();
		}
		if (wm == WriteMode.ChordBeforeCursor)
		{
			//add the given pitches to the chord before the cursor
			addPitchesToPrecedingChord(pitches);
		}
		else
		{
			//default behaviour: write chord after cursor
			Chord chord = new Chord(new ChordData(Note.createNotes(pitches), options.getFraction()));
			write(chord);
		}
	}
	
	
	/**
	 * Adds the given pitches to the chord before the cursor. If there is no
	 * chord, nothing is done.
	 */
	private void addPitchesToPrecedingChord(Pitch[] pitches)
	{
		// find the last voice element starting before the current beat
		VoiceElement element = input.getScore().getController()
			.getVoiceElementBefore(pos).getElement();

		// if the target element is a chord, add the given pitches to it
		if (element instanceof Chord)
		{
			Chord chord = (Chord) element;
			ChordData chordData = chord.getData();
			for (Pitch pitch : pitches)
			{
				chordData = chordData.addNote(new Note(pitch));
			}
			chord.setData(chordData);
		}
	}
	

}
