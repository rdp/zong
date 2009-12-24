package com.xenoage.zong.data.music.util;

import java.util.LinkedList;

import com.xenoage.util.MathTools;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.Duration;
import com.xenoage.zong.data.music.time.NormalTime;
import com.xenoage.zong.data.music.time.Time;


/**
 * 
 * @author Uli Teschemacher
 */
public class DurationTools
{

	public static LinkedList<Duration> getDisplayableDurations(Fraction duration)
	{
		LinkedList<Duration> durations = new LinkedList<Duration>();
		Fraction remaining = duration;
		while (!remaining.equals(new Fraction(0)))
		{
			Fraction newFraction = getGreatestPossibleRest(remaining);
			durations.add(new Duration(newFraction));
			remaining = remaining.sub(newFraction);
		}
		return durations;
	}


	private static Fraction getGreatestPossibleRest(Fraction duration)
	{
		if (duration.getNumerator() == 1)
		{
			return duration;
		}
		int prime = MathTools.lowestPrimeNumber(duration.getDenominator());
		int newDenominator = (int) Math.pow(prime, Math.ceil(Math.log((float) duration
			.getDenominator()
			/ (float) duration.getNumerator())
			/ Math.log(prime)));
		return new Fraction(1, newDenominator);
	}


	private static boolean durationIsOk(Fraction startBeat, Fraction duration, NormalTime time)
	{
		float multiple = startBeat.divideBy(duration).toFloat();
		if (Math.floor(multiple) == multiple && startBeat.add(duration).toFloat() <= time.getBeatsPerMeasure().toFloat())
		{
			return true;
		}
		else
		{
			return false;
		}
	}


	public static LinkedList<Duration> getDisplayableDurations(Fraction duration,
		Fraction startBeat, Time time)
	{
		if (time instanceof NormalTime)
		{
			NormalTime normalTime = (NormalTime) time;
			LinkedList<Duration> durations = new LinkedList<Duration>();
			Fraction currentBeat = startBeat;
			Fraction remaining = duration;
			while (!remaining.equals(new Fraction(0)))
			{
				Fraction fraction = getGreatestPosibbleFractionOnBeat(currentBeat,
					remaining, normalTime);
				durations.add(new Duration(fraction));
				remaining = remaining.sub(fraction);
				currentBeat = currentBeat.add(fraction);
				if (currentBeat.equals(normalTime.getDuration()))
				{
					currentBeat = new Fraction(0);
				}
			}
			return durations;
		}
		else
		{
			return getDisplayableDurations(duration);
		}
	}


	private static Fraction getGreatestPosibbleFractionOnBeat(Fraction beat,
		Fraction remaining, NormalTime time)
	{
		if (beat.equals(time.getDuration()))
		{
			return null;
		}
		Fraction fraction = new Fraction(1, beat.getDenominator());
		if (beat.add(fraction).toFloat() > time.getBeatsPerMeasure().toFloat())
		{
			// TODO think about it...
			fraction = getGreatestPossibleRest(time.getBeatsPerMeasure().sub(beat));
		}
		else if (fraction.toFloat() > remaining.toFloat())
		{
			// TODO think about it...
			fraction = getGreatestPossibleRest(remaining);
			
		}
		return fraction;
	}
}
