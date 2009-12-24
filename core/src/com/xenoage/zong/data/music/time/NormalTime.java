package com.xenoage.zong.data.music.time;

import java.util.ArrayList;

import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * A normal time signature, that means, on that consists of a numerator and a
 * denominator, like 3/4 or 6/8.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class NormalTime
	extends Time
{

	private final int denominator;
	private final int numerator;


	public NormalTime(int numerator, int denominator)
	{
		this.numerator = numerator;
		this.denominator = denominator;
	}


	/**
	 * Return this {@link NormalTime}. No deep copy is needed since everything
	 * is final.
	 */
	@Override
	public NormalTime deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}


	@Override
	public Fraction getBeatsPerMeasure()
	{
		return new Fraction(numerator, denominator);
	}


	public int getDenominator()
	{
		return denominator;
	}


	public int getNumerator()
	{
		return numerator;
	}


	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		else if (o instanceof NormalTime)
		{
			return (((NormalTime) o).denominator == this.denominator && ((NormalTime) o).numerator == this.numerator);
		}
		else
		{
			return false;
		}
	}


	@Override
	public ArrayList<Tuple2<Fraction, BeatWeight>> getBeatWeights()
	{
		ArrayList<Tuple2<Fraction, BeatWeight>> list = new ArrayList<Tuple2<Fraction, BeatWeight>>();
		list.add(new Tuple2<Fraction, BeatWeight>(new Fraction(0, denominator),
			BeatWeight.StrongBeat));
		for (int i = 1; i < numerator; i++)
		{
			list.add(new Tuple2<Fraction, BeatWeight>(new Fraction(i, denominator),
				BeatWeight.WeakBeat));
		}
		return list;
	}


}
