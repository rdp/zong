package com.xenoage.zong.musiclayout;

import static com.xenoage.util.math.Fraction.fr;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.ChordData;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.Note;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.BeatOffset;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureLeadingSpacingMock;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;


/**
 * Test cases for a SystemArrangement
 * 
 * @author Andreas Wenger
 */
public class SystemArrangementTry
{
	
	
	/**
	 * Creates and returns a simple {@link SystemArrangement} with only one
	 * measure with a clef and two notes, using the given parameters.
	 * This method is useful if a system is needed where only
	 * horizontal spacing is important.
	 * @param leadingWidth  width of the leading spacing in mm
	 * @param offsetBeat1   offset of beat 1/4 in mm
	 * @param offsetBeat2   offset of beat 3/4 in mm
	 * @param offsetBeat3   width of the voice spacing in mm
	 */
	public static SystemArrangement createSystemWith1HMeasure(float leadingWidth,
		float offsetBeat1, float offsetBeat2, float offsetBeat3)
	{
		Measure measure = new Measure(null);
		Voice voice = measure.getVoices().get(0);
		voice.addVoiceElement(new Chord(new ChordData(new Note(new Pitch(0, 0, 4)), new Fraction(2, 4))));
		voice.addVoiceElement(new Chord(new ChordData(new Note(new Pitch(1, 0, 4)), new Fraction(2, 4))));
		BeatOffset[] beatOffsets = new BeatOffset[]{
			new BeatOffset(new Fraction(1, 4), offsetBeat1),
			new BeatOffset(new Fraction(3, 4), offsetBeat2),
			new BeatOffset(new Fraction(5, 4), offsetBeat3)
		};
		VoiceSpacing[] voiceSpacings = new VoiceSpacing[]{new VoiceSpacing(voice,
			new SpacingElement[]{
				new SpacingElement(null, null, offsetBeat1),
				new SpacingElement(null, null, offsetBeat2)
				})};
		MeasureSpacing measureSpacing = new MeasureSpacing(measure, voiceSpacings,
			MeasureLeadingSpacingMock.createGClefSpacing(leadingWidth));
		MeasureSpacing[] measureSpacings = new MeasureSpacing[]{measureSpacing};
		MeasureColumnSpacing mcs = new
			MeasureColumnSpacing(measureSpacings, beatOffsets,
				new BeatOffset[]{new BeatOffset(fr(0, 4), 0), new BeatOffset(fr(6, 4), offsetBeat3)});
		SystemArrangement system = new SystemArrangement(10, 10,
			new MeasureColumnSpacing[]{mcs},
			0, 0, leadingWidth + offsetBeat3, new float[1], new float[0], 0);
		return system;
	}
	
	
	/**
	 * Creates and returns a simple {@link SystemArrangement} using the
	 * given values.
	 */
	public static SystemArrangement createSystem(int stavesCount,
		float staffHeight, float staffDistance,	float offsetY)
	{
		float[] staffHeights = new float[stavesCount];
		for (int i = 0; i < stavesCount; i++)
			staffHeights[i] = staffHeight;
		float[] staffDistances = new float[stavesCount - 1];
		for (int i = 0; i < stavesCount - 1; i++)
			staffDistances[i] = staffDistance;
		return new SystemArrangement(-1, -1,
			new MeasureColumnSpacing[]{}, 0, 0, 0, staffHeights, staffDistances, offsetY);
	}

}
