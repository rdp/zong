/**
 * 
 */
package com.xenoage.zong.util.demo;

import static com.xenoage.util.math.Fraction.fr;

import java.util.LinkedList;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Part;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.StavesList;
import com.xenoage.zong.data.format.StaffLayout;
import com.xenoage.zong.data.header.ScoreHeader;
import com.xenoage.zong.data.instrument.Instrument;
import com.xenoage.zong.data.instrument.PitchedInstrument;
import com.xenoage.zong.data.music.Articulation;
import com.xenoage.zong.data.music.Beam;
import com.xenoage.zong.data.music.BracketGroupStyle;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.ChordData;
import com.xenoage.zong.data.music.CurvedLine;
import com.xenoage.zong.data.music.CurvedLineWaypoint;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.Note;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.Rest;
import com.xenoage.zong.data.music.RestData;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.Articulation.Type;
import com.xenoage.zong.data.music.barline.Barline;
import com.xenoage.zong.data.music.barline.BarlineGroupStyle;
import com.xenoage.zong.data.music.barline.BarlineStyle;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.directions.Crescendo;
import com.xenoage.zong.data.music.directions.Dynamics;
import com.xenoage.zong.data.music.directions.DynamicsType;
import com.xenoage.zong.data.music.directions.Tempo;
import com.xenoage.zong.data.music.format.BezierPoint;
import com.xenoage.zong.data.music.format.Position;
import com.xenoage.zong.data.music.format.SP;
import com.xenoage.zong.data.music.key.TraditionalKey;
import com.xenoage.zong.data.music.time.CommonTime;


/**
 * This class creates a demo score (Revolutionary Etude op. 10/12 Chopin).
 * 
 * @author Uli Teschemacher
 *
 */
public class ScoreRevolutionary
{

	public static Score createScore()
	{
		Score score = new Score();
		Instrument instr = new PitchedInstrument("piano", "Piano", "Pno.", null, 0, 0, null,
			null, 0);
		float interlineSpace = score.getScoreFormat().getInterlineSpace();
		StaffLayout layout = StaffLayout.createEmptyStaffLayout();
		layout.setStaffDistance(interlineSpace * 9);
		score.getScoreFormat().setDefaultStaffLayout(1, layout);
		
		
		Articulation [] accent = {new Articulation(Type.Accent)};
		Articulation [] staccato = {new Articulation(Type.Staccato)};

		ScoreHeader header = score.getScoreHeader();
    		
		Part pianoPart = new Part("Piano", null, 2, instr);
		score.addPart(0, pianoPart);

		//Set Barlines and Brackets
		StavesList stavesList = score.getStavesList();
		stavesList.addBarlineGroup(0, 1, BarlineGroupStyle.Common);
		stavesList.addBracketGroup(0, 1, BracketGroupStyle.Brace);
		
		
		//first staff: g-clef, c-minor, C (4/4) time and some notes    
		Staff staff = score.getStaff(score.getPartStartIndex(pianoPart));
		score.addEmptyMeasures(5);
		Measure measure = staff.getMeasures().get(0);
		measure.addNoVoiceElement(new Clef(ClefType.G));
		measure.addNoVoiceElement(new TraditionalKey(-3));
		measure.addNoVoiceElement(new CommonTime());
		Voice voice = measure.getVoices().get(0);

		Fraction f2 = new Fraction(1, 2);
		Fraction f4 = new Fraction(1, 4);
		Fraction f8 = new Fraction(1, 8);
		Fraction f16 = new Fraction(1, 16);

		LinkedList<Chord> b;

		
		//Measure 1
    Tempo tempo = new Tempo(fr(1, 4), 160, "Allegro con fuoco.", new Position(null, 22f, -5f, -5f));
    header.getMeasureColumnHeader(0).addTempo(tempo, Fraction._0);
    voice.addElement(tempo);
    Chord c = createChord(f2,accent, new Pitch(Pitch.B, 0, 4), new Pitch(Pitch.D, 0, 5),
			new Pitch(Pitch.F, 0, 5), new Pitch(Pitch.G, 0, 5), new Pitch(Pitch.B, 0, 5));
    c.addDirection(new Dynamics(DynamicsType.f));
		voice.addElement(c);
		voice.addElement(new Rest(new RestData(f2)));


		//Measure 2
		measure = staff.getMeasures().get(1);
		voice = measure.getVoices().get(0);
		voice.addElement(new Rest(new RestData(f2)));
		voice.addElement(new Rest(new RestData(f4)));

		b = new LinkedList<Chord>();
    Crescendo cresc = new Crescendo(null, new Position(null, null, -1f, -2f));
    voice.addElement(cresc);
		c = createChord(new Fraction(3, 16),accent, new Pitch(Pitch.A, -1, 4), new Pitch(Pitch.E,
			-1, 5), new Pitch(Pitch.F, 0, 5), new Pitch(Pitch.A, -1, 5));
		b.add(c);
		voice.addElement(c);
		Chord firstSlurredChord = c;
		c = createChord(f16, new Pitch(Pitch.G, 0, 4), new Pitch(Pitch.G, 0, 5));
		voice.addElement(c);
		b.add(c);
		new Beam(b);
		Chord lastSlurredChord = c;
    voice.addElement(cresc.getWedgeEnd());
		CurvedLine curvedLine = CurvedLine.createAndConnect(CurvedLine.Type.Slur,
    	new CurvedLineWaypoint(firstSlurredChord, null), new CurvedLineWaypoint(lastSlurredChord, null));
		curvedLine.setBezierPoint(curvedLine.getStart(), new BezierPoint(new SP(interlineSpace*0.8f,interlineSpace*7.6f),new SP(interlineSpace,interlineSpace *0.8f)));
		curvedLine.setBezierPoint(curvedLine.getStop(), new BezierPoint(new SP(0,interlineSpace *6f), new SP(- interlineSpace  , interlineSpace * 1f)));

		//Measure 3
		measure = staff.getMeasures().get(2);
		voice = measure.getVoices().get(0);

		c = createChord(f2, new Pitch(Pitch.D, 0, 5), new Pitch(Pitch.F, 0, 5), new Pitch(
		Pitch.G, 0, 5), new Pitch(Pitch.D, 0, 6));
    c.addDirection(new Dynamics(DynamicsType.f));
    voice.addElement(c);
		voice.addElement(new Rest(new RestData(f2)));


		//Measure 4
		measure = staff.getMeasures().get(3);
		voice = measure.getVoices().get(0);
		voice.addElement(new Rest(new RestData(f2)));
		voice.addElement(new Rest(new RestData(f4)));

		b = new LinkedList<Chord>();
    cresc = new Crescendo(null, new Position(null, null, -1f, -2f));
    voice.addElement(cresc);
		c = createChord(new Fraction(3, 16),accent, new Pitch(Pitch.A, -1, 4), new Pitch(Pitch.E,
			-1, 5), new Pitch(Pitch.F, 0, 5), new Pitch(Pitch.A, -1, 5));
		b.add(c);
		voice.addElement(c);
		firstSlurredChord = c;
		c = createChord(f16, new Pitch(Pitch.G, 0, 4), new Pitch(Pitch.G, 0, 5));
		voice.addElement(c);
		b.add(c);
		lastSlurredChord = c;
		new Beam(b);
    voice.addElement(cresc.getWedgeEnd());
		curvedLine = CurvedLine.createAndConnect(CurvedLine.Type.Slur,
    	new CurvedLineWaypoint(firstSlurredChord, null), new CurvedLineWaypoint(lastSlurredChord, null));
		curvedLine.setBezierPoint(curvedLine.getStart(), new BezierPoint(new SP(interlineSpace*0.8f,interlineSpace*7.6f),new SP(interlineSpace,interlineSpace *0.8f)));
		curvedLine.setBezierPoint(curvedLine.getStop(), new BezierPoint(new SP(0,interlineSpace *6f), new SP(- interlineSpace  , interlineSpace * 1f)));


		//Measure 5
		measure = staff.getMeasures().get(4);
		voice = measure.getVoices().get(0);

		c = createChord(f4,staccato, new Pitch(Pitch.F, 0, 5),
			new Pitch(Pitch.G, 0, 5), new Pitch(Pitch.D, 0, 6), new Pitch(Pitch.F, 0, 6));
    c.addDirection(new Dynamics(DynamicsType.f));
		voice.addElement(c);
		//voice.addRest(f8);
		voice.addRest(f4);
		voice.addRest(f2);

		
		
		//second staff: f-clef, c-minor, C (4/4) time and some notes
		staff = score.getStaff(score.getPartStartIndex(pianoPart) + 1);
		measure = staff.getMeasures().get(0);
		measure.addNoVoiceElement(new Clef(ClefType.F));
		measure.addNoVoiceElement(new TraditionalKey(-3));
		measure.addNoVoiceElement(new CommonTime());
		voice = measure.getVoices().get(0);

	
		//Measure 1
		
		voice.addElement(new Rest(new RestData(f8)));
		b = new LinkedList<Chord>();
		c = createChord(f16, new Pitch(Pitch.A, -1, 4));
		b.add(c);
		voice.addElement(c);
		firstSlurredChord = c;
		c = createChord(f16, new Pitch(Pitch.G, 0, 4));
		voice.addElement(c);
		b.add(c);
		new Beam(b);
		
		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.F, 0, 4));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 4));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.E, -1, 4));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 4));
		voice.addElement(c);
		b.add(c);		
		new Beam(b);

		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.B, 0, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.A, -1, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 3));
		voice.addElement(c);
		b.add(c);		
		new Beam(b);

		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.F, 0, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.E, -1, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 3));
		voice.addElement(c);
		b.add(c);		
		new Beam(b);

		//Measure 2
		measure = staff.getMeasures().get(1);
		voice = measure.getVoices().get(0);
		
		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.B, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.A, -1, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 2));
		voice.addElement(c);
		b.add(c);		
		new Beam(b);

		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.F, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.E, -1, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 2));
		voice.addElement(c);
		b.add(c);		
		new Beam(b);

		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.C, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 1));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.C, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 1));
		voice.addElement(c);
		b.add(c);
		new Beam(b);

		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.C, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 1));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.C, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 1));
		voice.addElement(c);
		b.add(c);
		new Beam(b);
		lastSlurredChord = c;
		
		curvedLine = CurvedLine.createAndConnect(CurvedLine.Type.Slur,
    	new CurvedLineWaypoint(firstSlurredChord, null), new CurvedLineWaypoint(lastSlurredChord, null));
		curvedLine.setBezierPoint(curvedLine.getStart(), new BezierPoint(new SP(0,interlineSpace*1.5f),new SP(15,5)));
		curvedLine.setBezierPoint(curvedLine.getStop(), new BezierPoint(new SP(0,interlineSpace *7.5f), new SP(- interlineSpace * 5, interlineSpace * 2)));

		//Measure 3
		measure = staff.getMeasures().get(2);
		voice = measure.getVoices().get(0);
	
		c = createChord(f8,staccato, new Pitch(Pitch.B,0,1));
		voice.addElement(c);
		b = new LinkedList<Chord>();
		c = createChord(f16, new Pitch(Pitch.A, -1, 4));
		b.add(c);
		voice.addElement(c);
		firstSlurredChord = c;
		c = createChord(f16, new Pitch(Pitch.G, 0, 4));
		voice.addElement(c);
		b.add(c);
		new Beam(b);
		
		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.F, 0, 4));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 4));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.E, -1, 4));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 4));
		voice.addElement(c);
		b.add(c);		
		new Beam(b);

		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.B, 0, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.A, -1, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 3));
		voice.addElement(c);
		b.add(c);		
		new Beam(b);

		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.F, 0, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.E, -1, 3));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 3));
		voice.addElement(c);
		b.add(c);		
		new Beam(b);
		lastSlurredChord = c;

		curvedLine = CurvedLine.createAndConnect(CurvedLine.Type.Slur,
    	new CurvedLineWaypoint(firstSlurredChord, null), new CurvedLineWaypoint(lastSlurredChord, null));
		curvedLine.setBezierPoint(curvedLine.getStart(), new BezierPoint(new SP(0,interlineSpace*1.5f),new SP(15,3)));
		curvedLine.setBezierPoint(curvedLine.getStop(), new BezierPoint(new SP(0,interlineSpace *5f), new SP(- interlineSpace * 5.5f, interlineSpace * 2)));


		//Measure 4
		measure = staff.getMeasures().get(3);
		voice = measure.getVoices().get(0);
		
		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.B, 0, 2));
		voice.addElement(c);
		b.add(c);
		firstSlurredChord = c;
		c = createChord(f16, new Pitch(Pitch.G, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.A, -1, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 2));
		voice.addElement(c);
		b.add(c);		
		new Beam(b);

		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.F, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.E, -1, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.D, 0, 2));
		voice.addElement(c);
		b.add(c);		
		new Beam(b);

		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.C, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 1));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.C, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 1));
		voice.addElement(c);
		b.add(c);
		new Beam(b);

		b = new LinkedList<Chord>();
		c = createChord(f16,accent, new Pitch(Pitch.C, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 1));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.C, 0, 2));
		voice.addElement(c);
		b.add(c);
		c = createChord(f16, new Pitch(Pitch.G, 0, 1));
		voice.addElement(c);
		b.add(c);
		new Beam(b);
		lastSlurredChord = c;

		curvedLine = CurvedLine.createAndConnect(CurvedLine.Type.Slur,
    	new CurvedLineWaypoint(firstSlurredChord, null), new CurvedLineWaypoint(lastSlurredChord, null));
		curvedLine.setBezierPoint(curvedLine.getStart(), new BezierPoint(new SP(-interlineSpace,interlineSpace*8.5f),new SP(15,4)));
		curvedLine.setBezierPoint(curvedLine.getStop(), new BezierPoint(new SP(0,interlineSpace *7.5f), new SP(- interlineSpace * 5, interlineSpace * 2)));
	
		//Measure 5
		measure = staff.getMeasures().get(4);
		voice = measure.getVoices().get(0);
		
		c = createChord(f4,staccato, new Pitch(Pitch.B,0,1));
		voice.addElement(c);

		//voice.addRest(f8);
		voice.addRest(f4);
		voice.addRest(f2);

		
		//End line
		score.getScoreHeader().getMeasureColumnHeader(4).setEndBarline(Barline.createBarline(BarlineStyle.LightHeavy));


		return score;
	}
	
	
	public static Score createScoreOnlyFistChord()
	{
		Score score = new Score();
		Instrument instr = new PitchedInstrument("piano", "Piano", "Pno.", null, 0, 0, null,
			null, 0);
    		
		Part pianoPart = new Part("Piano", null, 1, instr);
		score.addPart(0, pianoPart);

		//first staff: g-clef, c-minor, C (4/4) time and some notes    
		Staff staff = score.getStaff(score.getPartStartIndex(pianoPart));
		score.addEmptyMeasures(1);
		Measure measure = staff.getMeasures().get(0);
		measure.addNoVoiceElement(new Clef(ClefType.G));
		measure.addNoVoiceElement(new TraditionalKey(-3));
		measure.addNoVoiceElement(new CommonTime());
		Voice voice = measure.getVoices().get(0);

		Fraction f2 = new Fraction(1, 2);
		
		//Measure 1
    Chord c = createChord(f2, new Articulation[0], new Pitch(Pitch.B, 0, 4), new Pitch(Pitch.D, 0, 5),
			new Pitch(Pitch.F, 0, 5), new Pitch(Pitch.G, 0, 5), new Pitch(Pitch.B, 0, 5));
		voice.addElement(c);
		voice.addElement(new Rest(new RestData(f2)));

		return score;
	}



	private static Chord createChord(Fraction fraction, Pitch... pitches)
	{
		return createChord(fraction, null, pitches);
	}


	private static Chord createChord(Fraction fraction, Articulation[] articulation,
		Pitch... pitches)
	{
		Note[] notes = new Note[pitches.length];
		for (int i = 0; i < pitches.length; i++)
		{
			notes[i] = new Note(pitches[i]);
		}
		return new Chord(new ChordData(notes, fraction, articulation));
	}
}
