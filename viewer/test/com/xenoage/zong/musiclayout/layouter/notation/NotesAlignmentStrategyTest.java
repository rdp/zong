package com.xenoage.zong.musiclayout.layouter.notation;

import static com.xenoage.util.Delta.DELTA_FLOAT;
import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.Pitch.pi;
import static org.junit.Assert.*;

import com.xenoage.util.Delta;
import com.xenoage.zong.core.music.*;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.ChordFactory;
import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.musiclayout.Constants;
import com.xenoage.zong.musiclayout.notations.chord.NotesAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NoteAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NoteSuspension;

import org.junit.Before;
import org.junit.Test;


/**
 * Test cases for the {@link NotesAlignmentStrategy}.
 *
 * @author Andreas Wenger
 */
public class NotesAlignmentStrategyTest
{
  
	private NotesAlignmentStrategy strategy;
  private MusicContext context;
  
  
  @Before public void setUp()
  {
  	strategy = new NotesAlignmentStrategy();
    context = new MusicContext();
  }
  

  /**
   * Tests a C5, 1/4. Stem: left, down. Width: 1x quarter.
   */
  @Test public void testSingleNoteC5()
  {
    Chord chord = ChordFactory.chord(pi(0, 0, 5), fr(1, 4));
    NotesAlignment cna = strategy.computeNotesAlignment(chord, StemDirection.Down, context);
    assertEquals(0, cna.getStemOffset(), DELTA_FLOAT);
    assertEquals(Constants.WIDTH_QUARTER, cna.getWidth(), DELTA_FLOAT);
    NoteAlignment na = cna.getNoteAlignment(0);
    assertEquals(5, na.getLinePosition());
    assertEquals(0, na.getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.None, na.getSuspension());
  }
  
  
  /**
   * Tests a F4, 1/2. Stem: right, up. Width: 1x half.
   */
  @Test public void testSingleNoteF4()
  {
  	Chord chord = ChordFactory.chord(pi(3, 0, 4), fr(1, 2));
    NotesAlignment cna = strategy.computeNotesAlignment(chord, StemDirection.Up, context);
    assertEquals(Constants.WIDTH_HALF, cna.getStemOffset(), DELTA_FLOAT);
    assertEquals(Constants.WIDTH_HALF, cna.getWidth(), DELTA_FLOAT);
    NoteAlignment na = cna.getNoteAlignment(0);
    assertEquals(1, na.getLinePosition());
    assertEquals(0, na.getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.None, na.getSuspension());
  }
  
  
  /**
   * Tests a C5/D5, 1/4. Stem: left, down. Width: 1x quarter.
   */
  @Test public void testChordC5D5()
  {
  	Chord chord = ChordFactory.chord(new Pitch[]{pi(0, 0, 5), pi(1, 0, 5)}, fr(1, 4));
    NotesAlignment cna = strategy.computeNotesAlignment(chord, StemDirection.Down, context);;
    assertEquals(Constants.WIDTH_QUARTER,
      cna.getStemOffset(), Delta.DELTA_FLOAT);
    assertEquals(2 * Constants.WIDTH_QUARTER, cna.getWidth(), DELTA_FLOAT);
    NoteAlignment na = cna.getNoteAlignment(0);
    assertEquals(5, na.getLinePosition());
    assertEquals(0, na.getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.Left, na.getSuspension());
    na = cna.getNoteAlignment(1);
    assertEquals(6, na.getLinePosition());
    assertEquals(Constants.WIDTH_QUARTER, na.getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.None, na.getSuspension());
  }
  
  
  /**
   * Tests a C4-E4-G4, 3/4. Stem: right, up. Width: 1x half + 1x dot.
   */
  @Test public void testChordC4E4G4()
  {
  	Chord chord = ChordFactory.chord(new Pitch[]{pi(0, 0, 4), pi(2, 0, 4),
  		pi(4, 0, 4)}, fr(3, 4));
    NotesAlignment cna = strategy.computeNotesAlignment(chord, StemDirection.Up, context);;
    assertEquals(Constants.WIDTH_HALF, cna.getStemOffset(), DELTA_FLOAT);
    assertEquals(Constants.WIDTH_HALF + Constants.WIDTH_DOT_GAP + Constants.WIDTH_DOT,
      cna.getWidth(), DELTA_FLOAT);
    assertEquals(0, cna.getNoteAlignment(0).getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.None,
      cna.getNoteAlignment(0).getSuspension());
    assertEquals(0, cna.getNoteAlignment(1).getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.None,
      cna.getNoteAlignment(1).getSuspension());
    assertEquals(0, cna.getNoteAlignment(2).getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.None,
      cna.getNoteAlignment(2).getSuspension());
  }
  
  
  /**
   * Tests a A4-C5-D5, 1/4. Stem: left, down. Width: 2x quarter.
   */
  @Test public void testChordA4C5D5()
  {
  	Chord chord = ChordFactory.chord(new Pitch[]{pi(5, 0, 4), pi(0, 0, 5),
  		pi(1, 0, 5)}, fr(1, 4));
    NotesAlignment cna = strategy.computeNotesAlignment(chord, StemDirection.Down, context);;
    float wq = Constants.WIDTH_QUARTER;
    assertEquals(wq, cna.getStemOffset(), DELTA_FLOAT);
    assertEquals(2 * wq, cna.getWidth(), DELTA_FLOAT);
    assertEquals(wq, cna.getNoteAlignment(0).getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.None,
      cna.getNoteAlignment(0).getSuspension());
    assertEquals(0, cna.getNoteAlignment(1).getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.Left,
      cna.getNoteAlignment(1).getSuspension());
    assertEquals(wq, cna.getNoteAlignment(2).getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.None,
      cna.getNoteAlignment(2).getSuspension());
  }
  
  
  /**
   * Tests the dot positions for some chords.
   */
  @Test public void testDotPositions()
  {
    //C5: position 6
  	Chord chord = ChordFactory.chord(pi(0, 0, 5), fr(3, 4));
  	NotesAlignment cna = strategy.computeNotesAlignment(chord, StemDirection.Down, context);;
    assertEquals(1, cna.getDotsPerNoteCount());
    assertEquals(1, cna.getDotPositions().length);
    assertEquals(5, cna.getDotPositions()[0]);
    //B4: position 6
    chord = ChordFactory.chord(pi(6, 0, 4), fr(7, 8));
    cna = strategy.computeNotesAlignment(chord, StemDirection.Down, context);;
    assertEquals(2, cna.getDotsPerNoteCount());
    assertEquals(1, cna.getDotPositions().length);
    assertEquals(5, cna.getDotPositions()[0]);
    //F4, F4: position 1
    chord = ChordFactory.chord(new Pitch[]{pi(3, 0, 4), pi(3, 0, 4)}, fr(7, 16));
    cna = strategy.computeNotesAlignment(chord, StemDirection.Down, context);
    assertEquals(2, cna.getDotsPerNoteCount());
    assertEquals(1, cna.getDotPositions().length);
    assertEquals(1, cna.getDotPositions()[0]);
    //F5, A5, B5: positions 7, 9, 11
    chord = ChordFactory.chord(new Pitch[]{
      pi(3, 0, 5), pi(5, 0, 5), pi(6, 0, 5)}, fr(3, 2));
    cna = strategy.computeNotesAlignment(chord, StemDirection.Down, context);;
    assertEquals(1, cna.getDotsPerNoteCount());
    assertEquals(3, cna.getDotPositions().length);
    assertEquals(7, cna.getDotPositions()[0]);
    assertEquals(9, cna.getDotPositions()[1]);
    assertEquals(11, cna.getDotPositions()[2]);
  }
  
  
  /**
   * Tests a C5/C5 (unison) chord, 1/4. Stem: left, down. Width: 2x quarter.
   */
  @Test public void testChordC5C5()
  {
  	Chord chord = ChordFactory.chord(new Pitch[]{
      pi(0, 0, 5), pi(0, 0, 5)}, fr(1, 4));
    NotesAlignment cna = strategy.computeNotesAlignment(chord, StemDirection.Down, context);
    assertEquals(Constants.WIDTH_QUARTER,
      cna.getStemOffset(), DELTA_FLOAT);
    assertEquals(2 * Constants.WIDTH_QUARTER, cna.getWidth(), DELTA_FLOAT);
    NoteAlignment na = cna.getNoteAlignment(0);
    assertEquals(5, na.getLinePosition());
    assertEquals(0, na.getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.Left, na.getSuspension());
    na = cna.getNoteAlignment(1);
    assertEquals(5, na.getLinePosition());
    assertEquals(Constants.WIDTH_QUARTER, na.getOffset(), DELTA_FLOAT);
    assertEquals(NoteSuspension.None, na.getSuspension());
  }
  
  
}

