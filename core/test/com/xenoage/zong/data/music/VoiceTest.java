package com.xenoage.zong.data.music;

import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.data.ScorePosition.sp;
import static org.junit.Assert.*;

import java.util.List;

import com.xenoage.util.SortedList;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.*;
import com.xenoage.zong.data.music.time.NormalTime;

import org.junit.Test;


/**
 * Test cases for a Voice.
 *
 * @author Andreas Wenger
 */
public class VoiceTest
{
  
  @Test public void testIsBeatUsed()
  {
    //create a voice using a 4/4 time signature
    Voice voice = createVoice1();
    //although it is empty, the start beat is always used
    assertTrue(voice.isBeatUsed(new Fraction(0)));
    //beat 1 and 2 is not used yet
    assertFalse(voice.isBeatUsed(new Fraction(1, 4)));
    assertFalse(voice.isBeatUsed(new Fraction(2, 4)));
    //add two quarter rests to the voice
    voice.addVoiceElement(new Rest(new RestData(new Fraction(1, 4))));
    voice.addVoiceElement(new Rest(new RestData(new Fraction(1, 4))));
    //beat 1 and 2 is used now
    assertTrue(voice.isBeatUsed(new Fraction(1, 4)));
    assertTrue(voice.isBeatUsed(new Fraction(2, 4)));
  }
  
  
  @Test public void getUsedBeatsTest()
  {
    //create a voice using a 4/4 time signature
    Voice voice = createVoice1();
    //check the beats
    SortedList<Fraction> beats = voice.getUsedBeats();
    assertEquals(1, beats.getSize());
    assertEquals(Fraction._0, beats.get(0));
    //add two quarter rests to the voice
    voice.addVoiceElement(new Rest(new RestData(new Fraction(1, 4))));
    voice.addVoiceElement(new Rest(new RestData(new Fraction(1, 4))));
    //check the beats again
    beats = voice.getUsedBeats();
    assertEquals(2, beats.getSize());
    assertEquals(Fraction._0, beats.get(0));
    assertEquals(fr(1, 4), beats.get(1));
  }
  
  
  @Test public void getElementsInRangeTest()
  {
  	//create rests we later need for verifying
  	Rest[] rests = new Rest[12];
  	for (int i = 0; i < 12; i++)
  		rests[i] = new Rest(new RestData(fr(1, 4)));
  	Score score = createScore2(rests);
  	//test in measure 1
  	Voice voice = score.getStaff(0).getMeasures().get(1).getVoices().get(0);
  	//test with start measure before measure 1 and end measure behind measure 1
  	List<MusicElement> ret = voice.getElementsInRange(sp(0, 0, fr(3, 4), -1), sp(0, 2, fr(1, 4), -1));
  	assertEquals(4, ret.size());
  	assertEquals(rests[4], ret.get(0));
  	assertEquals(rests[5], ret.get(1));
  	assertEquals(rests[6], ret.get(2));
  	assertEquals(rests[7], ret.get(3));
  	//test with start measure before measure 1 and end at beat 2/4
  	ret = voice.getElementsInRange(sp(0, 0, fr(3, 4), -1), sp(0, 1, fr(2, 4), -1));
  	assertEquals(2, ret.size());
  	assertEquals(rests[4], ret.get(0));
  	assertEquals(rests[5], ret.get(1));
  	//test with start at beat 1/4 and end measure behind measure 1
  	ret = voice.getElementsInRange(sp(0, 1, fr(1, 4), -1), sp(0, 2, fr(2, 4), -1));
  	assertEquals(3, ret.size());
  	assertEquals(rests[5], ret.get(0));
  	assertEquals(rests[6], ret.get(1));
  	assertEquals(rests[7], ret.get(2));
  	//test with start at beat 1/4 and end at beat 3/4
  	ret = voice.getElementsInRange(sp(0, 1, fr(1, 4), -1), sp(0, 1, fr(3, 4), -1));
  	assertEquals(2, ret.size());
  	assertEquals(rests[5], ret.get(0));
  	assertEquals(rests[6], ret.get(1));
  }


  /**
   * Creates and returns a score with a
   * voice using a 4/4 time signature.
   */
  private Score createScore1()
  {
    Score score = new Score();
    score.addPart(0, 1);
    score.addEmptyMeasures(1);
    Measure measure = score.getStaff(0).getMeasures().get(0);
    measure.addNoVoiceElement(new NormalTime(4, 4));
    return score;
  }
  
  
  /**
   * Creates and returns a single voice
   * using a 4/4 time signature.
   */
  private Voice createVoice1()
  {
  	return createScore1().getStaff(0).getMeasures().get(0).getVoices().get(0);
  }
  
  
  /**
   * Creates and returns a score using a 4/4 time signature
   * with three measures and a voice containing the given twelve
   * quarter rests.
   */
  private Score createScore2(Rest[] rests)
  {
    Score score = new Score();
    score.addPart(0, 1);
    score.addEmptyMeasures(3);
    Measure measure = score.getStaff(0).getMeasures().get(0);
    measure.addNoVoiceElement(new NormalTime(4, 4));
    for (int i = 0; i < 4; i++)
    	measure.getVoices().get(0).addVoiceElement(rests[i]);
    measure = score.getStaff(0).getMeasures().get(1);
    for (int i = 4; i < 8; i++)
    	measure.getVoices().get(0).addVoiceElement(rests[i]);
    measure = score.getStaff(0).getMeasures().get(2);
    for (int i = 8; i < 12; i++)
    	measure.getVoices().get(0).addVoiceElement(rests[i]);
    return score;
  }


}
