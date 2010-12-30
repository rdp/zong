package com.xenoage.zong.core.music.chord;

import com.xenoage.util.math.Fraction;


/**
 * Grace chord with slash (acciaccatura) or without (appoggiatura).
 * 
 * @author Andreas Wenger
 */
public final class Grace
{
	
	private final boolean slash;
	private final Fraction graceDuration;
	
	
	public Grace(boolean slash, Fraction graceDuration)
	{
		this.slash = slash;
		this.graceDuration = graceDuration;
	}

	
	public boolean isSlash()
	{
		return slash;
	}

	
	public Fraction getGraceDuration()
	{
		return graceDuration;
	}

}
