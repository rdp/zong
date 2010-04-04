package com.xenoage.zong.util.demo;

import static com.xenoage.pdlib.IVector.ivec;
import static com.xenoage.util.math.Fraction._0;
import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.MP.mp;
import static com.xenoage.zong.core.music.MP.mp0;
import static com.xenoage.zong.core.music.Pitch.A;
import static com.xenoage.zong.core.music.Pitch.B;
import static com.xenoage.zong.core.music.Pitch.C;
import static com.xenoage.zong.core.music.Pitch.D;
import static com.xenoage.zong.core.music.Pitch.E;
import static com.xenoage.zong.core.music.Pitch.F;
import static com.xenoage.zong.core.music.Pitch.G;
import static com.xenoage.zong.core.music.Pitch.pi;
import static com.xenoage.zong.core.music.format.SP.sp;
import static com.xenoage.zong.io.score.ScoreController.attachElement;
import static com.xenoage.zong.io.score.ScoreController.writeColumnEndBarline;
import static com.xenoage.zong.io.score.ScoreController.writeCurvedLine;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.StaffLayout;
import com.xenoage.zong.core.instrument.Instrument;
import com.xenoage.zong.core.instrument.PitchedInstrument;
import com.xenoage.zong.core.instrument.Transpose;
import com.xenoage.zong.core.music.ColumnElement;
import com.xenoage.zong.core.music.Part;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.core.music.StavesList;
import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.barline.BarlineGroupStyle;
import com.xenoage.zong.core.music.barline.BarlineStyle;
import com.xenoage.zong.core.music.bracket.BracketGroupStyle;
import com.xenoage.zong.core.music.chord.Articulation;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.Note;
import com.xenoage.zong.core.music.chord.Articulation.Type;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.clef.ClefType;
import com.xenoage.zong.core.music.curvedline.CurvedLine;
import com.xenoage.zong.core.music.curvedline.CurvedLineWaypoint;
import com.xenoage.zong.core.music.direction.Crescendo;
import com.xenoage.zong.core.music.direction.Dynamics;
import com.xenoage.zong.core.music.direction.DynamicsType;
import com.xenoage.zong.core.music.direction.Tempo;
import com.xenoage.zong.core.music.format.BezierPoint;
import com.xenoage.zong.core.music.format.Position;
import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.core.music.time.CommonTime;
import com.xenoage.zong.io.score.selections.Cursor;


/**
 * This class creates a demo score
 * (Revolutionary Etude op. 10/12 Chopin).
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class ScoreRevolutionary
{

	public static Score createScore()
	{
		Score score = Score.empty();
		Instrument instr = new PitchedInstrument("piano", "Piano", "Pno.", null, 0,
			Transpose.none(), null, null, 0);
		float is = score.getScoreFormat().getInterlineSpace();
		StaffLayout staffLayout = new StaffLayout(is * 9);
		score = score.withScoreFormat(
			score.getScoreFormat().withStaffLayoutOther(staffLayout));
		
		Articulation[] accent = {new Articulation(Type.Accent)};
		Articulation[] staccato = {new Articulation(Type.Staccato)};
		
		Fraction f2 = fr(1, 2);
		Fraction f4 = fr(1, 4);
		Fraction f8 = fr(1, 8);
		Fraction f16 = fr(1, 16);
		
		Chord attachC, firstSlurC, lastSlurC;
		BezierPoint firstSlurB, lastSlurB;

		Part pianoPart = new Part("Piano", null, 2, instr);
		score = score.plusPart(pianoPart);

		//set barlines and brackets
		StavesList stavesList = score.getStavesList();
		stavesList = stavesList.plusBarlineGroup(0, 1, BarlineGroupStyle.Common);
		stavesList = stavesList.plusBracketGroup(0, 1, BracketGroupStyle.Brace);
		score = score.withStavesList(stavesList);
		
		//use cursor for more convenient input
		Cursor cursor = new Cursor(score, mp0, true);
		
		//C minor, C (4/4) time
		cursor = cursor.write((ColumnElement) new TraditionalKey(-3));
		cursor = cursor.write(new CommonTime());
		
		//first staff: g-clef and some notes
		cursor = cursor.write(new Clef(ClefType.G));

		//measure 1
    Tempo tempo = new Tempo(f4, 160, "Allegro con fuoco.", new Position(null, 22f, -5f, -5f));
    cursor = cursor.write((ColumnElement) tempo);
    cursor = cursor.write(attachC = chord(f2, accent, pi(B, 4), pi(D, 5),
    	pi(F, 5), pi(G, 5), pi(B, 5)));
    cursor = cursor.withScore(
    	attachElement(cursor.getScore(), attachC, new Dynamics(DynamicsType.f)));
    cursor = cursor.write(new Rest(f2));

		//measure 2
    cursor = cursor.write(new Rest(f2));
    cursor = cursor.write(new Rest(f4));
    Crescendo cresc = new Crescendo(null, new Position(null, null, -1f, -2f));
    cursor = cursor.write(cresc);
    cursor = cursor.openBeam();
		cursor = cursor.write(firstSlurC = chord(fr(3, 16), accent, pi(A, -1, 4),
			pi(E, -1, 5), pi(F, 0, 5), pi(A, -1, 5)));
		cursor = cursor.write(lastSlurC = chord(f16, pi(G, 4), pi(G, 5)));
		cursor = cursor.closeBeam();
		cursor = cursor.write(cresc.getWedgeEnd());
		firstSlurB = new BezierPoint(sp(is*0.8f,is*7.6f), sp(is,is*0.8f));
		lastSlurB = new BezierPoint(sp(0,is*6f), sp(-is,is));
		CurvedLine curvedLine = new CurvedLine(CurvedLine.Type.Slur,
			clwp(firstSlurC, firstSlurB), clwp(lastSlurC, lastSlurB), null);
		cursor = cursor.withScore(writeCurvedLine(cursor.getScore(), curvedLine));

		//measure 3
		cursor = cursor.write(attachC = chord(f2, pi(D, 5), pi(F, 5), pi(G, 5), pi(D, 6)));
		cursor = cursor.withScore(
    	attachElement(cursor.getScore(), attachC, new Dynamics(DynamicsType.f)));
    cursor = cursor.write(new Rest(f2));

		//measure 4
    cursor = cursor.write(new Rest(f2));
    cursor = cursor.write(new Rest(f4));
    cresc = new Crescendo(null, new Position(null, null, -1f, -2f));
    cursor = cursor.write(cresc);
    cursor = cursor.openBeam();
    cursor = cursor.write(firstSlurC = chord(fr(3, 16),
    	accent, pi(A, -1, 4), pi(E, -1, 5), pi(F, 0, 5), pi(A, -1, 5)));
		cursor = cursor.write(lastSlurC = chord(f16, pi(G, 0, 4), pi(G, 0, 5)));
		cursor = cursor.closeBeam();
		cursor = cursor.write(cresc.getWedgeEnd());
		firstSlurB = new BezierPoint(sp(is*0.8f,is*7.6f), sp(is,is*0.8f));
		lastSlurB = new BezierPoint(sp(0,is*6f), sp(-is,is));
		curvedLine = new CurvedLine(CurvedLine.Type.Slur,
    	clwp(firstSlurC, firstSlurB), clwp(lastSlurC, lastSlurB), null);
		cursor = cursor.withScore(writeCurvedLine(cursor.getScore(), curvedLine));

		//measure 5
		cursor = cursor.write(attachC = chord(f4, staccato, pi(F, 5),
			pi(G, 5), pi(D, 6), pi(F, 6)));
		cursor = cursor.withScore(
			attachElement(cursor.getScore(), attachC, new Dynamics(DynamicsType.f)));
		cursor = cursor.write(new Rest(f4));
		cursor = cursor.write(new Rest(f2));
	
		//second staff: f-clef some notes
		cursor = new Cursor(cursor.getScore(), mp(1, 0, 0, _0), true);
		cursor = cursor.write(new Clef(ClefType.F));
	
		//measure 1
		cursor = cursor.openBeam();
		cursor = cursor.write(new Rest(f8));
		cursor = cursor.write(firstSlurC = chord(f16, pi(A, -1, 4)));
		cursor = cursor.write(chord(f16, pi(G, 0, 4)));
		cursor = cursor.closeBeam();
		
		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16, accent, pi(F, 0, 4)));
		cursor = cursor.write(chord(f16, pi(D, 0, 4)));;
		cursor = cursor.write(chord(f16, pi(E, -1, 4)));
		cursor = cursor.write(chord(f16, pi(D, 0, 4)));
		cursor = cursor.closeBeam();

		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16, accent, pi(B, 0, 3)));
		cursor = cursor.write(chord(f16, pi(G, 0, 3)));
		cursor = cursor.write(chord(f16, pi(A, -1, 3)));
		cursor = cursor.write(chord(f16, pi(G, 0, 3)));	
		cursor = cursor.closeBeam();

		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16, accent, pi(F, 0, 3)));
		cursor = cursor.write(chord(f16, pi(D, 0, 3)));
		cursor = cursor.write(chord(f16, pi(E, -1, 3)));
		cursor = cursor.write(chord(f16, pi(D, 0, 3)));
		cursor = cursor.closeBeam();

		//measure 2
		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16,accent, pi(B, 0, 2)));
		cursor = cursor.write(chord(f16, pi(G, 0, 2)));
		cursor = cursor.write(chord(f16, pi(A, -1, 2)));
		cursor = cursor.write(chord(f16, pi(G, 0, 2)));	
		cursor = cursor.closeBeam();

		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16,accent, pi(F, 0, 2)));
		cursor = cursor.write(chord(f16, pi(D, 0, 2)));
		cursor = cursor.write(chord(f16, pi(E, -1, 2)));
		cursor = cursor.write(chord(f16, pi(D, 0, 2)));
		cursor = cursor.closeBeam();

		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16,accent, pi(C, 0, 2)));
		cursor = cursor.write(chord(f16, pi(G, 0, 1)));
		cursor = cursor.write(chord(f16, pi(C, 0, 2)));
		cursor = cursor.write(chord(f16, pi(G, 0, 1)));
		cursor = cursor.closeBeam();

		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16,accent, pi(C, 0, 2)));
		cursor = cursor.write(chord(f16, pi(G, 0, 1)));
		cursor = cursor.write(chord(f16, pi(C, 0, 2)));
		cursor = cursor.write(lastSlurC = chord(f16, pi(G, 0, 1)));
		cursor = cursor.closeBeam();
		firstSlurB = new BezierPoint(sp(0,is*1.5f), sp(15,5));
		lastSlurB = new BezierPoint(sp(0,is*7.5f), sp(-is*5, is*2));
		curvedLine = new CurvedLine(CurvedLine.Type.Slur,
    	clwp(firstSlurC, firstSlurB), clwp(lastSlurC, lastSlurB), null);
		cursor = cursor.withScore(writeCurvedLine(cursor.getScore(), curvedLine));

		//measure 3
		cursor = cursor.write(chord(f8, staccato, pi(B,0,1)));
		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16, pi(A, -1, 4)));
		cursor = cursor.write(chord(f16, pi(G, 0, 4)));
		cursor = cursor.closeBeam();
		
		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16, accent, pi(F, 0, 4)));
		cursor = cursor.write(chord(f16, pi(D, 0, 4)));
		cursor = cursor.write(chord(f16, pi(E, -1, 4)));
		cursor = cursor.write(chord(f16, pi(D, 0, 4)));		
		cursor = cursor.closeBeam();

		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16,accent, pi(B, 0, 3)));
		cursor = cursor.write(chord(f16, pi(G, 0, 3)));
		cursor = cursor.write(chord(f16, pi(A, -1, 3)));
		cursor = cursor.write(chord(f16, pi(G, 0, 3)));		
		cursor = cursor.closeBeam();

		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16,accent, pi(F, 0, 3)));
		cursor = cursor.write(chord(f16, pi(D, 0, 3)));
		cursor = cursor.write(chord(f16, pi(E, -1, 3)));
		cursor = cursor.write(lastSlurC = chord(f16, pi(D, 0, 3)));	
		cursor = cursor.closeBeam();
		firstSlurB = new BezierPoint(sp(0,is*1.5f), sp(15,3));
		lastSlurB = new BezierPoint(sp(0,is*5f), sp(-is*5.5f, is*2));
		curvedLine = new CurvedLine(CurvedLine.Type.Slur,
    	clwp(firstSlurC, firstSlurB), clwp(lastSlurC, lastSlurB), null);
		cursor = cursor.withScore(writeCurvedLine(cursor.getScore(), curvedLine));

		//measure 4
		cursor = cursor.openBeam();
		cursor = cursor.write(firstSlurC = chord(f16,accent, pi(B, 0, 2)));
		cursor = cursor.write(chord(f16, pi(G, 0, 2)));
		cursor = cursor.write(chord(f16, pi(A, -1, 2)));
		cursor = cursor.write(chord(f16, pi(G, 0, 2)));
		cursor = cursor.closeBeam();

		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16,accent, pi(F, 0, 2)));
		cursor = cursor.write(chord(f16, pi(D, 0, 2)));
		cursor = cursor.write(chord(f16, pi(E, -1, 2)));
		cursor = cursor.write(chord(f16, pi(D, 0, 2)));	
		cursor = cursor.closeBeam();

		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16,accent, pi(C, 0, 2)));
		cursor = cursor.write(chord(f16, pi(G, 0, 1)));
		cursor = cursor.write(chord(f16, pi(C, 0, 2)));
		cursor = cursor.write(chord(f16, pi(G, 0, 1)));
		cursor = cursor.closeBeam();

		cursor = cursor.openBeam();
		cursor = cursor.write(chord(f16,accent, pi(C, 0, 2)));
		cursor = cursor.write(chord(f16, pi(G, 0, 1)));
		cursor = cursor.write(chord(f16, pi(C, 0, 2)));
		cursor = cursor.write(lastSlurC = chord(f16, pi(G, 0, 1)));
		cursor = cursor.closeBeam();
		firstSlurB = new BezierPoint(sp(-is,is*8.5f), sp(15,4));
		lastSlurB = new BezierPoint(sp(0, is*7.5f), sp(-is*5, is*2));
		curvedLine = new CurvedLine(CurvedLine.Type.Slur,
    	clwp(firstSlurC, firstSlurB), clwp(lastSlurC, lastSlurB), null);
		cursor = cursor.withScore(writeCurvedLine(cursor.getScore(), curvedLine));
	
		//measure 5
		cursor = cursor.write(chord(f4, staccato, pi(B, 0, 1)));
		cursor = cursor.write(new Rest(f4));
		cursor = cursor.write(new Rest(f2));
		
		//end line
		cursor = cursor.withScore(writeColumnEndBarline(cursor.getScore(), 4,
			Barline.createBarline(BarlineStyle.LightHeavy)));

		return cursor.getScore();
	}


	private static Chord chord(Fraction fraction, Pitch... pitches)
	{
		return chord(fraction, null, pitches);
	}


	private static Chord chord(Fraction fraction, Articulation[] articulations,
		Pitch... pitches)
	{
		return new Chord(Note.createNotes(pitches), fraction, null,
			articulations != null ? ivec(articulations) : null);
	}
	
	
	private static CurvedLineWaypoint clwp(Chord c, BezierPoint bezierPoint)
	{
		return new CurvedLineWaypoint(c, null, bezierPoint);
	}

	
}
