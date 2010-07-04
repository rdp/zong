package com.xenoage.zong.musiclayout.notations.chord;

import com.xenoage.zong.musiclayout.Constants;


/**
 * This class represents the alignment
 * of the notes and the dots of a chord.
 *
 * @author Andreas Wenger
 */
public final class NotesAlignment
{
	
  private final float width;
  
  private final float noteheadWidth;
  private final NoteAlignment[] notes; //always sorted upwards
  
  private final int dotsPerNoteCount;
  private final float dotsOffset;
  private final int[] dotPositions;
  
  private final float stemOffset;
  private final boolean leftSuspendedNotes;
  
  
  /**
   * Creates a new {@link NotesAlignment}.
   * @param dotPositions  line positions of the dots, or an empty array if chord has no dots
   */
  public NotesAlignment(float width, float noteheadWidth, NoteAlignment[] notes, float dotsOffset,
  	int dotsPerNoteCount, int[] dotPositions, float stemOffset)
  {
  	this.width = width;
  	this.noteheadWidth = noteheadWidth;
  	this.notes = notes;
  	this.dotsOffset = dotsOffset;
  	this.dotsPerNoteCount = dotsPerNoteCount;
  	this.dotPositions = dotPositions;
  	this.stemOffset = stemOffset;
  	//are there left-suspended notes?
  	boolean leftSuspendedNotes = false;
  	for (NoteAlignment note : notes)
    {
      if (note.getSuspension() == NoteSuspension.Left)
      {
        //yes, there is at least one left-suspended note
      	leftSuspendedNotes = true;
      	break;
      }
    }
    this.leftSuspendedNotes = leftSuspendedNotes;
  }
  
  
  /**
   * Gets the horizontal offset of the stem in spaces.
   */
  public float getStemOffset()
  {
    return stemOffset;
  }
  
  
  /**
   * Gets the width of the chord notes and dots in spaces.
   */
  public float getWidth()
  {
    return width;
  }
  
  
  /**
   * Gets the number of notes.
   */
  public int getNotesCount()
  {
    return notes.length;
  }
  
  
  /**
   * Gets the alignment of the note with the given index.
   * The notes are sorted upwards, that means, the lowest
   * note has index 0.
   */
  public NoteAlignment getNoteAlignment(int index)
  {
    return notes[index];
  }
  
  
  /**
   * Gets the number of dots per note.
   */
  public int getDotsPerNoteCount()
  {
    return dotsPerNoteCount;
  }
  
  
  /**
   * Gets the alignment of the top note of the chord.
   */
  public NoteAlignment getTopNoteAlignment()
  {
  	return notes[notes.length - 1];
  }
  
  
  /**
   * Gets the alignment of the bottom note of the chord.
   */
  public NoteAlignment getBottomNoteAlignment()
  {
  	return notes[0];
  }
  
  
  /**
   * Gets the positions of the dots, or an empty array, if the
   * chord has no dots.
   */
  public int[] getDotPositions()
  {
    return dotPositions;
  }
  
  
  /**
   * Gets the width of a notehead.
   */
  public float getNoteheadWidth()
  {
  	return noteheadWidth;
  }
  
  
  /**
   * Returns true, if there are left-suspended notes, otherwise null.
   */
  public boolean hasLeftSuspendedNotes()
  {
    return leftSuspendedNotes;
  }
  
  
  /**
   * Gets the offset of the dots with the given
   * number (1 or 2).
   */
  public float getDotsOffset(int dotNumber)
  {
    if (dotsPerNoteCount == 0)
      return dotsOffset;
    else
      return dotsOffset + Constants.WIDTH_DOT_GAP + dotNumber * Constants.WIDTH_DOT;
  }
  
  
  /**
   * Gets the list of note alignments.
   * The notes are sorted upwards, that means, the
   * lowest note has index 0.
   */
  public NoteAlignment[] getNoteAlignments()
  {
    return notes;
  }
  
  
  /**
   * Gets the line positions of the chord (convenience method).
   */
  public ChordLinePositions getLinePositions()
  {
  	int[] ret = new int[notes.length];
  	for (int i = 0; i < ret.length; i++)
  		ret[i] = notes[i].getLinePosition();
  	return new ChordLinePositions(ret);
  }

}
