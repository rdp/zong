package com.xenoage.zong.musiclayout.layouter.notation;

import java.util.ArrayList;


import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.musiclayout.Constants;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.notations.chord.ChordLinePositions;
import com.xenoage.zong.musiclayout.notations.chord.NoteAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NoteSuspension;
import com.xenoage.zong.musiclayout.notations.chord.NotesAlignment;
import com.xenoage.zong.util.ArrayTools;


/**
 * This strategy computes the alignment of the notes
 * and the dots of a given chord.
 * 
 * The rules are adepted from
 * "Chlapik: Die Praxis des Notengraphikers", page 40.
 * 
 * The dot placing rules are based on Sibelius 1.4.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class NotesAlignmentStrategy
	implements ScoreLayouterStrategy
{
	
  
  /**
   * There are four classes of chords:
   * <ul>
   * 	<li>1) stem down without unison/second interval</li>
   * 	<li>2) stem down with unison/second interval</li>
   * 	<li>3) stem up without unison/second interval</li>
   * 	<li>4) stem up with unison/second interval</li>
   * </ul>
   */
  private enum ChordClass { StemDownNoUni, StemDownUni, StemUpNoUni, StemUpUni }
	

  /**
   * Computes the alignment of the notes of the given chord, which has a stem into
   * the given direction, using the given musical context.
   */
	public NotesAlignment computeNotesAlignment(Chord chord, StemDirection stemDirection,
		MusicContext musicContext)
	{
		ChordLinePositions lp = new ChordLinePositions(chord, musicContext);
		ChordClass chordClass = computeChordClass(lp, stemDirection);
		float noteheadWidth = getNoteheadWidth(chord.getDuration());
		float stemOffset = computeStemOffset(chordClass, noteheadWidth);
		float notesWidth = computeNotesWidth(chordClass, noteheadWidth);
    NoteAlignment[] notes = computeNotesAlignment(lp, stemDirection, stemOffset);
    int dotsCount = computeDotsCount(chord.getDuration());
    float dotsWidth = computeDotsWidth(dotsCount);
    int[] dots = (dotsCount > 0 ? computeDots(lp) : new int[0]);
    
    return new NotesAlignment(notesWidth + dotsWidth, noteheadWidth, notes,
    	notesWidth, dotsCount, dots, stemOffset);
	}
	
	
	/**
	 * Computes the {@link ChordClass} of the given chord.
	 */
	private ChordClass computeChordClass(ChordLinePositions lp, StemDirection stemDirection)
	{
		int chordClass = (stemDirection == StemDirection.Up) ? 3 : 1;
    for (int i = 1; i < lp.getNotesCount(); i++)
    {
      if (Math.abs(lp.get(i) - lp.get(i-1)) <= 1)
      {
        chordClass++;
        break;
      }
    }
    switch (chordClass)
    {
    	case 1: return ChordClass.StemDownNoUni;
    	case 2: return ChordClass.StemDownUni;
    	case 3: return ChordClass.StemUpNoUni;
    	default: return ChordClass.StemUpUni;
    }
	}
	
	
	/**
	 * Computes the offset of the stem.
	 */
	private float computeStemOffset(ChordClass chordClass, float noteheadWidth)
	{
		//the stem offsets are:
    // 0 for StemDownNoUni
    // notehead width for all other classes
    if (chordClass == ChordClass.StemDownNoUni)
      return 0;
    else
    	return noteheadWidth;
	}
	
	
	/**
	 * Computes the width of the notes of the chord.
	 */
	private float computeNotesWidth(ChordClass chordClass, float noteheadWidth)
	{
		//the widths are:
    // NoUni: notehead width
    // Uni: 2 * notehead width
    if (chordClass == ChordClass.StemDownNoUni || chordClass == ChordClass.StemUpNoUni)
      return noteheadWidth;
    else
      return 2 * noteheadWidth;
	}
	
	
	/**
   * Computes and sets the note alignments,
   * the stem offset and the width.
   */
	private NoteAlignment[] computeNotesAlignment(ChordLinePositions lp, StemDirection sd, float stemOffset)
  {
  	NoteAlignment[] notes = new NoteAlignment[lp.getNotesCount()];
    
    //if stem direction is down or none, begin with the highest note,
    //otherwise with the lowest
    int dir = (sd == StemDirection.Up) ? 1 : -1;
    int startIndex = (dir == 1) ? 0 : lp.getNotesCount() - 1;
    int endIndex = lp.getNotesCount() - 1 - startIndex;
    //default side of the stem. 1 = right, 0 = left
    int stemSide = (sd == StemDirection.Up) ? 1 : 0; 
    int lastSide = stemSide;
    for (int i = startIndex; dir * i <= dir * endIndex; i += dir)
    {
      int side = 1 - stemSide;
      NoteSuspension suspension = NoteSuspension.None;
      if (i == startIndex)
      {
        //first note: use default side
      }
      else
      {
        //following note: change side, if last note is 1 or 0 steps away
        if (Math.abs(lp.get(i) - lp.get(i-dir)) <= 1)
        {
          //change side
          side = 1 - lastSide;
          if (side != 1 - stemSide)
          {
            if (side == 0)
              suspension = NoteSuspension.Left;
            else
              suspension = NoteSuspension.Right;
          }
        }
      }
      notes[i] = new NoteAlignment(lp.get(i),
        side * stemOffset, suspension);
      lastSide = side;
    }
    
    return notes;
  }
	
	
	/**
	 * Computes the number of prolongation dots, e.g. 2 if it is
	 * a half+quarter+eighth note (a half with two dots).
	 */
	private int computeDotsCount(Fraction duration)
	{
		int num = duration.getNumerator();
    if (num == 3) //if numerator == 3, we have one dot
      return 1;
    else if (num == 7) //if numerator == 7, we have two dots
    	return 2;
    else //otherwise we have no dots
    	return 0;
	}
	
	
	/**
   * Computes the widths of the dots.
   */
  private float computeDotsWidth(int dotsCount)
  {
    if (dotsCount == 0)
      return 0f;
    else
      return Constants.WIDTH_DOT_GAP + dotsCount * Constants.WIDTH_DOT;
  }
  
  
  /**
   * Computes the position of the dots of the chord.
   */
  private int[] computeDots(ChordLinePositions lp)
  {
    //compute the position of the dots
    ArrayList<Integer> dots = new ArrayList<Integer>();
    ArrayList<Integer> dotLinePos = new ArrayList<Integer>();
    for (int i = lp.getNotesCount() - 1; i >= 0; i--)
    {
      int dot = lp.get(i);
      //place dot between (leger) lines, not on them
      if (dot % 2 == 0) 
        dot++;
      //add dot
      if (!dots.contains(dot) && !dotLinePos.contains(lp.get(i)))
      {
        dots.add(0, dot);
        dotLinePos.add(lp.get(i));
      }
      else
      {
        //there is already a dot. try to place it 2 positions lower,
        //but only if the there is not already a dot for this pitch
        if (!dots.contains(dot - 2) && !dotLinePos.contains(lp.get(i)))
        {
          dots.add(0, dot - 2);
        }
      }
    }
    return ArrayTools.toIntArray(dots);
  }
  
  
  /**
   * Gets the width of the notehead of the given duration.
   * TODO: place elsewhere?
   */
  private float getNoteheadWidth(Fraction duration)
  {
    float dur = duration.getNumerator() / duration.getDenominator();
    if (dur >= 1)
      return Constants.WIDTH_WHOLE;
    else if (dur >= 2)
      return Constants.WIDTH_HALF;
    else
      return Constants.WIDTH_QUARTER;
  }
  
  

}
