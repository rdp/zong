package com.xenoage.zong.musiclayout;

import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.MP.atMeasure;
import static com.xenoage.zong.core.music.Pitch.pi;
import static com.xenoage.zong.core.music.chord.ChordFactory.chord;

import com.xenoage.pdlib.PVector;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.VoiceElement;
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
		Voice voice = new Voice(new PVector<VoiceElement>(
			chord(pi(0, 0, 4), fr(2, 4)),
			chord(pi(1, 0, 4), fr(2, 4))));
		BeatOffset[] beatOffsets = new BeatOffset[]{
			new BeatOffset(fr(1, 4), offsetBeat1),
			new BeatOffset(fr(3, 4), offsetBeat2),
			new BeatOffset(fr(5, 4), offsetBeat3)
		};
		VoiceSpacing[] voiceSpacings = new VoiceSpacing[]{new VoiceSpacing(voice, 1,
			new SpacingElement[]{
				new SpacingElement(null, null, offsetBeat1),
				new SpacingElement(null, null, offsetBeat2)
				})};
		MeasureSpacing measureSpacing = new MeasureSpacing(atMeasure(0, 0), voiceSpacings,
			MeasureLeadingSpacingMock.createGClefSpacing(leadingWidth));
		MeasureSpacing[] measureSpacings = new MeasureSpacing[]{measureSpacing};
		MeasureColumnSpacing mcs = new MeasureColumnSpacing(Score.empty(),
			measureSpacings, beatOffsets,
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
