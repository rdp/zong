package com.xenoage.zong.core.music.time;


/**
 * A 4/4 time signature with a C symbol.
 * 
 * @author Andreas Wenger
 */
public final class CommonTime
	extends NormalTime
{


	public CommonTime()
	{
		super(4, 4);
	}


	@Override public boolean equals(Object o)
	{
		return (o instanceof CommonTime);
	}


}
