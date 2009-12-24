package com.xenoage.zong.data;

import com.xenoage.util.math.Fraction;


/**
 * This class contains a position within
 * a score, which is defined by its
 * staff index, measure index, beat and
 * voice index.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class ScorePosition
	implements ScoreEntity
{

	private int staff;
	private int measure;
	private Fraction beat;
	private int voice;


	public ScorePosition(int staff, int measure, Fraction beat, int voice)
	{
		this.staff = staff;
		this.measure = measure;
		this.beat = beat;
		this.voice = voice;
	}


	public ScorePosition(int staff, int measure, int beatNumerator, int beatDenominator,
		int voice)
	{
		this.staff = staff;
		this.measure = measure;
		this.beat = new Fraction(beatNumerator, Math.max(beatDenominator, 1));
		this.voice = voice;
	}


	public static ScorePosition fromBeginning()
	{
		return new ScorePosition(0, 0, Fraction._0, 0);
	}


	/**
	 * Convenience method to create a {@link ScorePosition}
	 * (much shorter than </code>new ScorePosition(...)</code>).
	 */
	public static ScorePosition sp(int staff, int measure, Fraction beat, int voice)
	{
		return new ScorePosition(staff, measure, beat, voice);
	}


	public int getStaff()
	{
		return staff;
	}


	public int getMeasure()
	{
		return measure;
	}


	/**
	 * Gets the beat fraction.
	 */
	public Fraction getBeat()
	{
		return beat;
	}


	public int getVoice()
	{
		return voice;
	}


	@Override public String toString()
	{
		return "Staff = " + (staff != -1 ? staff : "unknown") + ", " + "Measure = "
			+ (measure != -1 ? measure : "unknown") + ", " + "Beat = "
			+ (beat != null ? beat.getNumerator() + "/" + beat.getDenominator() : "unknown")
			+ ", " + "Voice = " + (voice != -1 ? voice : "unknown");
	}

}
