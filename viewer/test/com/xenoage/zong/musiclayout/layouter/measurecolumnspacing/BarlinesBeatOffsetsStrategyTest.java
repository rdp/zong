package com.xenoage.zong.musiclayout.layouter.measurecolumnspacing;

import static com.xenoage.util.math.Fraction.fr;
import static org.junit.Assert.*;

import java.util.Arrays;
import org.junit.Test;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.zong.core.header.ColumnHeader;
import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.barline.BarlineStyle;
import com.xenoage.zong.musiclayout.spacing.horizontal.BeatOffset;


/**
 * Test cases for a {@link BarlinesBeatOffsetsStrategy}.
 * 
 * @author Andreas Wenger
 */
public class BarlinesBeatOffsetsStrategyTest
{
  

  @Test public void computeBeatOffsetsTest()
  {
    //notes:      |    1/4    1/4   ||   1/4    |   1/4    |
  	//barlines:   |:               :||:         |         :|
  	float d = 2; //original distance between notes
  	float is = 1.5f; //interline space
  	//create original offsets
  	PVector<BeatOffset> baseOffsets = new PVector<BeatOffset>();
  	baseOffsets = baseOffsets.plusAll(Arrays.asList(
  		new BeatOffset(fr(0, 4), 0 * d),
  		new BeatOffset(fr(1, 4), 1 * d),
  		new BeatOffset(fr(2, 4), 2 * d),
  		new BeatOffset(fr(3, 4), 3 * d),
  		new BeatOffset(fr(4, 4), 4 * d)));
  	//create barlines
  	ColumnHeader ch = ColumnHeader.empty();
  	ch = ch.withStartBarline(Barline.createForwardRepeatBarline(BarlineStyle.HeavyLight));
  	ch = ch.withMiddleBarline(Barline.createBothRepeatBarline(BarlineStyle.LightLight, 1), fr(2, 4));
  	ch = ch.withMiddleBarline(Barline.createRegularBarline(), fr(3, 4));
  	ch = ch.withEndBarline(Barline.createBackwardRepeatBarline(BarlineStyle.LightHeavy, 1));
  	//compute new offsets and check results
  	Tuple2<PVector<BeatOffset>, PVector<BeatOffset>> result =
  		new BarlinesBeatOffsetsStrategy().computeBeatOffsets(baseOffsets, ch, is);
  	float dRep = BarlinesBeatOffsetsStrategy.REPEAT_SPACE * is;
  	float dMid = BarlinesBeatOffsetsStrategy.MID_BARLINE_SPACE * is;
  	//note offsets
  	assertEquals(5, result.get1().size());
  	assertEquals(new BeatOffset(fr(0, 4), 0 * d + 1 * dRep), result.get1().get(0));
  	assertEquals(new BeatOffset(fr(1, 4), 1 * d + 1 * dRep), result.get1().get(1));
  	assertEquals(new BeatOffset(fr(2, 4), 2 * d + 3 * dRep + 1 * dMid), result.get1().get(2));
  	assertEquals(new BeatOffset(fr(3, 4), 3 * d + 3 * dRep + 2 * dMid), result.get1().get(3));
  	assertEquals(new BeatOffset(fr(4, 4), 4 * d + 3 * dRep + 2 * dMid), result.get1().get(4));
  	//barline offsets
  	assertEquals(4, result.get2().size());
  	assertEquals(new BeatOffset(fr(0, 4), 0 * d + 0 * dRep), result.get2().get(0));
  	assertEquals(new BeatOffset(fr(2, 4), 2 * d + 2 * dRep), result.get2().get(1));
  	assertEquals(new BeatOffset(fr(3, 4), 3 * d + 3 * dRep + 1 * dMid), result.get2().get(2));
  	assertEquals(new BeatOffset(fr(4, 4), 4 * d + 4 * dRep + 2 * dMid), result.get2().get(3));
  }

}
