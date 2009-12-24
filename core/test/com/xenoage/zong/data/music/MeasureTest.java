package com.xenoage.zong.data.music;

import static com.xenoage.util.math.Fraction.fr;
import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.*;

import com.xenoage.util.SortedList;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.music.key.Key;
import com.xenoage.zong.data.music.key.TraditionalKey;


/**
 * Test cases for the {@link Measure} class.
 * 
 * @author Andreas Wenger
 */
public class MeasureTest
{
	
	
	@Test public void getAccidentalsBeforeBeatTest()
	{
		//create measure with B, Bb, Bb
		Score score = new Score();
    score.addPart(0, 1);
    score.addEmptyMeasures(1);
    Voice voice = score.getVoice(0, 0, 0);
    Key key = new TraditionalKey(-2);
    voice.addNoVoiceElement(key);
    voice.addNote(new Pitch(6, 0, 4), new Fraction(1, 4));
    voice.addNote(new Pitch(6, -1, 4), new Fraction(1, 4));
    voice.addNote(new Pitch(6, -1, 4), new Fraction(1, 4));
  	//accidentals must be natural, flat, none
    Measure measure = score.getMeasure(0, 0);
    //beat 0: no accidentals
    Hashtable<Pitch, Byte> accidentals = measure.getAccidentalsBeforeBeat(new Fraction(0, 4), key);
    assertEquals(0, accidentals.size());
    //beat 1: B4 natural
    accidentals = measure.getAccidentalsBeforeBeat(new Fraction(1, 4), key);
    assertEquals(1, accidentals.size());
    assertEquals(0, (int) accidentals.get(new Pitch(6, 0, 4)));
    //beat 2: B4 flat
    accidentals = measure.getAccidentalsBeforeBeat(new Fraction(2, 4), key);
    assertEquals(1, accidentals.size());
    assertEquals(-1, (int) accidentals.get(new Pitch(6, 0, 4)));
	}
	
	
	@Test public void getUsedBeatsTest()
	{
		//create measure with two voices:
		// 1/4      1/4      1/8    1/8
		// 1/6   1/6   1/6   1/4
		Measure measure = new Measure(null);
		Voice voice = measure.getVoices().get(0);
		voice.addElement(new Rest(new RestData(fr(1, 4))));
		voice.addElement(new Rest(new RestData(fr(1, 4))));
		voice.addElement(new Rest(new RestData(fr(1, 8))));
		voice.addElement(new Rest(new RestData(fr(1, 8))));
		voice = measure.addVoice();
		voice.addElement(new Rest(new RestData(fr(1, 6))));
		voice.addElement(new Rest(new RestData(fr(1, 6))));
		voice.addElement(new Rest(new RestData(fr(1, 6))));
		voice.addElement(new Rest(new RestData(fr(1, 4))));
		//check beats
		SortedList<Fraction> beats = measure.getUsedBeats();
		assertEquals(6, beats.getSize());
		assertEquals(fr(0, 4), beats.get(0));
		assertEquals(fr(1, 6), beats.get(1));
		assertEquals(fr(1, 4), beats.get(2));
		assertEquals(fr(2, 6), beats.get(3));
		assertEquals(fr(2, 4), beats.get(4));
		assertEquals(fr(5, 8), beats.get(5));
	}
	

}
