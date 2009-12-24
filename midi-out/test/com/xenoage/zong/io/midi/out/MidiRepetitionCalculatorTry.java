/**
 * 
 */
package com.xenoage.zong.io.midi.out;

import com.xenoage.util.Range;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Part;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.Volta;
import com.xenoage.zong.data.music.barline.Barline;
import com.xenoage.zong.data.music.barline.BarlineStyle;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.key.TraditionalKey;
import com.xenoage.zong.data.music.time.NormalTime;


/**
 * @author Uli
 * 
 */
public class MidiRepetitionCalculatorTry
{

	public static Score createRepetitionDemoScore1()
	{
		Score ret = new Score();
		Part pianoPart = ret.addPart(0, 1);
		Staff staff;

		ret.addEmptyMeasures(9);
		staff = ret.getStaff(ret.getPartStartIndex(pianoPart));

		Measure measure = staff.getMeasures().get(0);
		measure.addNoVoiceElement(new Clef(ClefType.G));
		measure.addNoVoiceElement(new TraditionalKey(-3));
		measure.addNoVoiceElement(new NormalTime(3, 4));

		Voice voice;

		voice = measure.getVoices().get(0);
		voice.addNote(new Pitch('C', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('D', 0, 4), new Fraction(1, 4));
		ret.getScoreHeader().getMeasureColumnHeader(0).addMiddleBarline(Barline.createForwardRepeatBarline(BarlineStyle.HeavyLight), new Fraction(1,4));
		voice.addNote(new Pitch('E', 0, 4), new Fraction(1, 4));

		measure = staff.getMeasures().get(1);
		voice = measure.getVoices().get(0);
		voice.addNote(new Pitch('D', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('E', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('F', 0, 4), new Fraction(1, 4));
		ret.getScoreHeader().getMeasureColumnHeader(1).setEndBarline(Barline.createBackwardRepeatBarline(BarlineStyle.LightHeavy, 2));

		measure = staff.getMeasures().get(2);
		voice = measure.getVoices().get(0);
		ret.getScoreHeader().getMeasureColumnHeader(2).setStartBarline(Barline.createForwardRepeatBarline(BarlineStyle.HeavyLight));
		voice.addNote(new Pitch('E', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('F', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('G', 0, 4), new Fraction(1, 4));

		measure = staff.getMeasures().get(3);
		voice = measure.getVoices().get(0);
		voice.addNote(new Pitch('F', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('G', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('A', 0, 4), new Fraction(1, 4));

		measure = staff.getMeasures().get(4);
		voice = measure.getVoices().get(0);
		ret.getScoreHeader().getMeasureColumnHeader(4).setVolta(new Volta(1,new Range(1,2),null,true));
		voice.addNote(new Pitch('B', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('A', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('F', 0, 4), new Fraction(1, 4));
		ret.getScoreHeader().getMeasureColumnHeader(4).setEndBarline(Barline.createBackwardRepeatBarline(BarlineStyle.LightHeavy, 2));

		measure = staff.getMeasures().get(5);
		voice = measure.getVoices().get(0);
		ret.getScoreHeader().getMeasureColumnHeader(5).setVolta(new Volta(1,new Range(3,3),null,true));
		voice.addNote(new Pitch('E', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('D', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('C', 0, 4), new Fraction(1, 4));

		measure = staff.getMeasures().get(6);
		ret.getScoreHeader().getMeasureColumnHeader(6).setVolta(new Volta(2,null,null,false));
		voice = measure.getVoices().get(0);
		voice.addNote(new Pitch('E', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('F', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('G', 0, 4), new Fraction(1, 4));

		measure = staff.getMeasures().get(7);
		voice = measure.getVoices().get(0);
		voice.addNote(new Pitch('E', 0, 5), new Fraction(1, 4));
		voice.addNote(new Pitch('F', 0, 5), new Fraction(1, 4));
		voice.addNote(new Pitch('G', 0, 5), new Fraction(1, 4));

		measure = staff.getMeasures().get(8);
		voice = measure.getVoices().get(0);
		voice.addNote(new Pitch('E', 0, 4), new Fraction(1, 4));
		voice.addNote(new Pitch('F', 0, 5), new Fraction(1, 4));
		voice.addNote(new Pitch('G', 0, 5), new Fraction(1, 4));

		
		return ret;
	}
}
