package com.xenoage.zong.io.score.selections;

import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.data.ScorePosition.sp;
import static org.junit.Assert.*;

import com.xenoage.util.RAList;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.music.*;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.time.NormalTime;
import com.xenoage.zong.io.score.ScoreInput;
import com.xenoage.zong.util.exceptions.InvalidScorePositionException;
import com.xenoage.zong.util.exceptions.MeasureFullException;

import org.junit.Test;


/**
 * Test cases for a Cursor.
 *
 * @author Andreas Wenger
 */
public class CursorTest
{
  
  
  /**
   * Tests the write(MusicElement) method.
   * Overwrites other note with equal duration.
   */
  @Test public void writeTest1()
  {
    //create test score with two half notes
    Score score = createTestScoreHalfHalf();
    ScoreInput input = new ScoreInput(score);
    //change first half note from test score
    try
    {
      input.setSelection(new Cursor(input, sp(1, 1, fr(0, 4), 0), true));
      input.write(new ChordData(new Note(new Pitch(2, 0, 4)), fr(2, 4)));
    }
    catch (Exception ex)
    {
      fail();
    }
    //check elements
    Voice voice = score.getStaff(1).getMeasures().get(1).getVoices().get(0);
    RAList<MusicElement> elements = voice.getElements();
    assertEquals(4, elements.size());
    assertTrue(elements.get(0) instanceof Clef);
    assertTrue(elements.get(1) instanceof NormalTime);
    assertTrue(elements.get(2) instanceof Chord);
    assertEquals(2, ((Chord) elements.get(2)).getNotes()[0].getPitch().getStep());
    assertTrue(elements.get(3) instanceof Chord);
    assertEquals(0, ((Chord) elements.get(3)).getNotes()[0].getPitch().getStep());
  }
  
  
  /**
   * Tests the write(MusicElement) method.
   * Overwrites notes and fill front and rear gap
   * with rests.
   */
  @Test public void writeTest2()
  {
    //create test score with two half notes
    Score score = createTestScoreHalfHalf();
    ScoreInput input = new ScoreInput(score);
    //insert half note at beat 1. the two half notes
    //must be deleted and a quarter rest, half note and quarter
    //rest must be written
    try
    {
    	input.setSelection(new Cursor(input, sp(1, 1, fr(1, 4), 0), true));
      input.write(new ChordData(new Note(new Pitch(2, 0, 4)), fr(2, 4)));
    }
    catch (Exception ex)
    {
      fail();
    }
    //check elements
    Voice voice = score.getStaff(1).getMeasures().get(1).getVoices().get(0);
    RAList<MusicElement> elements = voice.getElements();
    assertEquals(5, elements.size());
    assertTrue(elements.get(0) instanceof Clef);
    assertTrue(elements.get(1) instanceof NormalTime);
    assertTrue(elements.get(2) instanceof Rest);
    assertEquals(fr(1, 4), elements.get(2).getDuration());
    assertTrue(elements.get(3) instanceof Chord);
    assertEquals(2, ((Chord) elements.get(3)).getNotes()[0].getPitch().getStep());
    assertTrue(elements.get(4) instanceof Rest);
    assertEquals(fr(1, 4), elements.get(4).getDuration());
  }
  
  
  /**
   * Tests the write(MusicElement) method.
   * Must fail because of invalid cursor position.
   */
  @Test public void writeTest3()
  {
    //create test score with two half notes
    Score score = createTestScoreHalfHalf();
    ScoreInput input = new ScoreInput(score);
    //write at invalid position
    try
    {
    	input.setSelection(new Cursor(input, sp(1, 5, fr(1, 4), 0), true));
      input.write(new RestData(fr(1, 4)));
      fail();
    }
    catch (InvalidScorePositionException ex)
    {
    }
    catch (Exception ex)
    {
      fail("wrong exception");
    }
  }
  
  
  /**
   * Tests the write(MusicElement) method.
   * Must fail because new element is too long.
   */
  @Test public void writeTest4()
  {
    //create test score with two half notes
    Score score = createTestScoreHalfHalf();
    ScoreInput input = new ScoreInput(score);
    //write too long rest
    try
    {
      input.setSelection(new Cursor(input, sp(1, 1, fr(1, 4), 0), true));
      input.write(new RestData(fr(4, 4)));
      fail();
    }
    catch (MeasureFullException ex)
    {
    }
    catch (Exception ex)
    {
      fail("wrong exception");
    }
  }
  
  
  /**
   * Tests the write(MusicElement) method.
   * Overwrite note with short rests.
   */
  @Test public void writeTest5()
  {
    //create test score with two half notes
    Score score = createTestScoreHalfHalf();
    ScoreInput input = new ScoreInput(score);
    //write a 16th rest (and implicitly a quarter rest) instead of first half note
    try
    {
      input.setSelection(new Cursor(input, sp(1, 1, fr(0, 4), 0), true));
      input.write(new RestData(fr(1, 16)));
      input.write(new RestData(fr(1, 16)));
      input.write(new RestData(fr(1, 16)));
      input.write(new RestData(fr(1, 16)));
    }
    catch (Exception ex)
    {
      fail();
    }
    //check elements
    Voice voice = score.getStaff(1).getMeasures().get(1).getVoices().get(0);
    RAList<MusicElement> elements = voice.getElements();
    assertEquals(8, elements.size());
    assertTrue(elements.get(0) instanceof Clef);
    assertTrue(elements.get(1) instanceof NormalTime);
    assertTrue(elements.get(2) instanceof Rest);
    assertEquals(fr(1, 16), elements.get(2).getDuration());
    assertTrue(elements.get(3) instanceof Rest);
    assertEquals(fr(1, 16), elements.get(3).getDuration());
    assertTrue(elements.get(4) instanceof Rest);
    assertEquals(fr(1, 16), elements.get(4).getDuration());
    assertTrue(elements.get(5) instanceof Rest);
    assertEquals(fr(1, 16), elements.get(5).getDuration());
    assertTrue(elements.get(6) instanceof Rest);
    assertEquals(fr(1, 4), elements.get(6).getDuration());
    assertTrue(elements.get(7) instanceof Chord);
    assertEquals(0, ((Chord) elements.get(7)).getNotes()[0].getPitch().getStep());
    assertEquals(fr(1, 2), elements.get(7).getDuration());
  }
  
  
  /**
   * Tests the write(MusicElement) method.
   * Overwrites first two notes and write
   * additional two notes (implicity in a new
   * measure).
   */
  @Test public void writeTest6()
  {
    //create test score with two half notes
    Score score = createTestScoreHalfHalf();
    ScoreInput input = new ScoreInput(score);
    //change first half note from test score
    try
    {
    	input.setSelection(new Cursor(input, sp(1, 1, fr(0, 4), 0), true));
      input.write(new ChordData(new Note(new Pitch(2, 0, 4)), fr(2, 4)));
      input.write(new ChordData(new Note(new Pitch(3, 0, 4)), fr(2, 4)));
      input.write(new ChordData(new Note(new Pitch(4, 0, 4)), fr(2, 4)));
      input.write(new ChordData(new Note(new Pitch(5, 0, 4)), fr(2, 4)));
    }
    catch (Exception ex)
    {
      fail(ex.getMessage());
    }
    //check elements
    //there must be 4 measures now, because the 3rd measure
    //is full and the 4th measure was begun
    assertEquals(4, score.getMeasuresCount());
    //first measure
    Voice voice = score.getStaff(1).getMeasures().get(1).getVoices().get(0);
    RAList<MusicElement> elements = voice.getElements();
    assertEquals(4, elements.size());
    assertTrue(elements.get(0) instanceof Clef);
    assertTrue(elements.get(1) instanceof NormalTime);
    assertTrue(elements.get(2) instanceof Chord);
    assertEquals(2, ((Chord) elements.get(2)).getNotes()[0].getPitch().getStep());
    assertTrue(elements.get(3) instanceof Chord);
    assertEquals(3, ((Chord) elements.get(3)).getNotes()[0].getPitch().getStep());
    //second measure
    voice = score.getStaff(1).getMeasures().get(2).getVoices().get(0);
    elements = voice.getElements();
    assertEquals(2, elements.size());
    assertTrue(elements.get(0) instanceof Chord);
    assertEquals(4, ((Chord) elements.get(0)).getNotes()[0].getPitch().getStep());
    assertTrue(elements.get(1) instanceof Chord);
    assertEquals(5, ((Chord) elements.get(1)).getNotes()[0].getPitch().getStep());
  }
  
  
  /**
   * Creates a simple score with 2 staves and 2 measures.
   * Only the second measure of the second staff is filled:
   * clef, time, half note, half note.
   */
  private Score createTestScoreHalfHalf()
  {
    Score score = new Score();
    score.addPart(0, 2);
    score.addEmptyMeasures(2);
    Measure measure = score.getStaff(1).getMeasures().get(1);
    Voice voice = measure.getVoices().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.G));
    measure.addNoVoiceElement(new NormalTime(4, 4));
    voice.addVoiceElement(new Chord(new ChordData(new Note(new Pitch(0, 0, 4)), fr(2, 4))));
    voice.addVoiceElement(new Chord(new ChordData(new Note(new Pitch(0, 0, 4)), fr(2, 4))));
    return score;
  }
  

}
