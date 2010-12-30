package com.xenoage.zong.core.music;

import com.xenoage.util.math.Fraction;


/**
 * Musical position.
 * 
 * This class contains a position within a score, which is defined by its
 * staff index, measure index, voice index and beat.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class MP
	implements Comparable<MP>
{

	private static final int UNKNOWN = -1;
	private static final Fraction UNKNOWNBEAT = null;
	
	private final int staff;
	private final int measure;
	private final int voice;
	private final Fraction beat;
	
	public static final MP mp0 = new MP(0, 0, 0, Fraction._0);


	/**
	 * Creates a new musical position at the given staff, measure, voice and beat.
	 */
	public static MP mp(int staff, int measure, int voice, Fraction beat)
	{
		return new MP(staff, measure, voice, beat);
	}
	
	
	/**
	 * Creates a new musical position with the given measure and the
	 * other values set to unknown.
	 */
	public static MP atMeasure(int measure)
	{
		return new MP(UNKNOWN, measure, UNKNOWN, UNKNOWNBEAT);
	}
	
	
	/**
	 * Creates a new musical position with the given staff.
	 */
	public static MP atStaff(int staff)
	{
		return new MP(staff, UNKNOWN, UNKNOWN, UNKNOWNBEAT);
	}
	
	
	/**
	 * Creates a new musical position with the given staff and measure.
	 */
	public static MP atMeasure(int staff, int measure)
	{
		return new MP(staff, measure, UNKNOWN, UNKNOWNBEAT);
	}
	
	
	/**
	 * Creates a new musical position with the given beat and the
	 * other values set to unknown.
	 */
	public static MP atBeat(Fraction beat)
	{
		return new MP(UNKNOWN, UNKNOWN, UNKNOWN, beat);
	}
	
	
	/**
	 * Creates a new musical position with the given voice and the
	 * other values set to unknown.
	 */
	public static MP atVoice(int voice)
	{
		return new MP(UNKNOWN, UNKNOWN, voice, UNKNOWNBEAT);
	}
	
	
	/**
	 * Creates a new musical position with the given staff, measure
	 * and voice.
	 */
	public static MP atVoice(int staff, int measure, int voice)
	{
		return new MP(staff, measure, voice, UNKNOWNBEAT);
	}
	
	
	/**
	 * Returns a new musical position based on this one, with only
	 * the beat changed.
	 */
	public MP withBeat(Fraction beat)
	{
		return new MP(staff, measure, voice, beat);
	}
	
	
	/**
	 * Returns a new musical position based on this one, with only
	 * the voice changed.
	 */
	public MP withVoice(int voice)
	{
		return new MP(staff, measure, voice, beat);
	}
	
	
	/**
	 * Returns a new musical position based on this one, with only
	 * the measure changed.
	 */
	public MP withMeasure(int measure)
	{
		return new MP(staff, measure, voice, beat);
	}
	
	
	/**
	 * Returns a new musical position based on this one, with only
	 * the staff changed.
	 */
	public MP withStaff(int staff)
	{
		return new MP(staff, measure, voice, beat);
	}
	
	
	private MP(int staff, int measure, int voice, Fraction beat)
	{
		this.staff = staff;
		this.measure = measure;
		this.beat = beat;
		this.voice = voice;
	}
	
	
	public boolean equalsVoice(MP mp)
	{
		return staff == mp.staff && measure == mp.measure && voice == mp.voice;
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
	
	
	public boolean isMeasureUnknown()
	{
		return measure == UNKNOWN;
	}
	
	
	public boolean isStaffOrMeasureUnknown()
	{
		return staff == UNKNOWN || measure == UNKNOWN;
	}
	
	
	public boolean isStaffOrMeasureOrVoiceUnknown()
	{
		return staff == UNKNOWN || measure == UNKNOWN || voice == UNKNOWN;
	}
	
	
	public boolean isStaffOrMeasureOrVoiceOrBeatUnknown()
	{
		return staff == UNKNOWN || measure == UNKNOWN ||
			beat == UNKNOWNBEAT || voice == UNKNOWN;
	}
	
	
	@Override public int hashCode()
	{
		return staff * 100000000 + measure * 1000000 + voice * 10000 +
			beat.getNumerator() * 100 + beat.getDenominator();
	}
	
	
	@Override public boolean equals(Object o)
	{
		if (o instanceof MP)
		{
			MP mp = (MP) o;
			return (staff == mp.staff && measure == mp.measure && voice == mp.voice &&
				beat.equals(mp.beat));
		}
		return false;
	}


	@Override public String toString()
	{
		return "Staff = " + (staff != UNKNOWN ? staff : "unknown") + ", " + "Measure = "
			+ (measure != UNKNOWN ? measure : "unknown") + ", " +
			"Voice = " + (voice != UNKNOWN ? voice : "unknown") + ", " +
			"Beat = " + (beat != UNKNOWNBEAT ? beat.getNumerator() + "/" +
				beat.getDenominator() : "unknown");
	}
	
	
	/**
	 * Compares this {@link MP} with the given one.
	 * It is compared by staff, measure, voice and beat.
	 */
  @Override public int compareTo(MP mp)
  {
    if (staff < mp.staff)
    {
    	return -1;
    }
    else if (staff > mp.staff)
    {
    	return 1;
    }
    else
    {
    	if (measure < mp.measure)
      {
      	return -1;
      }
      else if (measure > mp.measure)
      {
      	return 1;
      }
      else
      {
      	if (voice < mp.voice)
        {
        	return -1;
        }
        else if (voice > mp.voice)
        {
        	return 1;
        }
        else
        {
        	return beat.compareTo(mp.beat);
        }
      }
    }
  }

}
