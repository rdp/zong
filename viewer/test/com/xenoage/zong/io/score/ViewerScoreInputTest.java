package com.xenoage.zong.io.score;

import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.data.ScorePosition.sp;
import static org.junit.Assert.*;

import com.xenoage.util.RAList;

import com.xenoage.zong.data.*;
import com.xenoage.zong.data.music.*;
import com.xenoage.zong.data.music.time.NormalTime;
import com.xenoage.zong.io.score.ScoreInputOptions;
import com.xenoage.zong.io.score.selections.PitchCursor;

import org.junit.Test;


/**
 * Test cases for a ScoreInput class.
 *
 * @author Andreas Wenger
 */
public class ViewerScoreInputTest
{
  
  
  /**
   * Tests write(Pitch) and write(Pitch[]) by creating
   * a small melody.
   */
  @Test public void writePitches()
  {
    Score score = createEmptyScore();
    ViewerScoreInput input = new ViewerScoreInput(score, new ScoreInputOptions());
    ScoreInputOptions options = input.getOptions();
    //write melody ("Alle meine Entchen")
    try
    {
      input.setSelection(new PitchCursor(input, sp(1, 0, fr(0, 1), 0), true));
      input.write(new NormalTime(4, 4));
      options.setDuration(fr(1, 4));
      input.write(new Pitch(0, 0, 4));
      input.write(new Pitch(1, 0, 4));
      input.write(new Pitch(2, 0, 4));
      input.write(new Pitch(3, 0, 4));
      options.setDuration(fr(1, 2));
      input.write(new Pitch(4, 0, 4));
      input.write(new Pitch(4, 0, 4));
      options.setDuration(fr(1, 4));
      input.write(new Pitch[]{
        new Pitch(0, 0, 4), new Pitch(3, 0, 4), new Pitch(5, 0, 4)});
      input.write(new Pitch(5, 0, 4));
      input.write(new Pitch(5, 0, 4));
      input.write(new Pitch(5, 0, 4));
      options.setDuration(fr(1, 1));
      input.write(new Pitch[]{
        new Pitch(0, 0, 4), new Pitch(2, 0, 4), new Pitch(4, 0, 4)});
    }
    catch (Exception ex)
    {
      fail(ex.toString());
    }
    //check if melody was written correctly
    Staff staff = score.getStaff(0, 1);
    //measure 1
    Voice voice = staff.getMeasures().get(0).getVoices().get(0);
    RAList<MusicElement> el = voice.getElements();
    assertEquals(5, el.size());
    assertTrue(el.get(0) instanceof NormalTime);
    for (int i = 0; i < 4; i++)
    {
	    assertTrue(el.get(i+1) instanceof Chord);
	    assertEquals(fr(1, 4), el.get(i+1).getDuration());
    }
    assertEquals(new Pitch(0, 0, 4), ((Chord) el.get(1)).getNotes()[0].getPitch());
    //measure 2
    voice = staff.getMeasures().get(1).getVoices().get(0);
    el = voice.getElements();
    assertEquals(2, el.size());
    //measure 3
    voice = staff.getMeasures().get(2).getVoices().get(0);
    el = voice.getElements();
    assertEquals(4, el.size());
    //measure 4
    voice = staff.getMeasures().get(3).getVoices().get(0);
    el = voice.getElements();
    assertEquals(1, el.size());
    assertTrue(el.get(0) instanceof Chord);
    assertEquals(fr(1, 1), el.get(0).getDuration());
    Chord c = (Chord) el.get(0);
    assertEquals(3, c.getNotes().length);
    assertEquals(new Pitch(0, 0, 4), c.getNotes()[0].getPitch());
    assertEquals(new Pitch(2, 0, 4), c.getNotes()[1].getPitch());
    assertEquals(new Pitch(4, 0, 4), c.getNotes()[2].getPitch());
  }
  
  
  /**
   * Creates a simple score with 2 staves and 4 measures.
   */
  private Score createEmptyScore()
  {
  	Score score = new Score();
    score.addPart(0, 2);
    score.addEmptyMeasures(3);
    return score;
  }

}
