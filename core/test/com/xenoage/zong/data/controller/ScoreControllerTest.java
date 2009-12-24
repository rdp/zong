package com.xenoage.zong.data.controller;

import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.data.ScorePosition.sp;
import static com.xenoage.zong.data.music.Pitch.pi;

import java.util.ArrayList;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.SortedList;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.ChordData;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.MusicContext;
import com.xenoage.zong.data.music.NoVoiceElement;
import com.xenoage.zong.data.music.Note;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.Rest;
import com.xenoage.zong.data.music.RestData;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.VoiceElement;
import com.xenoage.zong.data.music.barline.Barline;
import com.xenoage.zong.data.music.barline.BarlineStyle;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.key.Key;
import com.xenoage.zong.data.music.key.TraditionalKey;
import com.xenoage.zong.data.music.time.NormalTime;
import com.xenoage.zong.data.music.time.SenzaMisura;
import com.xenoage.zong.data.music.time.Time;
import com.xenoage.zong.data.music.util.Endpoint;
import com.xenoage.zong.util.demo.ScoreDemo;
import com.xenoage.zong.util.exceptions.InvalidScorePositionException;


/**
 * Test cases for the {@link ScoreController} class.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class ScoreControllerTest
{
	
	
	@Test public void getMusicContextAtTest()
		throws InvalidScorePositionException
  {
		Clef scoreClef = new Clef(ClefType.G);
		Key scoreKey = new TraditionalKey(-3);
		Score score = createScore1(scoreClef, scoreKey);
		ScoreController ctrl = score.getController();
    //at the beginning of measure 1: G clef, Eb major, no accidentals
    MusicContext mc = ctrl.getMusicContextAt(new ScorePosition(0, 0, Fraction._0, 0));
    assertEquals(scoreClef, mc.getClef());
    assertEquals(scoreKey, mc.getKey());
    assertEquals(0, mc.getAccidentals().size());
    //the same for the beginning of measure 2
    mc = ctrl.getMusicContextAt(new ScorePosition(0, 1, Fraction._0, 0));
    assertEquals(scoreClef, mc.getClef());
    assertEquals(scoreKey, mc.getKey());
    assertEquals(0, mc.getAccidentals().size());
    //measure 2, beat 4: C#5
    mc = ctrl.getMusicContextAt(new ScorePosition(0, 1, new Fraction(1), 0));
    assertEquals(scoreClef, mc.getClef());
    assertEquals(scoreKey, mc.getKey());
    assertEquals(1, mc.getAccidentals().size());
    Pitch acc = mc.getAccidentals().keys().nextElement();
    assertEquals(0, acc.getStep());
    assertEquals(5, acc.getOctave());
    assertEquals(1, (int) mc.getAccidentals().get(acc));
    //measure 3, beat 1: B(nat)4
    mc = ctrl.getMusicContextAt(new ScorePosition(0, 2, new Fraction(1), 0));
    assertEquals(scoreClef, mc.getClef());
    assertEquals(scoreKey, mc.getKey());
    assertEquals(1, mc.getAccidentals().size());
    acc = mc.getAccidentals().keys().nextElement();
    assertEquals(6, acc.getStep());
    assertEquals(4, acc.getOctave());
    assertEquals(0, (int) mc.getAccidentals().get(acc));
    //measure 4: like measure 1
    mc = ctrl.getMusicContextAt(new ScorePosition(0, 3, Fraction._0, 0));
    assertEquals(scoreClef, mc.getClef());
    assertEquals(scoreKey, mc.getKey());
    assertEquals(0, mc.getAccidentals().size());
  }
	
	
	@Test public void getMusicContextBeforeTest()
		throws InvalidScorePositionException
	{
		Clef scoreClef = new Clef(ClefType.G);
		Key scoreKey = new TraditionalKey(-3);
		Score score = createScore1(scoreClef, scoreKey);
		ScoreController ctrl = score.getController();
	  //before the beginning of measure 1: (default) G clef, C major, no accidentals
	  MusicContext mc = ctrl.getMusicContextBefore(new ScorePosition(0, 0, Fraction._0, 0));
	  assertEquals(ClefType.G, mc.getClef().getType());
	  assertEquals(0, ((TraditionalKey)mc.getKey()).getFifth());
	  assertEquals(0, mc.getAccidentals().size());
	  //before the beginning of measure 2: G clef, Eb major, no accidentals
	  mc = ctrl.getMusicContextBefore(new ScorePosition(0, 1, Fraction._0, 0));
	  assertEquals(scoreClef, mc.getClef());
	  assertEquals(scoreKey, mc.getKey());
	  assertEquals(0, mc.getAccidentals().size());
	  //before measure 2, beat 2: no accidental
	  mc = ctrl.getMusicContextBefore(new ScorePosition(0, 1, new Fraction(2, 4), 0));
	  assertEquals(0, mc.getAccidentals().size());
	  //before measure 2, beat 3: C#5
	  mc = ctrl.getMusicContextBefore(new ScorePosition(0, 1, new Fraction(3, 4), 0));
	  assertEquals(1, mc.getAccidentals().size());
	}
	
	
	@Test public void testHasSameNoVoiceElementType()
  {
		ScoreController ctrl = new ScoreController(null);
  	assertTrue(ctrl.hasSameNoVoiceElementType(new Clef(ClefType.F), new Clef(ClefType.G)));
  	assertTrue(ctrl.hasSameNoVoiceElementType(new NormalTime(3, 4), new SenzaMisura()));
  	assertTrue(ctrl.hasSameNoVoiceElementType(new TraditionalKey(2), new TraditionalKey(-3)));
  	assertTrue(ctrl.hasSameNoVoiceElementType(
  		Barline.createBarline(BarlineStyle.Dotted), Barline.createBarline(BarlineStyle.Dashed)));
  	assertFalse(ctrl.hasSameNoVoiceElementType(
  		Barline.createBarline(BarlineStyle.Dotted), new SenzaMisura()));
  }
	
	
	@Test public void testAddNoVoiceElement()
  {
  	//create a voice
    Score score = createScore2();
    ScoreController ctrl = score.getController();
    Voice voice = score.getVoice(0, 0, 0);
    //add F clef at the beginning
    Clef clef = new Clef(ClefType.F);
    ctrl.writeAt(clef, new ScorePosition(0, 0, new Fraction(0), 0));
    assertEquals(1, voice.getElements().size());
    assertEquals(clef, voice.getElements().get(0));
    //replace it by G clef
    clef = new Clef(ClefType.G);
    ctrl.writeAt(clef, new ScorePosition(0, 0, new Fraction(0), 0));
    assertEquals(1, voice.getElements().size());
    assertEquals(clef, voice.getElements().get(0));
    //insert traditional key at 1/4
    Key key = new TraditionalKey(2);
    ctrl.writeAt(key, new ScorePosition(0, 0, new Fraction(1, 4), 0));
    assertEquals(3, voice.getElements().size());
    assertEquals(clef, voice.getElements().get(0));
    assertTrue(voice.getElements().get(1) instanceof Rest);
    assertEquals(new Fraction(1, 4), ((Rest)voice.getElements().get(1)).getDuration());
    assertEquals(key, voice.getElements().get(2));
    //insert clef at 1/8, splitting the rest
    Clef clef2 = new Clef(ClefType.F);
    ctrl.writeAt(clef2, new ScorePosition(0, 0, new Fraction(1, 8), 0));
    assertEquals(5, voice.getElements().size());
    assertEquals(clef, voice.getElements().get(0));
    assertTrue(voice.getElements().get(1) instanceof Rest);
    assertEquals(clef2, voice.getElements().get(2));
    assertTrue(voice.getElements().get(3) instanceof Rest);
    assertEquals(key, voice.getElements().get(4));
  }
	
	@Test public void getClefBeforeTest()
	{
		//create simple test measure
		//with clef-g, 1/4, clef-f, 1/4, clef-g, 1/4
		Score score = new Score();
    score.addPart(0, 1);
    score.addEmptyMeasures(1);
    Voice voice = score.getVoice(0, 0, 0);
    Clef clef1 = new Clef(ClefType.G);
    voice.addNoVoiceElement(clef1);
    voice.addNote(new Pitch(1, 0, 4), fr(1, 4));
    Clef clef2 = new Clef(ClefType.G);
    voice.addNoVoiceElement(clef2);
    voice.addNote(new Pitch(2, 0, 4), fr(1, 4));
    Clef clef3 = new Clef(ClefType.G);
    voice.addNoVoiceElement(clef3);
    voice.addNote(new Pitch(3, 0, 4), fr(1, 4));
    //test method
    ScoreController ctrl = score.getController();
    assertEquals(clef1, ctrl.getClef(sp(0, 0, fr(0, 4), 0), Endpoint.AtOrBefore));
    assertEquals(clef1, ctrl.getClef(sp(0, 0, fr(1, 4), 0), Endpoint.Before));
    assertEquals(clef2, ctrl.getClef(sp(0, 0, fr(1, 4), 0), Endpoint.AtOrBefore));
    assertEquals(clef2, ctrl.getClef(sp(0, 0, fr(2, 4), 0), Endpoint.Before));
    assertEquals(clef3, ctrl.getClef(sp(0, 0, fr(2, 4), 0), Endpoint.AtOrBefore));
    assertEquals(clef3, ctrl.getClef(sp(0, 0, fr(3, 4), 0), Endpoint.Before));
	}
	
	
	/**
	 * When writing a {@link NoVoiceElement} in the middle of a
	 * measure, this may not affect
	 * the preceding and following {@link VoiceElement}s.
	 */
	@Test public void testAddNoVoiceElement2()
	{
		Score score = createScore1(new Clef(ClefType.G), new TraditionalKey(5));
		ScoreController ctrl = score.getController();
		ScorePosition posBefore = new ScorePosition(0, 0, Fraction._0, 0);
		ScorePosition posAfter = new ScorePosition(0, 0, fr(1, 4), 0);
		VoiceElement eBefore = ctrl.getVoiceElementAt(posBefore).getElement();
		VoiceElement eAfter = ctrl.getVoiceElementAt(posAfter).getElement();
		ctrl.writeAt(new Clef(ClefType.F), posAfter);
		assertEquals(eBefore, ctrl.getVoiceElementAt(posBefore).getElement());
		assertEquals(eAfter, ctrl.getVoiceElementAt(posAfter).getElement());
	}
	
	
	/**
	 * When writing a {@link NoVoiceElement}, at the beginning of a
	 * measure, this may not affect the following {@link VoiceElement}.
	 */
	@Test public void testAddNoVoiceElement3()
	{
		Score score = createScore1(new Clef(ClefType.G), new TraditionalKey(5));
		ScoreController ctrl = score.getController();
		ScorePosition posAfter = new ScorePosition(0, 0, Fraction._0, 0);
		VoiceElement eAfter = ctrl.getVoiceElementAt(posAfter).getElement();
		ctrl.writeAt(new Clef(ClefType.F), posAfter);
		assertEquals(eAfter, ctrl.getVoiceElementAt(posAfter).getElement());
	}
	
	
	/**
	 * Remove a {@link NoVoiceElement} of a given type.
	 */
	@Test public void testRemoveNoVoiceElementsAt()
	{
		Score score = createScore1(new Clef(ClefType.G), new TraditionalKey(-2));
		//remove the key
		score.getController().removeNoVoiceElementsAt(sp(0, 0, Fraction._0, 0), Key.class);
		//check the elements
		Voice voice = score.getVoice(0, 0, 0);
		assertEquals(5, voice.getElements().size());
		assertTrue(voice.getElements().get(0) instanceof Clef);
		assertTrue(voice.getElements().get(1) instanceof Time);
		assertTrue(voice.getElements().get(2) instanceof Chord);
		assertTrue(voice.getElements().get(3) instanceof Chord);
		assertTrue(voice.getElements().get(4) instanceof Chord);
	}
	
	
	/**
	 * Tests the getTimeAtOrBefore-method.
	 */
	@Test public void testGetTimeAtOrBefore()
	{
		Score score = createScoreTime();
		ScoreController ctrl = score.getController();
		assertEquals(new SenzaMisura(), ctrl.getTimeAtOrBefore(0, 0));
		assertEquals(new NormalTime(3, 4), ctrl.getTimeAtOrBefore(0, 1));
		assertEquals(new NormalTime(4, 4), ctrl.getTimeAtOrBefore(0, 2));
		assertEquals(new NormalTime(5, 4), ctrl.getTimeAtOrBefore(0, 3));
	}
	
	
	/**
	 * Tests the getMeasureBeats-method.
	 */
	@Test public void testGetMeasureBeats()
	{
		Score score = createScoreTime();
		ScoreController ctrl = score.getController();
		assertEquals(new Fraction(3, 8), ctrl.getMeasureBeats(0));
		assertEquals(new Fraction(3, 4), ctrl.getMeasureBeats(1));
		assertEquals(new Fraction(4, 4), ctrl.getMeasureBeats(2));
		assertEquals(new Fraction(5, 4), ctrl.getMeasureBeats(3));
	}
  
  
  private Score createScore1(Clef scoreClef, Key scoreKey)
  {
    Score score = new Score();
    score.addPart(0, 2);
    Staff staff = score.getStaff(0);
    score.addEmptyMeasures(4);
    //measure 1
    Measure measure = staff.getMeasures().get(0); 
    measure.addNoVoiceElement(scoreClef);
    measure.addNoVoiceElement(scoreKey);
    measure.addNoVoiceElement(new NormalTime(3, 4));
    Voice voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(2, -1, 4), new Fraction(1, 4));
    voice.addNote(new Pitch(3, 0, 4), new Fraction(1, 4));
    voice.addNote(new Pitch(4, 0, 4), new Fraction(1, 4));
    //measure 2
    measure = staff.getMeasures().get(1);
    voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(5, -1, 4), new Fraction(2, 4));
    voice.addNote(new Pitch(0, 1, 5), new Fraction(1, 4)); //C#5
    //measure 3
    measure = staff.getMeasures().get(2);
    voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(6, 0, 4), new Fraction(1, 8)); //B(nat)4
    voice.addNote(new Pitch(5, -1, 4), new Fraction(1, 8));
    voice.addNote(new Pitch(4, 0, 4), new Fraction(1, 4));
    voice.addNote(new Pitch(3, 0, 5), new Fraction(1, 4));
    //measure 4
    measure = staff.getMeasures().get(3);
    voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(2, -1, 5), new Fraction(2, 4));
    return score;
  }
  
  
  /**
   * Creates and returns a score with a single voice.
   */
  private Score createScore2()
  {
    Score score = new Score();
    score.addPart(0, 1);
    score.addEmptyMeasures(1);
  	return score;
  }
  
  
  @Test public void getUsedBeatsTest()
  {
  	Score score = ScoreDemo.createDemoScore(4);
  	ArrayList<SortedList<Fraction>> beats = score.getController().getUsedBeats();
  	//measure 0
  	SortedList<Fraction> measure0 = beats.get(0);
  	assertEquals(7, measure0.getSize());
  	assertEquals(fr(0, 8), measure0.get(0));
  	assertEquals(fr(1, 8), measure0.get(1));
  	assertEquals(fr(2, 8), measure0.get(2));
  	assertEquals(fr(3, 8), measure0.get(3));
  	assertEquals(fr(7, 16), measure0.get(4));
  	assertEquals(fr(4, 8), measure0.get(5));
  	assertEquals(fr(5, 8), measure0.get(6));
  	//measure 2
  	SortedList<Fraction> measure2 = beats.get(2);
  	assertEquals(6, measure2.getSize());
  	assertEquals(fr(0, 8), measure2.get(0));
  	assertEquals(fr(1, 8), measure2.get(1));
  	assertEquals(fr(2, 8), measure2.get(2));
  	assertEquals(fr(5, 12), measure2.get(3));
  	assertEquals(fr(4, 8), measure2.get(4));
  	assertEquals(fr(7, 12), measure2.get(5));
  }
  
  
  private Score createScoreTime()
  {
    Score score = new Score();
    score.addPart(0, 1);
    Staff staff = score.getStaff(0);
    score.addEmptyMeasures(4);
    //measure 1
    //no time signature here, but a voice with 1 quarter and a voice with 3 eighth
    Measure measure = staff.getMeasures().get(0); 
    measure.getVoices().get(0).addElement(new Chord(new ChordData(new Note(pi(0, 0, 4)), new Fraction(1, 4))));
    measure.addVoice();
    measure.getVoices().get(1).addElement(new Chord(new ChordData(new Note(pi(0, 0, 4)), new Fraction(1, 8))));
    measure.getVoices().get(1).addElement(new Chord(new ChordData(new Note(pi(0, 0, 4)), new Fraction(1, 8))));
    measure.getVoices().get(1).addElement(new Rest(new RestData(new Fraction(1, 8))));
    //measure 2
    measure = staff.getMeasures().get(1); 
    measure.addNoVoiceElement(new NormalTime(3, 4));
    //measure 3
    measure = staff.getMeasures().get(2);
    measure.addNoVoiceElement(new NormalTime(4, 4));
    //measure 4
    measure = staff.getMeasures().get(3);
    measure.addNoVoiceElement(new NormalTime(5, 4));
    return score;
  }
  
  
  @Test public void clipToMeasureTest()
  {
  	Score score = ScoreDemo.createDemoScore32Measures();
  	ScoreController controller = score.getController();
  	
  	
  	//Position before measure
  	ScorePosition pos = new ScorePosition(1,1,new Fraction(2,4),0);
  	ScorePosition clippedPos = controller.clipToMeasure(2, pos);
  	assertEquals(clippedPos.getMeasure(), 2);
  	assertTrue(clippedPos.getBeat().equals(Fraction._0));
  	
  	
  	//Position at the beginning of the measure
  	pos = new ScorePosition(1,2,Fraction._0,0);
  	clippedPos = controller.clipToMeasure(2, pos);
  	assertEquals(clippedPos.getMeasure(), 2);
  	assertTrue(clippedPos.getBeat().equals(Fraction._0));
  	
  	
  	//Position in the measure
  	pos = new ScorePosition(1,2,new Fraction(2,4),0);
  	clippedPos = controller.clipToMeasure(2, pos);
  	assertEquals(clippedPos.getMeasure(), 2);
  	assertTrue(clippedPos.getBeat().equals(new Fraction(2,4)));
  	
  	
  	//Position at the end of the measure
  	pos = new ScorePosition(1,2,new Fraction(4,4),0);
  	clippedPos = controller.clipToMeasure(2, pos);
  	assertEquals(clippedPos.getMeasure(), 2);
  	assertTrue(clippedPos.getBeat().equals(new Fraction(4,4)));
  	
  	
  	//Position after the measure
  	pos = new ScorePosition(1,3,new Fraction(2,4),0);
  	clippedPos = controller.clipToMeasure(2, pos);
  	assertEquals(clippedPos.getMeasure(), 2);
  	assertTrue(clippedPos.getBeat().equals(new Fraction(4,4)));
  	
 	
  }

}
