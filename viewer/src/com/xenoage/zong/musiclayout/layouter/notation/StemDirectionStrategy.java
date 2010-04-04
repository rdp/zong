package com.xenoage.zong.musiclayout.layouter.notation;

import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.notations.chord.ChordLinePositions;


/**
 * This class is the default strategy for the stem direction
 * of single chords.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class StemDirectionStrategy
	implements ScoreLayouterStrategy
{


	/**
	 * Computes the direction of the stem, using a given chord and
	 * its musical context.
	 */
	public StemDirection computeStemDirection(Chord chord, MusicContext context)
	{
		StemDirection direction = StemDirection.None;
		
		//stem specified in chord? then use it
		if (chord.getStem() != null && chord.getStem().getDirection() != null)
		{
			direction = chord.getStem().getDirection();
		}
		else
		{
			//stem needed? if not, return None
			if (!isStemNeeded(chord))
			{
				direction = StemDirection.None;
			}
			else
			{
				//compute line positions of the notes
				ChordLinePositions chordLp = new ChordLinePositions(chord, context);
				//number of lines
				int linesCount = 5; //TODO context.getLines();
				//do the work
				direction = computeStemDirection(chordLp, linesCount);
			}
		}
		
		return direction;
	}


	/**
	* Computes the direction of the stem by the linepositions of the chord.
	* @param chordLp        the line positions of all notes of the chord
	* @param linesCount     the number of lines in this staff
	* @return  the direction of the stem
	* 
	* //LAYOUT-PERFORMANCE (needed 4 of 60 seconds)
	*/
	public StemDirection computeStemDirection(ChordLinePositions chordLp, int linesCount)
	{
		int distanceLowestNoteFromMidline = Math.abs(getDistanceFromMidline(chordLp
			.getBottom(), linesCount));
		int distanceHighestNoteFromMidline = Math.abs(getDistanceFromMidline(chordLp
			.getTop(), linesCount));
		if (distanceLowestNoteFromMidline > distanceHighestNoteFromMidline)
		{
			return StemDirection.Up;
		}
		else if (distanceLowestNoteFromMidline == distanceHighestNoteFromMidline)
		{
			//If the majority of notes on the stem are above the middleline, 
			//a down stem is used, otherwise an up-stem
			int above = 0;
			int below = 0;
			for (int i = 0; i < chordLp.getNotesCount(); i++)
			{
				int d = getDistanceFromMidline(chordLp.get(i), linesCount);
				if (d > 0)
				{
					above++;
				}
				else if (d < 0)
				{
					below++;
				}
			}
			if (Math.abs(below) > Math.abs(above))
			{
				return StemDirection.Up;
			}
			else
			{
				return StemDirection.Down;
			}
		}
		return StemDirection.Down; //Also returned if distanceLowestNoteFromMidline < distanceHighestNoteFromMidline
	}


	/**
	* Returns true, if the given chord needs a stem.
	* //LAYOUT-PERFORMANCE (needed 1 of 60 seconds)
	*/
	private boolean isStemNeeded(Chord chord)
	{
		//all chords shorter than 1/1 need a stem
		return chord.getDuration().toFloat() < 1;
	}


	private int getDistanceFromMidline(int linePosition, int linesCount)
	{
		return linePosition - (linesCount - 1);
	}


}
