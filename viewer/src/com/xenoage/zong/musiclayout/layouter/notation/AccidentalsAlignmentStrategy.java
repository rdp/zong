package com.xenoage.zong.musiclayout.layouter.notation;

import static com.xenoage.zong.musiclayout.notations.chord.AccidentalsAlignment.WIDTH_GAP_ACCTOACC;
import static com.xenoage.zong.musiclayout.notations.chord.AccidentalsAlignment.WIDTH_GAP_ACCTONOTE;
import static com.xenoage.zong.musiclayout.notations.chord.AccidentalsAlignment.computeAccidentalsMaxWidth;
import static com.xenoage.zong.musiclayout.notations.chord.AccidentalsAlignment.getAccidentalWidth;

import com.xenoage.pdlib.Vector;
import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.core.music.chord.Accidental;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.notations.chord.AccidentalAlignment;
import com.xenoage.zong.musiclayout.notations.chord.AccidentalsAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NoteAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NotesAlignment;


/**
 * This strategy stores the alignment of
 * the accidentals of the given chord.
 * 
 * @author Andreas Wenger
 */
public class AccidentalsAlignmentStrategy
	implements ScoreLayouterStrategy
{

	
	/**
	 * Computes the alignment of the accidentals of the given chord,
	 * using the given note alignments and the given musical context,
	 * and returns it. If there are no accidentals, null is returned.
	 */
	public AccidentalsAlignment computeAccidentalsAlignment(Chord chord,
		NotesAlignment notesAlignment, MusicContext mc)
	{
		return computeAccidentalsAlignment(chord.getPitches(),
			notesAlignment.getNoteAlignments(), mc);
	}
	
	
	/**
   * Creates a new {@link AccidentalsAlignment} for the given notes,
   * or null if there are no accidentals.
   */
  AccidentalsAlignment computeAccidentalsAlignment(
  	Vector<Pitch> pitches, NoteAlignment[] alignments, MusicContext mc)
  {
    //count number of needed accidentals
    int accCount = 0;
    for (Pitch pitch : pitches)
    {
      Accidental.Type acc = mc.getAccidentalType(pitch);
      if (acc != null)
        accCount++;
    }
    //there are different alignment algorithms for each number
    if (accCount == 1)
      return computeAlignment1Accidental(pitches, alignments, mc);
    else if (accCount == 2)
    	return computeAlignment2Accidentals(pitches, alignments, mc);
    else if (accCount == 3)
    	return computeAlignment3Accidentals(pitches, alignments, mc);
    else if (accCount > 3)
    	throw new IllegalStateException("Chords with more than 3 accidentals not supported yet!"); //TODO
    else
    	return null;
  }
  
  
  /**
   * Computes the accidentals alignment for a chord
   * with one accidental.
   */
  private AccidentalsAlignment computeAlignment1Accidental(Vector<Pitch> pitches,
    NoteAlignment[] alignments, MusicContext mc)
  {
    for (int i = 0; i < pitches.size(); i++)
    {
      Accidental.Type at = mc.getAccidentalType(pitches.get(i));
      if (at != null)
      {
      	AccidentalAlignment[] a = {new AccidentalAlignment(
          alignments[i].getLinePosition(), 0, at)};
        float width = getAccidentalWidth(at) + WIDTH_GAP_ACCTONOTE;
        return new AccidentalsAlignment(a, width);
      }
    }
    throw new IllegalArgumentException("Invalid chord");
  }
  
  
  /**
   * Computes the accidentals alignment for a chord
   * with two accidentals.
   */
  private AccidentalsAlignment computeAlignment2Accidentals(Vector<Pitch> pitches,
    NoteAlignment[] alignments, MusicContext context)
  {
    //compute index of top and bottom note with accidental
    boolean[] checklist = computeAccidentalsChecklist(
      pitches, alignments, context);
    int topNoteIndex = computeLastTrueEntryIndex(checklist);
    checklist[topNoteIndex] = false;
    int bottomNoteIndex = computeLastTrueEntryIndex(checklist);
    //compute accidental types
    Accidental.Type atTop = context.getAccidentalType(pitches.get(topNoteIndex));
    Accidental.Type atBottom = context.getAccidentalType(pitches.get(bottomNoteIndex));
    //interval of at least a seventh?
    int distance = alignments[topNoteIndex].getLinePosition() -
      alignments[bottomNoteIndex].getLinePosition();
    AccidentalAlignment[] a = new AccidentalAlignment[2];
    float width;
    if (distance >= 6)
    {
      //placed on the same horizontal position
    	a[0] = new AccidentalAlignment(alignments[bottomNoteIndex].getLinePosition(), 0, atBottom);
    	a[1] = new AccidentalAlignment(alignments[topNoteIndex].getLinePosition(), 0, atTop);
      width = computeAccidentalsMaxWidth(
        new Accidental.Type[]{atBottom, atTop}) + WIDTH_GAP_ACCTONOTE;
    }
    else
    {
      //placed on different horizontal positions
      //normally begin with the bottom accidental
      boolean bottomFirst = true;
      //when the bottom accidental note is suspended
      //on the right side of the stem, and the top accidental note
      //is not, then the bottom accidental
      //is nearer to the note (see Ross, p. 132)
      if (alignments[bottomNoteIndex].getOffset() >
        alignments[topNoteIndex].getOffset())
      {
        bottomFirst = false;
      }
      if (bottomFirst)
      {
        //begin with bottom note
      	a[0] = new AccidentalAlignment(alignments[bottomNoteIndex].getLinePosition(), 0, atBottom);
      	a[1] = new AccidentalAlignment(alignments[topNoteIndex].getLinePosition(),
      			getAccidentalWidth(atBottom) + WIDTH_GAP_ACCTOACC, atTop);
      }
      else
      {
        //begin with top note
      	a = new AccidentalAlignment[]{
      		new AccidentalAlignment(alignments[bottomNoteIndex].getLinePosition(),
      			getAccidentalWidth(atTop) + WIDTH_GAP_ACCTOACC, atBottom),
      		new AccidentalAlignment(alignments[topNoteIndex].getLinePosition(), 0f, atTop)
      	};
      }
      //compute width
      width = getAccidentalWidth(atBottom) + WIDTH_GAP_ACCTOACC +
        getAccidentalWidth(atTop) + WIDTH_GAP_ACCTONOTE;
    }
    return new AccidentalsAlignment(a, width);
  }
  
  
  /**
   * Computes the accidentals alignment for a chord with three accidentals.
   * The 6 rules are adepted from Ross, page 132 f.
   */
  private AccidentalsAlignment computeAlignment3Accidentals(Vector<Pitch> pitches,
    NoteAlignment[] alignments, MusicContext context)
  {
    //compute index of top, middle and bottom note with accidental
    boolean[] checklist = computeAccidentalsChecklist(
      pitches, alignments, context);
    int topNoteIndex = computeLastTrueEntryIndex(checklist);
    checklist[topNoteIndex] = false;
    int middleNoteIndex = computeLastTrueEntryIndex(checklist);
    checklist[middleNoteIndex] = false;
    int bottomNoteIndex = computeLastTrueEntryIndex(checklist);
    //compute accidental types
    Accidental.Type atTop = context.getAccidentalType(pitches.get(topNoteIndex));
    Accidental.Type atMiddle = context.getAccidentalType(pitches.get(middleNoteIndex));
    Accidental.Type atBottom = context.getAccidentalType(pitches.get(bottomNoteIndex));
    //interval of at least a seventh?
    int distance = alignments[topNoteIndex].getLinePosition() -
      alignments[bottomNoteIndex].getLinePosition();
    AccidentalAlignment[] a = new AccidentalAlignment[3];
    float width;
    if (distance >= 6)
    {
      //interval of at least a seventh. can be rule 1, 3 or 4
      if (alignments[topNoteIndex].getOffset() > 0f)
      {
        //top note is suspended on the right side of the stem.
        //this is rule 4. (same code as rule 1)
        a[1] = new AccidentalAlignment(alignments[middleNoteIndex].getLinePosition(), 0f, atMiddle);
        float middleWidth = getAccidentalWidth(atMiddle);
        a[0] = new AccidentalAlignment(alignments[bottomNoteIndex].getLinePosition(), middleWidth + WIDTH_GAP_ACCTOACC, atBottom);
        a[2] = new AccidentalAlignment(alignments[topNoteIndex].getLinePosition(), middleWidth + WIDTH_GAP_ACCTOACC, atTop);
        width = middleWidth + WIDTH_GAP_ACCTOACC +
          computeAccidentalsMaxWidth(new Accidental.Type[]{atBottom, atTop}) + WIDTH_GAP_ACCTONOTE;
      }
      else if (alignments[middleNoteIndex].getOffset() > 0f)
      {
        //middle note is suspended on the right side of the stem.
        //(bottom note is never suspended on the right) (TODO: really?)
        //this is rule 3.
        a[0] = new AccidentalAlignment(alignments[bottomNoteIndex].getLinePosition(), 0f, atBottom);
        float bottomWidth = getAccidentalWidth(atBottom);
        a[2] = new AccidentalAlignment(alignments[topNoteIndex].getLinePosition(), bottomWidth + WIDTH_GAP_ACCTOACC, atTop);
        float topWidth = getAccidentalWidth(atTop);
        a[1] = new AccidentalAlignment(alignments[middleNoteIndex].getLinePosition(), bottomWidth + WIDTH_GAP_ACCTOACC +
        	topWidth + WIDTH_GAP_ACCTOACC, atMiddle);
        width = bottomWidth + WIDTH_GAP_ACCTOACC + topWidth + WIDTH_GAP_ACCTOACC +
        	getAccidentalWidth(atMiddle) + WIDTH_GAP_ACCTONOTE;
      }
      else
      {
        //there are no accidental notes suspended on the right side of the stem.
        //this is rule 1.
        a[1] = new AccidentalAlignment(alignments[middleNoteIndex].getLinePosition(), 0f, atMiddle);
        float middleWidth = getAccidentalWidth(atMiddle);
        a[0] = new AccidentalAlignment(alignments[bottomNoteIndex].getLinePosition(), middleWidth + WIDTH_GAP_ACCTOACC, atBottom);
        a[2] = new AccidentalAlignment(alignments[topNoteIndex].getLinePosition(), middleWidth + WIDTH_GAP_ACCTOACC, atTop);
        width = middleWidth + WIDTH_GAP_ACCTOACC +
          computeAccidentalsMaxWidth(new Accidental.Type[]{atBottom, atTop}) + WIDTH_GAP_ACCTONOTE;
      }
    }
    else
    {
      //interval of less than a seventh. can be rule 2, 5 or 6
      if (alignments[topNoteIndex].getOffset() > 0f)
      {
        //top note is suspended on the right side of the stem.
        //this is rule 5. (same code as rule 2)
        a[1] = new AccidentalAlignment(alignments[middleNoteIndex].getLinePosition(), 0f, atMiddle);
        float middleWidth = getAccidentalWidth(atMiddle);
        a[0] = new AccidentalAlignment(alignments[bottomNoteIndex].getLinePosition(), middleWidth + WIDTH_GAP_ACCTOACC, atBottom);
        float bottomWidth = getAccidentalWidth(atBottom);
        a[2] = new AccidentalAlignment(alignments[topNoteIndex].getLinePosition(),
        	middleWidth + WIDTH_GAP_ACCTOACC + bottomWidth + WIDTH_GAP_ACCTOACC, atTop);
        float topWidth = getAccidentalWidth(atTop);
        width = middleWidth + WIDTH_GAP_ACCTOACC + bottomWidth + WIDTH_GAP_ACCTOACC + topWidth + WIDTH_GAP_ACCTONOTE;
      }
      else if (alignments[middleNoteIndex].getOffset() > 0f)
      {
        //middle note is suspended on the right side of the stem.
        //(bottom note is never suspended on the right)
        //this is rule 6. (same code as rule 3)
        a[0] = new AccidentalAlignment(alignments[bottomNoteIndex].getLinePosition(), 0f, atBottom);
        float bottomWidth = getAccidentalWidth(atBottom);
        a[2] = new AccidentalAlignment(alignments[topNoteIndex].getLinePosition(), bottomWidth + WIDTH_GAP_ACCTOACC, atTop);
        float topWidth = getAccidentalWidth(atTop);
        a[1] = new AccidentalAlignment(alignments[middleNoteIndex].getLinePosition(), bottomWidth + WIDTH_GAP_ACCTOACC +
        	topWidth + WIDTH_GAP_ACCTOACC, atMiddle);
        width = bottomWidth + WIDTH_GAP_ACCTOACC + topWidth + WIDTH_GAP_ACCTOACC +
          getAccidentalWidth(atMiddle) + WIDTH_GAP_ACCTONOTE;
      }
      else
      {
        //there are no accidental notes suspended on the right side of the stem.
        //this is rule 2.
        a[1] = new AccidentalAlignment(alignments[middleNoteIndex].getLinePosition(), 0f, atMiddle);
        float middleWidth = getAccidentalWidth(atMiddle);
        a[0] = new AccidentalAlignment(alignments[bottomNoteIndex].getLinePosition(), middleWidth + WIDTH_GAP_ACCTOACC, atBottom);
        float bottomWidth = getAccidentalWidth(atBottom);
        a[2] = new AccidentalAlignment(alignments[topNoteIndex].getLinePosition(),
          middleWidth + WIDTH_GAP_ACCTOACC + bottomWidth + WIDTH_GAP_ACCTOACC, atTop);
        float topWidth = getAccidentalWidth(atTop);
        width = middleWidth + WIDTH_GAP_ACCTOACC + bottomWidth + WIDTH_GAP_ACCTOACC + topWidth + WIDTH_GAP_ACCTONOTE;
      }
    }
    return new AccidentalsAlignment(a, width);
  }
  
  
  /**
   * Computes and returns a boolean array for the given
   * notes, with a true indicating that a accidental is needed
   * for this note, and a false indicating that no accidental is needed.
   */
  private boolean[] computeAccidentalsChecklist(Vector<Pitch> pitches,
    NoteAlignment[] alignments, MusicContext context)
  {
    boolean[] ret = new boolean[pitches.size()];
    for (int i = 0; i < pitches.size(); i++)
    {
      ret[i] = (context.getAccidentalType(pitches.get(i)) != null);
    }
    return ret;
  }
  
  
  /**
   * Computes the index of the last field containing
   * true in the given array, or -1 if there is none.
   */
  private int computeLastTrueEntryIndex(boolean[] checklist)
  {
    for (int i = checklist.length - 1; i >= 0; i--)
    {
      if (checklist[i])
        return i;
    }
    return -1;
  }

}
