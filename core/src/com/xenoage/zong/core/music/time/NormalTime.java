package com.xenoage.zong.core.music.time;

import static com.xenoage.util.math.Fraction.fr;

import java.util.ArrayList;

import com.xenoage.pdlib.IVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;


/**
 * A normal time signature, that means, on that consists of a numerator and a
 * denominator, like 3/4 or 6/8.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class NormalTime
	implements Time
{

	private final int denominator;
	private final int numerator;
	private final Vector<Tuple2<Fraction, BeatWeight>> beatWeights;


	public NormalTime(int numerator, int denominator)
	{
		this.numerator = numerator;
		this.denominator = denominator;
		//beat weights
		ArrayList<Tuple2<Fraction, BeatWeight>> beatWeights = new ArrayList<Tuple2<Fraction, BeatWeight>>();
		beatWeights.add(new Tuple2<Fraction, BeatWeight>(fr(0, denominator),
			BeatWeight.StrongBeat));
		for (int i = 1; i < numerator; i++)
		{
			beatWeights.add(new Tuple2<Fraction, BeatWeight>(fr(i, denominator),
				BeatWeight.WeakBeat));
		}
		this.beatWeights = new IVector<Tuple2<Fraction,BeatWeight>>(beatWeights);
	}


	@Override public Fraction getBeatsPerMeasure()
	{
		return fr(numerator, denominator);
	}


	public int getDenominator()
	{
		return denominator;
	}


	public int getNumerator()
	{
		return numerator;
	}


	@Override public boolean equals(Object o)
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


	@Override public Vector<Tuple2<Fraction, BeatWeight>> getBeatWeights()
	{
		return beatWeights;
	}


}
