package com.xenoage.zong.musiclayout.layouter.verticalframefilling;

import static com.xenoage.pdlib.PVector.pvec;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.xenoage.util.Delta;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.SystemArrangementTry;


/**
 * Test cases for a {@link FillPageVerticalFrameFillingStrategy}.
 * 
 * @author Andreas Wenger
 */
public class FillPageVerticalFrameFillingStrategyTest
{
	
	/**
	 * Tests the applyFilling-method by creating a frames
	 * with some systems and checking if the systems were
	 * positioned correctly.
	 */
	@Test public void applyFilling()
	{
		//create a simple frame for testing
		float usableHeight = 400;
		int stavesCount = 2;
		float staffHeight = 10;
		float staffDistance = 30;
		float offset1 = 0;
		float offset2 = 100;
		float offset3 = 200;
		SystemArrangement system1 = SystemArrangementTry.createSystem(
			stavesCount, staffHeight, staffDistance, offset1);
		SystemArrangement system2 = SystemArrangementTry.createSystem(
			stavesCount, staffHeight, staffDistance, offset2);
		SystemArrangement system3 = SystemArrangementTry.createSystem(
			stavesCount, staffHeight, staffDistance, offset3);
		
		FrameArrangement frame = new FrameArrangement(pvec(
			system1, system2, system3), new Size2f(10, usableHeight));
		
		//apply strategy
		FillPageVerticalFrameFillingStrategy strategy = FillPageVerticalFrameFillingStrategy.getInstance();
		frame = strategy.computeFrameArrangement(frame, null);
		
		//compare values
		//remaining space is usable height - offset3 - (height of last system)
		float remainingSpace = usableHeight - offset3 - system3.getHeight();
		//the last two systems are moved down, each remainingSpace/2
		float additionalSpace = remainingSpace / 2;
		//compare new offsets
		assertEquals(offset2 + 1 * additionalSpace, frame.getSystems().get(1).getOffsetY(), Delta.DELTA_FLOAT);
		assertEquals(offset3 + 2 * additionalSpace, frame.getSystems().get(2).getOffsetY(), Delta.DELTA_FLOAT);
		
	}

}
