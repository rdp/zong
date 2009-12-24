package com.xenoage.zong.util.demo;

import static com.xenoage.util.math.Fraction.fr;

import java.util.LinkedList;

import com.xenoage.util.Range;
import com.xenoage.util.annotations.Demo;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Part;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.header.ScoreHeader;
import com.xenoage.zong.data.instrument.Instrument;
import com.xenoage.zong.data.instrument.PitchedInstrument;
import com.xenoage.zong.data.music.Articulation;
import com.xenoage.zong.data.music.Beam;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.CurvedLine;
import com.xenoage.zong.data.music.CurvedLineWaypoint;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.Stem;
import com.xenoage.zong.data.music.StemDirection;
import com.xenoage.zong.data.music.TupletInfo;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.Volta;
import com.xenoage.zong.data.music.Articulation.Type;
import com.xenoage.zong.data.music.barline.Barline;
import com.xenoage.zong.data.music.barline.BarlineStyle;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.directions.Crescendo;
import com.xenoage.zong.data.music.directions.Diminuendo;
import com.xenoage.zong.data.music.directions.Dynamics;
import com.xenoage.zong.data.music.directions.DynamicsType;
import com.xenoage.zong.data.music.directions.Tempo;
import com.xenoage.zong.data.music.format.BezierPoint;
import com.xenoage.zong.data.music.format.Position;
import com.xenoage.zong.data.music.format.SP;
import com.xenoage.zong.data.music.key.TraditionalKey;
import com.xenoage.zong.data.music.text.Lyric;
import com.xenoage.zong.data.music.text.Lyric.SyllableType;
import com.xenoage.zong.data.music.time.CommonTime;
import com.xenoage.zong.data.music.time.NormalTime;


/**
 * Some demo methods for the {@link Score} class.
 * 
 * @author Andreas Wenger
 */
public class ScoreDemo
{
	
	/**
   * Creates a very small demo score for piano.
   */
  @Demo public static Score createDemoScore()
  {
    Score ret = new Score();
    Instrument instr = new PitchedInstrument("piano", "Piano", "Pno.", null, 0, 0, null, null, 0);
    //OBSOLETE
    //Instrument instr2 = ip.getInstrument("instrument_flute");
    //ArrayList<Instrument> instruments = new ArrayList<Instrument>();
    //instruments.add(instr);
    //instruments.add(instr2);
    Part pianoPart = new Part("Piano", null, 2, instr);
    ret.addPart(0, pianoPart);
    //End Addition
    //first staff: g-clef, eb-key, 3/4 time and some notes    
    Staff staff = ret.getStaff(ret.getPartStartIndex(pianoPart));
    ret.addEmptyMeasures(4);
    Measure measure = staff.getMeasures().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.G));
    measure.addNoVoiceElement(new TraditionalKey(-3));
    measure.addNoVoiceElement(new NormalTime(3, 4));
    Voice voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(2, -1, 4), new Fraction(1, 4));
    voice.addNote(new Pitch(3, 0, 4), new Fraction(1, 4));
    voice.addNote(new Pitch(4, 0, 4), new Fraction(1, 4));
    measure = staff.getMeasures().get(1);
    voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(5, -1, 4), new Fraction(2, 4));
    voice.addNote(new Pitch(0, 0, 5), new Fraction(1, 4));
    measure = staff.getMeasures().get(2);
    voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(6, -1, 4), new Fraction(1, 8));
    voice.addNote(new Pitch(5, -1, 4), new Fraction(1, 8));
    voice.addNote(new Pitch(4, 0, 4), new Fraction(1, 4));
    voice.addNote(new Pitch(3, 0, 5), new Fraction(1, 4));
    measure = staff.getMeasures().get(3);
    voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(2, -1, 5), new Fraction(2, 4));
    //second staff: f-clef, eb-key, 3/4 time and some notes
    staff = ret.getStaff(ret.getPartStartIndex(pianoPart) + 1);
    measure = staff.getMeasures().get(0);
    voice = measure.getVoices().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.F));
    measure.addNoVoiceElement(new TraditionalKey(-3));
    measure.addNoVoiceElement(new NormalTime(3, 4));
    voice.addNote(new Pitch(2, -1, 3), new Fraction(3, 4));
    measure = staff.getMeasures().get(1);
    voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(5, -1, 2), new Fraction(1, 4));
    measure = staff.getMeasures().get(2);
    voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(6, -1, 2), new Fraction(1, 4));
    voice.addNote(new Pitch(0, 0, 3), new Fraction(1, 4));
    voice.addNote(new Pitch(1, 0, 3), new Fraction(1, 4));
    measure = staff.getMeasures().get(3);
    voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(2, -1, 3), new Fraction(2, 4));
    //ret.addEmptyMeasures(3);
    return ret;
  }
  
  
  /**
   * Creates a demo score.
   * 4 measures are used 8 times for 32 measures.
   */
  @Demo public static Score createDemoScore32Measures()
  {
  	return createDemoScore(32);
  }
  
  
  /**
   * Creates a demo score.
   * 4 measures are used (measures/4) times for (measures) measures.
   */
  @Demo public static Score createDemoScore(int measures)
  {
    Score ret = new Score();
    Part pianoPart = ret.addPart(0, 2);
    //first staff: g-clef, eb-key, 3/4 time and some notes    
    Staff staff;
    ret.addEmptyMeasures(measures);
    
    //measure 0+4*n: begin repeat.
    //measure 1+4*n: end repeat, volta 1.
    //measure 2+4*n: double line, volta 2 (two measures).
    //measure 3+4*n: mid-measure barline at beat 2: ":||:"
    ScoreHeader header = ret.getScoreHeader();
    for (int i = 0; i < measures; i += 4)
    {
    	header.getMeasureColumnHeader(i + 0).setStartBarline(
    		Barline.createForwardRepeatBarline(BarlineStyle.HeavyLight));
    	header.getMeasureColumnHeader(i + 1).setEndBarline(
    		Barline.createBackwardRepeatBarline(BarlineStyle.LightHeavy, 1));
    	header.getMeasureColumnHeader(i + 1).setVolta(new Volta(1, new Range(1, 1), null, true));
    	header.getMeasureColumnHeader(i + 2).setEndBarline(
    		Barline.createBarline(BarlineStyle.LightLight));
    	header.getMeasureColumnHeader(i + 2).setVolta(new Volta(2, null, "weiter", false));
    	header.getMeasureColumnHeader(i + 3).addMiddleBarline(Barline.createBothRepeatBarline(
    		BarlineStyle.LightLight, 1), fr(2,4));
    }
    
    staff = ret.getStaff(ret.getPartStartIndex(pianoPart));
    Measure measure = staff.getMeasures().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.G));
    measure.addNoVoiceElement(new TraditionalKey(-3));
    measure.addNoVoiceElement(new CommonTime()); //new NormalTime(3, 4));
    
    staff = ret.getStaff(ret.getPartStartIndex(pianoPart) + 1);
    measure = staff.getMeasures().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.F));
    measure.addNoVoiceElement(new TraditionalKey(-3));
    measure.addNoVoiceElement(new NormalTime(3, 4));
    
    for (int i = 0; i < measures / 4; i++)
    {
      staff = ret.getStaff(ret.getPartStartIndex(pianoPart));
      int measureIndex = i * 4 + 0;
      measure = staff.getMeasures().get(measureIndex);
      Voice voice = measure.getVoices().get(0);
      if (i > 0)
      	measure.addNoVoiceElement(new Clef(ClefType.G));
      Tempo tempo = new Tempo(fr(1, 4), 108, "Andante", new Position(null, 22f, -5f, -10f));
      header.getMeasureColumnHeader(measureIndex).addTempo(tempo, Fraction._0);
      voice.addElement(tempo);
      Chord firstSlurredChord = voice.addNote(new Pitch(3, 1, 4), new Fraction(1, 4));
      voice.addNote(new Pitch(3, 0, 4), new Fraction(1, 4));
      firstSlurredChord.addDirection(new Dynamics(DynamicsType.ff));
      Chord lastSlurredChord = voice.addNote(new Pitch(4, 0, 4), new Fraction(1, 4));
      CurvedLine.createAndConnect(CurvedLine.Type.Slur,
      	new CurvedLineWaypoint(firstSlurredChord, null), new CurvedLineWaypoint(lastSlurredChord, null));
      
      measureIndex = i * 4 + 1;
      measure = staff.getMeasures().get(measureIndex);
      
      //voice 1
      voice = measure.getVoices().get(0);
      LinkedList<Chord> beamChords = new LinkedList<Chord>();
      Chord c = voice.addNote(new Pitch(2, -1, 4), new Fraction(1, 16));
      c.setStem(new Stem(StemDirection.Down, null));
      beamChords.add(c);
      beamChords.add(voice.addNote(new Pitch(4, 0, 4), new Fraction(1, 16)));
      beamChords.add(voice.addNote(new Pitch(6, -1, 4), new Fraction(1, 16)));
      beamChords.add(voice.addNote(new Pitch(1, 0, 5), new Fraction(1, 16)));
      new Beam(beamChords);
      voice.addNote(new Pitch(0, 0, 5), new Fraction(1, 2));
      //voice 2
      measure.addVoice();
      voice = measure.getVoices().get(1);
      beamChords = new LinkedList<Chord>();
      c = voice.addNote(new Pitch(2, -1, 5), new Fraction(1, 8));
      c.setStem(new Stem(StemDirection.Up, null));
      beamChords.add(c);
      c = voice.addNote(new Pitch(6, -1, 5), new Fraction(1, 16));
      c.setStem(new Stem(StemDirection.Up, null));
      beamChords.add(c);
      c = voice.addNote(new Pitch(1, 0, 5), new Fraction(1, 16));
      c.setStem(new Stem(StemDirection.Up, null));
      beamChords.add(c);
      new Beam(beamChords);
      beamChords = new LinkedList<Chord>();
      c = voice.addNote(new Pitch(6, -1, 5), new Fraction(7, 32));
      c.setStem(new Stem(StemDirection.Up, null));
      beamChords.add(c);
      c = voice.addNote(new Pitch(5, -1, 5), new Fraction(1, 32));
      c.setStem(new Stem(StemDirection.Up, null));
      beamChords.add(c);
      c = voice.addNote(new Pitch(5, -1, 5), new Fraction(3, 32));
      c.setStem(new Stem(StemDirection.Up, null));
      beamChords.add(c);
      c = voice.addNote(new Pitch(5, -1, 5), new Fraction(1, 32));
      c.setStem(new Stem(StemDirection.Up, null));
      beamChords.add(c);
      c = voice.addNote(new Pitch(5, -1, 5), new Fraction(4, 32));
      c.setStem(new Stem(StemDirection.Up, null));
      beamChords.add(c);
      new Beam(beamChords);
      
      measureIndex = i * 4 + 2;
      measure = staff.getMeasures().get(measureIndex);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(6, -1, 4), new Fraction(1, 8),
      	new Articulation[]{new Articulation(Type.Tenuto), new Articulation(Type.StrongAccent)});
      voice.addNote(new Pitch(5, -1, 4), new Fraction(1, 8));
      //tuplet
      Chord[] tuplet = new Chord[3];
      tuplet[0] = voice.addNote(new Pitch(4, 0, 4), new Fraction(1, 6));
      tuplet[1] = voice.addNote(new Pitch(3, 1, 5), new Fraction(1, 6));
      tuplet[2] = voice.addNote(new Pitch(2, 1, 5), new Fraction(1, 6));
      new TupletInfo(3, 2, fr(1, 4), true, tuplet);
      
      measureIndex = i * 4 + 3;
      measure = staff.getMeasures().get(measureIndex);
      voice = measure.getVoices().get(0);
      measure.addNoVoiceElement(new Clef(ClefType.F));
      voice.addNote(new Pitch(2, -1, 4), new Fraction(2, 4), accent());
      voice.addRest(new Fraction(1, 4));
      //second staff: f-clef, eb-key, 3/4 time and some notes
      staff = ret.getStaff(ret.getPartStartIndex(pianoPart) + 1);
      measure = staff.getMeasures().get(i * 4 + 0);
      voice = measure.getVoices().get(0);
      if (i > 0)
      	measure.addNoVoiceElement(new TraditionalKey(-3));
      firstSlurredChord = voice.addNote(new Pitch(2, -1, 3), new Fraction(7, 16));
      firstSlurredChord.setLyric(new Lyric("Ein", SyllableType.Single), 0);
      firstSlurredChord.addDirection(new Dynamics(DynamicsType.fp));
      c = voice.addNote(new Pitch(2, -1, 3), new Fraction(1, 16));
      c.setLyric(new Lyric("win", SyllableType.Begin), 0);
      Chord startTiedChord = voice.addNote(new Pitch(2, -1, 3), new Fraction(1, 8));
      startTiedChord.setLyric(new Lyric("zi", SyllableType.Middle), 0);
      Chord stopTiedChord = voice.addNote(new Pitch(2, -1, 3), new Fraction(1, 8));
      stopTiedChord.setLyric(new Lyric("ger", SyllableType.End), 0);
      CurvedLine.createAndConnect(CurvedLine.Type.Tie,
      	new CurvedLineWaypoint(startTiedChord, null), new CurvedLineWaypoint(stopTiedChord, null));
      measure = staff.getMeasures().get(i * 4 + 1);
      voice = measure.getVoices().get(0);
      measure.addNoVoiceElement(new TraditionalKey(5));
      Crescendo cresc = new Crescendo(null, null);
      voice.addElement(cresc);
      c = voice.addNote(new Pitch(5, -1, 2), new Fraction(1, 4));
      c.setLyric(new Lyric("Test", SyllableType.Single), 0);
      c = voice.addNote(new Pitch(2, -1, 3), new Fraction(1, 8));
      c.setLyric(Lyric.createExtend(), 0);
      c = voice.addNote(new Pitch(0, 0, 3), new Fraction(1, 8));
      c.setLyric(Lyric.createExtend(), 0);
      voice.addRest(new Fraction(1, 4));
      measure = staff.getMeasures().get(i * 4 + 2);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(6, -1, 2), new Fraction(1, 4), tenuto());
      voice.addNote(new Pitch(0, 0, 3), new Fraction(1, 4), tenuto());
      startTiedChord = voice.addNote(new Pitch(1, 0, 3), new Fraction(1, 4), tenuto());
      measure = staff.getMeasures().get(i * 4 + 3);
      voice = measure.getVoices().get(0);
      Tempo tempo2 = new Tempo(fr(1, 2), 76, null, null);
      header.getMeasureColumnHeader(measureIndex).addTempo(tempo2, Fraction._0);
      voice.addElement(tempo2);
      stopTiedChord = lastSlurredChord = voice.addNote(new Pitch(1, 0, 3), new Fraction(2, 4));
      CurvedLine.createAndConnect(CurvedLine.Type.Tie,
      	new CurvedLineWaypoint(startTiedChord, null), new CurvedLineWaypoint(stopTiedChord, null));
      CurvedLine.createAndConnect(CurvedLine.Type.Slur,
      	new CurvedLineWaypoint(firstSlurredChord, null), new CurvedLineWaypoint(lastSlurredChord, null));
      voice.addElement(cresc.getWedgeEnd());
      voice.addRest(new Fraction(1, 8));
      voice.addRest(new Fraction(1, 16));
      voice.addRest(new Fraction(1, 32));
      voice.addRest(new Fraction(1, 64));
      voice.addRest(new Fraction(1, 128));
      voice.addRest(new Fraction(1, 256));
      voice.addRest(new Fraction(1, 256));
    }
    
    Part bassPart = ret.addPart(1, 1);
    //f-clef, eb-key, 3/4 time and some notes    
    staff = ret.getStaff(ret.getPartStartIndex(bassPart));
    measure = staff.getMeasures().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.F));
    measure.addNoVoiceElement(new TraditionalKey(-3));
    measure.addNoVoiceElement(new NormalTime(3, 4));
    
    for (int i = 0; i < measures / 4; i++)
    {
      staff = ret.getStaff(ret.getPartStartIndex(bassPart));
      measure = staff.getMeasures().get(i * 4 + 0);
      //voice 1
      Voice voice = measure.getVoices().get(0);
      LinkedList<Chord> beamChords = new LinkedList<Chord>();
      Chord firstSlurredChord = voice.addNote(new Pitch(2, -1, 3), new Fraction(1, 8), staccato());
      beamChords.add(firstSlurredChord);
      firstSlurredChord.addDirection(new Dynamics(DynamicsType.sfz));
      beamChords.add(voice.addNote(new Pitch(3, 0, 3), new Fraction(1, 8), staccato()));
      Diminuendo dim = new Diminuendo(2f, null);
      voice.addElement(dim);
      beamChords.add(voice.addNote(new Pitch(6, 0, 3), new Fraction(1, 8), staccato()));
      beamChords.add(voice.addNote(new Pitch(5, -1, 3), new Fraction(1, 8), staccato()));
      beamChords.add(voice.addNote(new Pitch(4, 0, 3), new Fraction(1, 8), tenuto()));
      Chord lastSlurredChord = voice.addNote(new Pitch(5, -1, 3), new Fraction(1, 8), tenuto());
      beamChords.add(lastSlurredChord);
      new Beam(beamChords);
      CurvedLine slur = CurvedLine.createAndConnect(CurvedLine.Type.Slur,
      	new CurvedLineWaypoint(firstSlurredChord, null), new CurvedLineWaypoint(lastSlurredChord, null));
      slur.setBezierPoint(slur.getStart(), new BezierPoint(new SP(0, -4), new SP(5, -8))); //custom slur formatting
      slur.setBezierPoint(slur.getStop(), new BezierPoint(new SP(2, 0), new SP(10, 10))); //custom slur formatting
      voice.addElement(dim.getWedgeEnd());
      //voice 2
      measure.addVoice();
      voice = measure.getVoices().get(1);
      beamChords = new LinkedList<Chord>();
      beamChords.add(voice.addNote(new Pitch(2, -1, 3), new Fraction(1, 8)));
      beamChords.add(voice.addNote(new Pitch(3, 0, 3), new Fraction(1, 8)));
      beamChords.add(voice.addNote(new Pitch(6, 0, 3), new Fraction(1, 8)));
      beamChords.add(voice.addNote(new Pitch(5, -1, 3), new Fraction(1, 8)));
      beamChords.add(voice.addNote(new Pitch(4, 0, 3), new Fraction(1, 8)));
      beamChords.add(voice.addNote(new Pitch(5, -1, 3), new Fraction(1, 8)));
      new Beam(beamChords);
      
      measure = staff.getMeasures().get(i * 4 + 1);
      //voice 1
      voice = measure.getVoices().get(0);
      Chord c = voice.addNote(new Pitch(6, -1, 2), new Fraction(1, 4));
      c.setLyric(new Lyric("Trom", SyllableType.Begin), 0);
      c = voice.addNote(new Pitch(0, 0, 3), new Fraction(1, 4));
      c.setLyric(new Lyric("pe", SyllableType.Middle), 0);
      c = voice.addNote(new Pitch(1, 0, 3), new Fraction(1, 4));
      c.setLyric(new Lyric("te", SyllableType.End), 0);
      c.setLyric(new Lyric("Se", SyllableType.Begin), 1);
      //voice 2
      measure.addVoice();
      voice = measure.getVoices().get(1);
      Chord chord = voice.addNote(new Pitch(6, -1, 1), new Fraction(3, 4));
      chord.addDirection(new Dynamics(DynamicsType.mp));
      
      measure = staff.getMeasures().get(i * 4 + 2);
      measure.addVoice();
      voice = measure.getVoices().get(1);
      c = voice.addNote(new Pitch(5, -1, 1), new Fraction(3, 4));
      c.setLyric(Lyric.createExtend(), 0);
      c.setLyric(new Lyric("cond", SyllableType.End), 1);
      measure = staff.getMeasures().get(i * 4 + 3);
      measure.addVoice();
      voice = measure.getVoices().get(1);
      firstSlurredChord = voice.addNote(new Pitch(2, -1, 2), new Fraction(2, 4));
      firstSlurredChord.setLyric(Lyric.createExtend(), 0);
      firstSlurredChord.setLyric(new Lyric("voice", SyllableType.Single), 1);
      lastSlurredChord = voice.addNote(new Pitch(2, -1, 2), new Fraction(1, 4));
      lastSlurredChord.setLyric(Lyric.createExtend(), 0);
      CurvedLine.createAndConnect(CurvedLine.Type.Tie,
      	new CurvedLineWaypoint(firstSlurredChord, null), new CurvedLineWaypoint(lastSlurredChord, null));
    }
    
    return ret;
  }
  
  
  /**
   * Creates a demo score for piano.
   * 4 measures are used 256 times for 1024 measures.
   */
  @Demo public static Score createDemoScore1024Measures()
  {
    Score ret = new Score();
    Part pianoPart = ret.addPart(0, 2);
    //first staff: g-clef, eb-key, 3/4 time and some notes    
    Staff staff;
    ret.addEmptyMeasures(1024);
    
    staff = ret.getStaff(ret.getPartStartIndex(pianoPart));
    Measure measure = staff.getMeasures().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.G));
    measure.addNoVoiceElement(new TraditionalKey(-3));
    measure.addNoVoiceElement(new NormalTime(3, 4));
    
    staff = ret.getStaff(ret.getPartStartIndex(pianoPart) + 1);
    measure = staff.getMeasures().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.F));
    measure.addNoVoiceElement(new TraditionalKey(-3));
    measure.addNoVoiceElement(new NormalTime(3, 4));
    
    for (int i = 0; i < 256; i++)
    {
      staff = ret.getStaff(ret.getPartStartIndex(pianoPart));
      measure = staff.getMeasures().get(i * 4 + 0);
      Voice voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(2, -1, 4), new Fraction(1, 4));
      voice.addNote(new Pitch(3, 0, 4), new Fraction(1, 4));
      voice.addNote(new Pitch(4, 0, 4), new Fraction(1, 4));
      measure = staff.getMeasures().get(i * 4 + 1);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(5, -1, 4), new Fraction(2, 4));
      voice.addNote(new Pitch(0, 0, 5), new Fraction(1, 4));
      measure = staff.getMeasures().get(i * 4 + 2);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(6, -1, 4), new Fraction(1, 8));
      voice.addNote(new Pitch(5, -1, 4), new Fraction(1, 8));
      voice.addNote(new Pitch(4, 0, 4), new Fraction(1, 4));
      voice.addNote(new Pitch(3, 1, 5), new Fraction(1, 4));
      measure = staff.getMeasures().get(i * 4 + 3);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(2, -1, 4), new Fraction(2, 4));
      voice.addRest(new Fraction(1, 4));
      //second staff: f-clef, eb-key, 3/4 time and some notes
      staff = ret.getStaff(ret.getPartStartIndex(pianoPart) + 1);
      measure = staff.getMeasures().get(i * 4 + 0);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(2, -1, 3), new Fraction(7, 16));
      voice.addNote(new Pitch(2, -1, 3), new Fraction(1, 16));
      voice.addNote(new Pitch(2, -1, 3), new Fraction(1, 8));
      voice.addNote(new Pitch(2, -1, 3), new Fraction(1, 8));
      measure = staff.getMeasures().get(i * 4 + 1);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(5, -1, 2), new Fraction(1, 4));
      voice.addRest(new Fraction(2, 4));
      measure = staff.getMeasures().get(i * 4 + 2);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(6, -1, 2), new Fraction(1, 4));
      voice.addNote(new Pitch(0, 0, 3), new Fraction(1, 4));
      voice.addNote(new Pitch(1, 0, 3), new Fraction(1, 4));
      measure = staff.getMeasures().get(i * 4 + 3);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(2, -1, 2), new Fraction(2, 4));
      voice.addRest(new Fraction(1, 4));
    }
    
    Part bassPart = ret.addPart(1, 1);
    //f-clef, eb-key, 3/4 time and some notes    
    staff = ret.getStaff(ret.getPartStartIndex(bassPart));
    measure = staff.getMeasures().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.F));
    measure.addNoVoiceElement(new TraditionalKey(-3));
    measure.addNoVoiceElement(new NormalTime(3, 4));
    
    for (int i = 0; i < 256; i++)
    {
      staff = ret.getStaff(ret.getPartStartIndex(bassPart));
      measure = staff.getMeasures().get(i * 4 + 0);
      Voice voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(2, -1, 2), new Fraction(3, 4));
      measure = staff.getMeasures().get(i * 4 + 1);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(6, -1, 1), new Fraction(3, 4));
      measure = staff.getMeasures().get(i * 4 + 2);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(5, -1, 1), new Fraction(3, 4));
      measure = staff.getMeasures().get(i * 4 + 3);
      voice = measure.getVoices().get(0);
      voice.addNote(new Pitch(2, -1, 2), new Fraction(3, 4));
    }
    
    return ret;
  }
  
  
  /**
   * Creates a single-measure demo score for piano.
   */
  @Demo public static Score createDemoScore1Measure()
  {
    Score ret = new Score();
    Part pianoPart = ret.addPart(0, 2);
    ret.addEmptyMeasures(1);
    //first staff: g-clef, eb-key, 3/4 time and some notes    
    Staff staff;
    int startStaffIndex = ret.getPartStartIndex(pianoPart);
    
    staff = ret.getStaff(startStaffIndex);
    Measure measure = staff.getMeasures().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.G));
    measure.addNoVoiceElement(new TraditionalKey(-3));
    measure.addNoVoiceElement(new NormalTime(4, 4)); //3181111, 49711111));
    
    staff = ret.getStaff(startStaffIndex + 1);
    measure = staff.getMeasures().get(0);
    measure.addNoVoiceElement(new Clef(ClefType.F));
    measure.addNoVoiceElement(new TraditionalKey(-3));
    measure.addNoVoiceElement(new NormalTime(497, 318));
    
    staff = ret.getStaff(startStaffIndex);
    measure = staff.getMeasures().get(0);
    Voice voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(2, 0, 4), new Fraction(1, 4));
    voice.addNote(new Pitch(3, 0, 4), new Fraction(1, 4));
    voice.addNote(new Pitch(4, 0, 4), new Fraction(2, 4));
  
    staff = ret.getStaff(startStaffIndex + 1);
    measure = staff.getMeasures().get(0);
    voice = measure.getVoices().get(0);
    voice.addNote(new Pitch(2, -1, 3), new Fraction(4, 4));

    return ret;
  }
  
  
  /**
   * Creates a simple demo score with 1 staff, 4 equal measures
   * with each 2 voices.
   */
  @Demo public static Score createDemoScoreSimple2Staves4Measures2Voices()
  {
    Score ret = new Score();
    Part part = ret.addPart(0, 2);  
    Staff staff;
    ret.addEmptyMeasures(4);
    
    for (int iStaff = 0; iStaff < 2; iStaff++)
    {
    	staff = ret.getStaff(ret.getPartStartIndex(part) + iStaff);
      Measure measure = staff.getMeasures().get(0);
      measure.addNoVoiceElement(new Clef(ClefType.G));
      measure.addNoVoiceElement(new TraditionalKey(7));
      measure.addNoVoiceElement(new NormalTime(3, 4));
      
	    for (int iMeasure = 0; iMeasure < 4; iMeasure++)
	    {
		    measure = staff.getMeasures().get(iMeasure);
		    //in staff 1, measure 2: change key signature
		    if (iStaff == 1 && iMeasure == 2)
		    {
		    	measure.addNoVoiceElement(new TraditionalKey(-7));
		    }
		    //voice 0 (not in staff 0 in measure 1 and measure 3)
		    if (!(iStaff == 0 && (iMeasure == 1 || iMeasure == 3)))
		    {
			    Voice voice = measure.getVoices().get(0);
			    voice.addNote(new Pitch(1, 0, 5), new Fraction(1, 4));
			    voice.addNote(new Pitch(2, 0, 5), new Fraction(1, 4));
			    voice.addNote(new Pitch(3, 1, 5), new Fraction(1, 4));
		    }
		    
		    //voice 1
		    measure.addVoice();
		    Voice voice = measure.getVoices().get(1);
		    voice.addNote(new Pitch(3, 1, 4), new Fraction(1, 4));
		    voice.addNote(new Pitch(4, 0, 4), new Fraction(3, 8));
		    voice.addNote(new Pitch(5, 0, 4), new Fraction(1, 8));
	    }
    }
    
    return ret;
  }
  
  
  
  /**
   * Creates a demo score with two measures and 3 barlines.
   */
  @Demo public static Score createBarlinesDemoScore()
  {
  	BarlineStyle style = BarlineStyle.Regular;
  	
    Score ret = new Score();
    ret.addPart(0, 1); 
    ret.addEmptyMeasures(2);
    
    ScoreHeader header = ret.getScoreHeader();
  	header.getMeasureColumnHeader(0).setStartBarline(Barline.createBarline(style));
  	header.getMeasureColumnHeader(0).setEndBarline(Barline.createBarline(style));
  	header.getMeasureColumnHeader(1).setEndBarline(Barline.createBarline(style));
    
    return ret;
  }
  
  
  private static Articulation[] staccato()
  {
  	return new Articulation[]{new Articulation(Type.Staccato)};
  }
  
  
  private static Articulation[] tenuto()
  {
  	return new Articulation[]{new Articulation(Type.Tenuto)};
  }
  
  
  private static Articulation[] accent()
  {
  	return new Articulation[]{new Articulation(Type.Accent)};
  }
  

}
