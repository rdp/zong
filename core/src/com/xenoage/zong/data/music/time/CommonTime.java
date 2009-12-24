package com.xenoage.zong.data.music.time;

import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * A 4/4 time signature with a C symbol.
 * 
 * @author Andreas Wenger
 */
public class CommonTime
	extends NormalTime
{


	public CommonTime()
	{
		super(4, 4);
	}


	/**
	 * Return this {@link CommonTime}. No deep copy is needed since everything
	 * is final.
	 */
	@Override public CommonTime deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}


	@Override public boolean equals(Object o)
	{
		return (o instanceof CommonTime);
	}


}
