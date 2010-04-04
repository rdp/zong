package com.xenoage.zong.musiclayout.layouter.notation;

import static com.xenoage.zong.core.music.chord.StemDirection.Down;
import static com.xenoage.zong.core.music.chord.StemDirection.None;
import static com.xenoage.zong.core.music.chord.StemDirection.Up;

import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.notations.chord.NoteAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NotesAlignment;
import com.xenoage.zong.musiclayout.notations.chord.StemAlignment;


/**
 * A {@link StemAlignmentStrategy} computes the
 * length of single chord stems.
 * 
 * If there is a fixed stem length, it is used, otherwise
 * a good value is computed.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class StemAlignmentStrategy
	implements ScoreLayouterStrategy
{
	
	
	/**
   * Computes the vertical position of the stem of the given chord layout.
   * @param stem        the stem of the chord, or null for default stem
   * @param na          the positions of the notes of of the chord
   * @param sd          the direction of the stem
   * @param linesCount  the number of lines in this staff
   * @return  the vertical position of the stem, or <code>null</code> if the chord has no stem.
   */
	public StemAlignment computeStemAlignment(com.xenoage.zong.core.music.chord.Stem stem,
		NotesAlignment na, StemDirection sd, int linesCount)
	{
		NoteAlignment high = na.getNoteAlignment(na.getNotesCount() - 1);
		NoteAlignment low = na.getNoteAlignment(0);
		float startlineposition = 0;
		float endlineposition = 0;
		int middlelineposition = 4; //TODO calculate

		//use a stem?
		if (sd == None)
		{
			return null;
		}
		
		//compute start position
		if (sd == Down)
		{
			startlineposition = high.getLinePosition();
		}
		else if (sd == Up)
		{
			startlineposition = low.getLinePosition();
		}
		
		//compute end position
		if (stem != null && stem.getLength() != null)
		{
			//used fixed length
			if (sd == Down)
			{
				endlineposition = low.getLinePosition() - 2 * stem.getLength();
			}
			else if (sd == Up)
			{
				endlineposition = high.getLinePosition() + 2 * stem.getLength();
			}
		}
		else
		{
			//compute length
			if (sd == Down)
			{
				endlineposition = low.getLinePosition() - 7;
				if (endlineposition > middlelineposition)
				{
					endlineposition = middlelineposition;
				}
			}
			else if (sd == Up)
			{
				startlineposition = low.getLinePosition();
				endlineposition = high.getLinePosition() + 7;
				if (endlineposition < middlelineposition)
				{
					endlineposition = middlelineposition;
				}
			}
		}
		
		StemAlignment stemAlignment = new StemAlignment(startlineposition,
			endlineposition);
		return stemAlignment;
	}

}
