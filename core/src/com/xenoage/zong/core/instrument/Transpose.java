package com.xenoage.zong.core.instrument;


/**
 * Transposition of an instrument.
 * 
 * Like in MusicXML (following documentation copied), a transposition
 * contains the following data:
 * 
 * The transposition is represented by chromatic steps
 * (required) and three optional elements: diatonic pitch
 * steps, octave changes, and doubling an octave down. The
 * chromatic and octave-change elements are numeric values
 * added to the encoded pitch data to create the sounding
 * pitch. The diatonic element is also numeric and allows
 * for correct spelling of enharmonic transpositions.
 * 
 * @author Andreas Wenger
 */
public final class Transpose
{
	
	private static final Transpose instanceNone = new Transpose(0, 0, 0, false);

	private final int chromatic;
	private final Integer diatonic; //null means default
	private final int octaveChange;
	private final boolean doubleOctaveDown;


	/**
	 * Creates a new {@link Transpose}.
	 * @param chromatic         number of chromatic steps to add to the pitch
	 * @param diatonic          number of diatonic steps, or null for default
	 * @param octaveChange      octave change (like -2 for 2 octaves down)
	 * @param doubleOctaveDown  copy pitch one octave down
	 */
	public Transpose(int chromatic, Integer diatonic, int octaveChange,
		boolean doubleOctaveDown)
	{
		this.chromatic = chromatic;
		this.diatonic = diatonic;
		this.octaveChange = octaveChange;
		this.doubleOctaveDown = doubleOctaveDown;
	}
	
	
	/**
	 * No transposition.
	 */
	public static Transpose none()
	{
		return instanceNone;
	}


	public int getChromatic()
	{
		return chromatic;
	}
	
	
	public Integer getDiatonic()
	{
		return diatonic;
	}
	
	
	public int getOctaveChange()
	{
		return octaveChange;
	}
	
	
	public boolean getDoubleOctaveDown()
	{
		return doubleOctaveDown;
	}


	@Override public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		else if (o instanceof Transpose)
		{
			return ((Transpose) o).chromatic == this.chromatic;
		}
		else
		{
			return false;
		}
	}

	
}
