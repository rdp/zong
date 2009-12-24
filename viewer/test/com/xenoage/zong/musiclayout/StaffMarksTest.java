package com.xenoage.zong.musiclayout;


import static com.xenoage.util.math.Fraction.fr;
import static org.junit.Assert.*;

import com.xenoage.util.Delta;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.ScorePosition;

import org.junit.Test;


/**
 * Test cases for a {@link StaffMarks} class.
 *
 * @author Andreas Wenger
 */
public class StaffMarksTest
{
  
	StaffMarks sm = new StaffMarks(0, 0, 0, new MeasureMarks[]{
		new MeasureMarks(0, 0, 25, new BeatMarker[]{new BeatMarker(5, fr(0, 4)), new BeatMarker(20, fr(1, 4))}),
		new MeasureMarks(35, 35, 55, new BeatMarker[]{new BeatMarker(40, fr(0, 4)), new BeatMarker(50, fr(1, 4))}),
		new MeasureMarks(55, 55, 75, new BeatMarker[]{new BeatMarker(60, fr(0, 4)), new BeatMarker(70, fr(3, 4))})
	});
  
  
  /**
   * Tests the getScorePositionAt method.
   */
  @Test public void getScorePositionAt()
  {
    ScorePosition sp;
    MeasureMarks[] mm = sm.getMeasureMarks();
    MeasureMarks lastMm = mm[mm.length - 1];
    //coordinate before first measure must return null
    assertNull(sm.getScorePositionAt(mm[0].getStartMm() - 1));
    //coordinate before first beat in measure 0 must return first beat
    sp = sm.getScorePositionAt((mm[0].getStartMm() + mm[0].getFirstBeatMarker().getXMm()) / 2);
    assertEquals(0, sp.getMeasure());
    assertEquals(mm[0].getBeatMarkers()[0].getBeat(), sp.getBeat());
    //coordinate after last measure must return null
    assertNull(sm.getScorePositionAt(lastMm.getEndMm() + 1));
    //coordinate after last beat in last measure must return last beat
    sp = sm.getScorePositionAt((lastMm.getLastBeatMarker().getXMm() + lastMm.getStartMm()) / 2);
    assertEquals(mm.length - 1, sp.getMeasure());
    assertEquals(lastMm.getLastBeatMarker().getBeat(), sp.getBeat());
    //coordinate at i-th x-position must return i-th beat
    for (int iMeasure = 0; iMeasure < mm.length; iMeasure++)
    {
    	BeatMarker[] bm = mm[iMeasure].getBeatMarkers();
	    for (int iBeat = 0; iBeat < bm.length; iBeat++)
	    {
	      sp = sm.getScorePositionAt(bm[iBeat].getXMm());
	      assertEquals(iMeasure, sp.getMeasure());
	      assertEquals(bm[iBeat].getBeat(), sp.getBeat());
	    }
    }
    //coordinate between beat 0 and 3 in measure 2 must return beat 3
    sp = sm.getScorePositionAt((mm[2].getBeatMarkers()[0].getXMm() + mm[2].getBeatMarkers()[1].getXMm()) / 2);
    assertEquals(2, sp.getMeasure());
    assertEquals(mm[2].getBeatMarkers()[1].getBeat(), sp.getBeat());
  }
  
  
  /**
   * Tests the getXMmAt method.
   */
  @Test public void getXMmAt()
  {
  	MeasureMarks[] mm = sm.getMeasureMarks();
  	MeasureMarks lastMm = mm[mm.length - 1];
  	//beat before first measure and after last measure must return null
  	assertNull(sm.getXMmAt(0 - 1, Fraction._0));
  	assertNull(sm.getXMmAt(mm.length, Fraction._0));
    //beat before first beat in measure 0 must return first beat
  	assertEquals(mm[0].getFirstBeatMarker().getXMm(),
  		sm.getXMmAt(0, mm[0].getFirstBeatMarker().getBeat().sub(fr(1, 4))), Delta.DELTA_FLOAT);
    //beat after last beat in last measure must return last beat
  	assertEquals(lastMm.getLastBeatMarker().getXMm(),
  		sm.getXMmAt(mm.length - 1, lastMm.getLastBeatMarker().getBeat().add(fr(1, 4))), Delta.DELTA_FLOAT);
    //i-th beat must return coordinate at i-th x-position
  	for (int iMeasure = 0; iMeasure < mm.length; iMeasure++)
    {
    	BeatMarker[] bm = mm[iMeasure].getBeatMarkers();
	    for (int iBeat = 0; iBeat < bm.length; iBeat++)
	    {
	      assertEquals(bm[iBeat].getXMm(), sm.getXMmAt(iMeasure, bm[iBeat].getBeat()), Delta.DELTA_FLOAT);
	    }
    }
  }
  

}
