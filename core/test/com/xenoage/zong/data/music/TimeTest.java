/**
 * 
 */
package com.xenoage.zong.data.music;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.time.BeatWeight;
import com.xenoage.zong.data.music.time.NormalTime;
import com.xenoage.zong.data.music.time.SenzaMisura;
import com.xenoage.zong.data.music.time.Time;


/**
 * @author Uli
 *
 */
public class TimeTest
{
	@Test
	public void getBeatWeightsTest()
	{
		Time time = new NormalTime(4,4);
		ArrayList<Tuple2<Fraction,BeatWeight>> beatWeights = time.getBeatWeights();
		
		assertEquals(BeatWeight.StrongBeat, beatWeights.get(0).get2());
		assertEquals(BeatWeight.WeakBeat, beatWeights.get(1).get2());
		assertEquals(BeatWeight.WeakBeat, beatWeights.get(2).get2());
		assertEquals(BeatWeight.WeakBeat, beatWeights.get(3).get2());
		
		
		time = new NormalTime(3,8);
		beatWeights = time.getBeatWeights();
		
		assertEquals(BeatWeight.StrongBeat, beatWeights.get(0).get2());
		assertEquals(BeatWeight.WeakBeat, beatWeights.get(1).get2());
		assertEquals(BeatWeight.WeakBeat, beatWeights.get(2).get2());
		
		
		time = new SenzaMisura();
		beatWeights = time.getBeatWeights();
		
		assertEquals(0, beatWeights.size());
	}
}
