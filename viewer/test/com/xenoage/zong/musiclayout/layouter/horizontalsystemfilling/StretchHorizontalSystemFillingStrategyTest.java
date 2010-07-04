package com.xenoage.zong.musiclayout.layouter.horizontalsystemfilling;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.Delta;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.SystemArrangementTry;
import com.xenoage.zong.musiclayout.spacing.ColumnSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;


/**
 * Test cases for a StretchHorizontalSystemFillingStrategy.
 * 
 * @author Andreas Wenger
 */
public class StretchHorizontalSystemFillingStrategyTest
{
	
	
	/**
	 * Tests the applyFilling-method by creating an easy
	 * system and checking if the elements were
	 * stretched correctly.
	 */
	@Test public void applyFilling()
	{
		//create an easy system for testing
		float leadingWidth = 4;
		float offsetBeat1 = 3;
		float offsetBeat2 = 7;
		float offsetBeat3 = 12;
		SystemArrangement system = SystemArrangementTry.createSystemWith1HMeasure(leadingWidth, offsetBeat1,
			offsetBeat2, offsetBeat3);
		
		//stretch the system
		StretchHorizontalSystemFillingStrategy strategy =
			StretchHorizontalSystemFillingStrategy.getInstance();
		float newWidth = 20;
		system = strategy.computeSystemArrangement(system, newWidth);
		
		//compare the result
		//since the leading spacing (4 spaces) is not scaled, the
		//remaining 12 spaces of the voices width have to be scaled
		float stretch = (newWidth - leadingWidth) / offsetBeat3;
		ColumnSpacing newCol = system.getColumnSpacings().get(0);
		//beat offsets
		assertEquals(offsetBeat1 * stretch, newCol.getBeatOffsets().get(0).getOffsetMm(), Delta.DELTA_FLOAT);
		assertEquals(offsetBeat2 * stretch, newCol.getBeatOffsets().get(1).getOffsetMm(), Delta.DELTA_FLOAT);
		//element spacings
		VoiceSpacing newVoice = newCol.getMeasureSpacings().get(0).getVoiceSpacings().get(0);
		assertEquals(offsetBeat1 * stretch, newVoice.getSpacingElements().get(0).getOffset(), Delta.DELTA_FLOAT);
		assertEquals(offsetBeat2 * stretch, newVoice.getSpacingElements().get(1).getOffset(), Delta.DELTA_FLOAT);
		
	}


}
