/**
 * 
 */
package com.xenoage.zong.data.music;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.*;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.time.NormalTime;
import com.xenoage.zong.data.music.util.DurationTools;


/**
 * @author Uli Teschemacher
 * 
 */
public class DurationToolsTest
{

	@Test
	public void getDisplayableDurationsTest()
	{
		LinkedList<Duration> durations;
		Fraction frac;

		frac = new Fraction(5, 8);
		durations = DurationTools.getDisplayableDurations(frac);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(1, 2))));
		assertTrue(durations.get(1).equals(new Duration(new Fraction(1, 8))));

		frac = new Fraction(623, 512);
		durations = DurationTools.getDisplayableDurations(frac);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(1, 1))));
		assertTrue(durations.get(1).equals(new Duration(new Fraction(1, 8))));
		assertTrue(durations.get(2).equals(new Duration(new Fraction(1, 16))));
		assertTrue(durations.get(3).equals(new Duration(new Fraction(1, 64))));
		assertTrue(durations.get(4).equals(new Duration(new Fraction(1, 128))));
		assertTrue(durations.get(5).equals(new Duration(new Fraction(1, 256))));
		assertTrue(durations.get(6).equals(new Duration(new Fraction(1, 512))));

		frac = new Fraction(5, 6);
		durations = DurationTools.getDisplayableDurations(frac);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(1, 2))));
		assertTrue(durations.get(1).equals(new Duration(new Fraction(1, 3))));
	}
	
	@Test
	public void getDisplayableDurationsTest2()
	{
		LinkedList<Duration> durations;
		Fraction duration;
		NormalTime time;
		Fraction startBeat;

		time = new NormalTime(4,4);
		startBeat= new Fraction(1,4);
		duration = new Fraction(2, 4);
		durations = DurationTools.getDisplayableDurations(duration, startBeat, time);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(1, 4))));
		assertTrue(durations.get(1).equals(new Duration(new Fraction(1, 4))));
		
		time = new NormalTime(4,4);
		startBeat= new Fraction(1,4);
		duration = new Fraction(2, 4);
		durations = DurationTools.getDisplayableDurations(duration, startBeat, time);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(1, 4))));
		assertTrue(durations.get(1).equals(new Duration(new Fraction(1, 4))));
		
		time = new NormalTime(4,4);
		startBeat= new Fraction(3,8);
		duration = new Fraction(2, 4);
		durations = DurationTools.getDisplayableDurations(duration, startBeat, time);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(1, 8))));
		assertTrue(durations.get(1).equals(new Duration(new Fraction(1, 4))));
		assertTrue(durations.get(2).equals(new Duration(new Fraction(1, 8))));
		
		time = new NormalTime(4,4);
		startBeat= new Fraction(0,4);
		duration = new Fraction(2, 4);
		durations = DurationTools.getDisplayableDurations(duration, startBeat, time);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(2, 4))));
		
		time = new NormalTime(4,4);
		startBeat= new Fraction(0,4);
		duration = new Fraction(3, 4);
		durations = DurationTools.getDisplayableDurations(duration, startBeat, time);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(1, 2))));
		assertTrue(durations.get(1).equals(new Duration(new Fraction(1, 4))));
		
		time = new NormalTime(4,4);
		startBeat= new Fraction(1,4);
		duration = new Fraction(5, 12);
		durations = DurationTools.getDisplayableDurations(duration, startBeat, time);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(1, 4))));
		assertTrue(durations.get(1).equals(new Duration(new Fraction(1, 6))));
		
		/*
		time = new NormalTime(4,4);
		startBeat= new Fraction(2,6);
		duration = new Fraction(5, 12);
		durations = DurationTools.getDisplayableDurations(duration, startBeat, time);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(1, 3))));
		assertTrue(durations.get(1).equals(new Duration(new Fraction(1, 12))));
		
		
		time = new NormalTime(4,4);
		startBeat= new Fraction(1,6);
		duration = new Fraction(7, 12);
		durations = DurationTools.getDisplayableDurations(duration, startBeat, time);
		assertTrue(durations.get(0).equals(new Duration(new Fraction(1, 6))));
		assertTrue(durations.get(1).equals(new Duration(new Fraction(1, 6))));
		assertTrue(durations.get(2).equals(new Duration(new Fraction(1, 4))));
		*/
		
	}
}


