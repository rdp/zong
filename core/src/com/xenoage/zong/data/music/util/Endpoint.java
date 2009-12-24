package com.xenoage.zong.data.music.util;

import com.xenoage.util.math.Fraction;


/**
 * This enumeration is used together with a beat.
 * It tells, if the given beat belongs to an interval ending at
 * this beat or not.
 * 
 * <ul>
 *   <li>Before: ]∞, beat[</li>
 *   <li>AtOrBefore: ]∞, beat]</li>
 * </ul>
 * 
 * @author Andreas Wenger
 */
public enum Endpoint
{
	
	Before, AtOrBefore;
	
	
	/**
	 * Returns true, if the given beat is before (or at, dependent
	 * on the value of this Endpoint) the given end beat.
	 */
	public boolean isBeatInInterval(Fraction beat, Fraction endBeat)
	{
		if (this == Before)
		{
			return beat.compareTo(endBeat) < 0;
		}
		else if (this == AtOrBefore)
		{
			return beat.compareTo(endBeat) <= 0;
		}
		return false;
	}
	

}
