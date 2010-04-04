package com.xenoage.zong.core.music.util;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.MP;


/**
 * This enumeration is used when searching elements
 * relative to a given beat.
 * 
 * <ul>
 *   <li>Before: ]-∞, beat[</li>
 *   <li>BeforeOrAt: ]-∞, beat]</li>
 *   <li>At: [beat, beat]</li>
 *   <li>AtOrAfter: [beat, +∞[</li>
 *   <li>After: ]beat, +∞[</li>
 * </ul>
 * 
 * @author Andreas Wenger
 */
public enum BeatInterval
{
	
	Before, BeforeOrAt, At, AtOrAfter, After;
	
	
	/**
	 * Returns true, if the given beat is in the given interval
	 * relative to the given reference beat.
	 */
	public boolean isInInterval(Fraction beat, Fraction referenceBeat)
	{
		int compare = beat.compareTo(referenceBeat);
		switch (this)
		{
			case Before:
				return compare < 0;
			case BeforeOrAt:
				return compare <= 0;
			case At:
				return compare == 0;
			case AtOrAfter:
				return compare >= 0;
			case After:
				return compare > 0;
		}
		return false;
	}
	
	
	/**
	 * Returns true, if the given {@link MP} is in the given interval
	 * relative to the given reference {@link MP}.
	 */
	public boolean isInInterval(MP mp, MP referenceMP)
	{
		int compare = mp.compareTo(referenceMP);
		switch (this)
		{
			case Before:
				return compare < 0;
			case BeforeOrAt:
				return compare <= 0;
			case At:
				return compare == 0;
			case AtOrAfter:
				return compare >= 0;
			case After:
				return compare > 0;
		}
		return false;
	}
	
	
	/**
	 * Returns true, if this interval is {@link #Before}, {@link #BeforeOrAt} or {@link #At}.
	 */
	public boolean isPrecedingOrAt()
	{
		return this == Before || this == BeforeOrAt || this == At;
	}
	

}
