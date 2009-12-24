package com.xenoage.zong.io.score.selections;

import static com.xenoage.zong.data.ScorePosition.sp;
import static org.junit.Assert.*;

import com.xenoage.util.RAList;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.music.*;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.time.NormalTime;
import com.xenoage.zong.io.score.ScoreInputOptions;
import com.xenoage.zong.io.score.ViewerScoreInput;
import com.xenoage.zong.io.score.ScoreInputOptions.WriteMode;

import org.junit.Test;


/**
 * Test cases for a {@link PitchCursor}.
 *
 * @author Andreas Wenger
 */
public class PitchCursorTest
{
  
  
  /**
   * Tests the write(Pitch[]) method with
   * WriteMode == ChordBeforeCursor.
   */
  @Test public void writePitchesChordBeforeCursorTest1()
  {
    //create test score with 2 half notes and 4 quarter notes
    Score score = createTestScore2Half4Quarter();
    ViewerScoreInput input = new ViewerScoreInput(score, new ScoreInputOptions());
    //set write mode to ChordBeforeCursor
    input.getOptions().setWriteMode(WriteMode.ChordBeforeCursor);
    //set cursor to the end of the first measure and add
    //a F4. This must result in a D4/F4 chord.
    try
    {
      input.setSelection(new PitchCursor(input, sp(1, 0, new Fraction(4, 4), 0), true));
      input.write(new Pitch[]{new Pitch(3, 0, 4)});
    }
    catch (Exception ex)
    {
      fail();
    }
    Voice voice = score.getStaff(1).getMeasures().get(0).getVoices().get(0);
    RAList<MusicElement> elements = voice.getElements();
    assertEquals(4, elements.size());
    assertTrue(elements.get(0) instanceof Clef);
    assertTrue(elements.get(1) instanceof NormalTime);
    assertTrue(elements.get(2) instanceof Chord);
    assertEquals(0, ((Chord) elements.get(2)).getNotes()[0].getPitch().getStep());
    assertTrue(elements.get(3) instanceof Chord);
    assertEquals(2, ((Chord) elements.get(3)).getNotes().length);
    assertEquals(1, ((Chord) elements.get(3)).getNotes()[0].getPitch().getStep());
    assertEquals(3, ((Chord) elements.get(3)).getNotes()[1].getPitch().getStep());
    //set cursor to the beginning of the second measure and add
    //a A3. This must result in a A3/D4/F4 chord at the end
    //of the first measure.
    try
    {
    	input.setSelection(new PitchCursor(input, sp(1, 1, new Fraction(0, 4), 0), true));
      input.write(new Pitch[]{new Pitch(5, 0, 3)});
    }
    catch (Exception ex)
    {
      fail();
    }
    voice = score.getStaff(1).getMeasures().get(0).getVoices().get(0);
    elements = voice.getElements();
    assertEquals(4, elements.size());
    assertTrue(elements.get(0) instanceof Clef);
    assertTrue(elements.get(1) instanceof NormalTime);
    assertTrue(elements.get(2) instanceof Chord);
    assertEquals(0, ((Chord) elements.get(2)).getNotes()[0].getPitch().getStep());
    assertTrue(elements.get(3) instanceof Chord);
    assertEquals(3, ((Chord) elements.get(3)).getNotes().length);
    assertEquals(5, ((Chord) elements.get(3)).getNotes()[0].getPitch().getStep());
    assertEquals(1, ((Chord) elements.get(3)).getNotes()[1].getPitch().getStep());
    assertEquals(3, ((Chord) elements.get(3)).getNotes()[2].getPitch().getStep());
  }
  
  
  /**
   * Creates a simple score with 2 staves and 2 measures.
   * Both measures of the second staff are filled:
   * clef, time, 2x half | 4x quarter.
   */
  private Score createTestScore2Half4Quarter()
  {
  	Score score = new Score();
    score.addPart(0, 2);
    score.addEmptyMeasures(2);
    Measure measure = score.getStaff(1).getMeasures().get(0);
    Voice voice = measure.getVoices().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.G));
    measure.addNoVoiceElement(new NormalTime(4, 4));
    voice.addVoiceElement(new Chord(new ChordData(new Note(new Pitch(0, 0, 4)), new Fraction(2, 4))));
    voice.addVoiceElement(new Chord(new ChordData(new Note(new Pitch(1, 0, 4)), new Fraction(2, 4))));
    voice = score.getStaff(1).getMeasures().get(1).getVoices().get(0);
    voice.addVoiceElement(new Chord(new ChordData(new Note(new Pitch(3, 0, 4)), new Fraction(1, 4))));
    voice.addVoiceElement(new Chord(new ChordData(new Note(new Pitch(4, 0, 4)), new Fraction(1, 4))));
    voice.addVoiceElement(new Chord(new ChordData(new Note(new Pitch(5, 0, 4)), new Fraction(1, 4))));
    voice.addVoiceElement(new Chord(new ChordData(new Note(new Pitch(5, 0, 4)), new Fraction(1, 4))));
    return score;
  }

}
