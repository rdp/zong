package com.xenoage.zong.io.midi.out;

import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.MP.mp0;
import static com.xenoage.zong.core.music.Pitch.pi;
import static com.xenoage.zong.core.music.barline.Barline.createBackwardRepeatBarline;
import static com.xenoage.zong.core.music.barline.Barline.createForwardRepeatBarline;
import static com.xenoage.zong.core.music.chord.ChordFactory.chord;

import com.xenoage.util.Range;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.ColumnElement;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.Part;
import com.xenoage.zong.core.music.barline.BarlineStyle;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.clef.ClefType;
import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.core.music.time.NormalTime;
import com.xenoage.zong.core.music.volta.Volta;
import com.xenoage.zong.io.score.ScoreController;
import com.xenoage.zong.io.score.selections.Cursor;


/**
 * @author Uli Teschemacher
 */
public class MidiRepetitionCalculatorTry
{

	public static Score createRepetitionDemoScore1()
	{
		Score score = Score.empty();
		Part pianoPart = new Part("", null, 1, null);
		score = score.plusPart(pianoPart);
		//measure 0
		Cursor cursor = new Cursor(score, mp0, true);
		cursor = cursor.write(new Clef(ClefType.G));
		cursor = cursor.write((ColumnElement) new TraditionalKey(-3));
		cursor = cursor.write(new NormalTime(3, 4));

		cursor = cursor.write(chord(pi('C', 0, 4), fr(1, 4)));
		cursor = cursor.write(createForwardRepeatBarline(BarlineStyle.HeavyLight));
		cursor = cursor.write(chord(pi('D', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('E', 0, 4), fr(1, 4)));

		//measure 1
		cursor = cursor.write(chord(pi('D', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('E', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('F', 0, 4), fr(1, 4)));
		cursor = cursor.withScore(ScoreController.writeColumnEndBarline(
			cursor.getScore(), 1, createBackwardRepeatBarline(BarlineStyle.LightHeavy, 2)));

		//measure 2
		cursor = cursor.withScore(ScoreController.writeColumnEndBarline(
			cursor.getScore(), 2, createForwardRepeatBarline(BarlineStyle.HeavyLight)));
		cursor = cursor.write(chord(pi('E', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('F', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('G', 0, 4), fr(1, 4)));

		//measure 3
		cursor = cursor.write(chord(pi('F', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('G', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('A', 0, 4), fr(1, 4)));

		//measure 4
		cursor = cursor.write(new Volta(1,new Range(1,2),null,true));
		cursor = cursor.write(chord(pi('B', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('A', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('F', 0, 4), fr(1, 4)));
		cursor = cursor.withScore(ScoreController.writeColumnEndBarline(
			cursor.getScore(), 4, createBackwardRepeatBarline(BarlineStyle.LightHeavy, 2)));

		//measure 5
		cursor = cursor.write(new Volta(1,new Range(3,3),null,true));
		cursor = cursor.write(chord(pi('E', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('D', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('C', 0, 4), fr(1, 4)));

		//measure 6
		cursor = cursor.write(new Volta(2,null,null,false));
		cursor = cursor.write(chord(pi('E', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('F', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('G', 0, 4), fr(1, 4)));

		//measure 7
		cursor = cursor.write(chord(pi('E', 0, 5), fr(1, 4)));
		cursor = cursor.write(chord(pi('F', 0, 5), fr(1, 4)));
		cursor = cursor.write(chord(pi('G', 0, 5), fr(1, 4)));

		//measure 8
		cursor = cursor.write(chord(pi('E', 0, 4), fr(1, 4)));
		cursor = cursor.write(chord(pi('F', 0, 5), fr(1, 4)));
		cursor = cursor.write(chord(pi('G', 0, 5), fr(1, 4)));

		return cursor.getScore();
	}
}
