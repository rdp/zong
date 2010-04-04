package com.xenoage.zong.musiclayout;

import static com.xenoage.util.math.Fraction.fr;
import static org.junit.Assert.*;

import com.xenoage.util.Delta;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.MP;

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
    MP mp;
    MeasureMarks[] mm = sm.getMeasureMarks();
    MeasureMarks lastMm = mm[mm.length - 1];
    //coordinate before first measure must return null
    assertNull(sm.getMPAt(mm[0].getStartMm() - 1));
    //coordinate before first beat in measure 0 must return first beat
    mp = sm.getMPAt((mm[0].getStartMm() + mm[0].getFirstBeatMarker().getXMm()) / 2);
    assertEquals(0, mp.getMeasure());
    assertEquals(mm[0].getBeatMarkers()[0].getBeat(), mp.getBeat());
    //coordinate after last measure must return null
    assertNull(sm.getMPAt(lastMm.getEndMm() + 1));
    //coordinate after last beat in last measure must return last beat
    mp = sm.getMPAt((lastMm.getLastBeatMarker().getXMm() + lastMm.getStartMm()) / 2);
    assertEquals(mm.length - 1, mp.getMeasure());
    assertEquals(lastMm.getLastBeatMarker().getBeat(), mp.getBeat());
    //coordinate at i-th x-position must return i-th beat
    for (int iMeasure = 0; iMeasure < mm.length; iMeasure++)
    {
    	BeatMarker[] bm = mm[iMeasure].getBeatMarkers();
	    for (int iBeat = 0; iBeat < bm.length; iBeat++)
	    {
	      mp = sm.getMPAt(bm[iBeat].getXMm());
	      assertEquals(iMeasure, mp.getMeasure());
	      assertEquals(bm[iBeat].getBeat(), mp.getBeat());
	    }
    }
    //coordinate between beat 0 and 3 in measure 2 must return beat 3
    mp = sm.getMPAt((mm[2].getBeatMarkers()[0].getXMm() + mm[2].getBeatMarkers()[1].getXMm()) / 2);
    assertEquals(2, mp.getMeasure());
    assertEquals(mm[2].getBeatMarkers()[1].getBeat(), mp.getBeat());
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
