package com.xenoage.zong.io.midi.out;

import static org.junit.Assert.*;

import com.xenoage.zong.data.music.Pitch;

import org.junit.Test;


/**
 * Test cases for a Pitch class.
 *
 * @author Andreas Wenger
 */
public class MidiToolsTest
{
  
  private int[] midi;
  private Pitch[] pitch;
  
  
  /**
   * Tests the getPitchFromNoteNumber-method.
   */
  @Test public void getPitchFromNoteNumber()
  {
    createMidiTestPitches();
    for (int i = 0; i < midi.length; i++)
    {
      Pitch p = MidiTools.getPitchFromNoteNumber(midi[i]);
      assertEquals(pitch[i], p);
    }
  }
  
  
  /**
   * Tests the getNoteNumberFromPitch-method.
   */
  @Test public void getNoteNumberFromPitch()
  {
    createMidiTestPitches();
    for (int i = 0; i < midi.length; i++)
    {
      assertEquals(midi[i], MidiTools.getNoteNumberFromPitch(pitch[i]));
    }
  }
  
  
  private void createMidiTestPitches()
  {
    midi = new int[5];
    pitch = new Pitch[5];
    //C8
    midi[0] = 108;
    pitch[0] = new Pitch(0, 0, 8);
    //F#6
    midi[1] = 90;
    pitch[1] = new Pitch(3, 1, 6);
    //C4
    midi[2] = 60;
    pitch[2] = new Pitch(0, 0, 4);
    //Eb2
    midi[3] = 39;
    pitch[3] = new Pitch(1, 1, 2);
    //A0
    midi[4] = 21;
    pitch[4] = new Pitch(5, 0, 0);
  }

}
