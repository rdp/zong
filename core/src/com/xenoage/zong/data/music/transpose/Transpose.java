package com.xenoage.zong.data.music.transpose;

import com.xenoage.zong.data.music.NoVoiceElement;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Transposition change.
 * 
 * The chromatic value represents the number of half-tone steps that
 * must be added to the written pitch to get the correct sounding pitch.
 * 
 * @author Andreas Wenger
 */
public class Transpose
	extends NoVoiceElement
{

	private final int chromatic;


	public Transpose(int chromatic)
	{
		this.chromatic = chromatic;
	}


	/**
	 * Return this {@link Transpose}. No deep copy is needed since everything
	 * is final.
	 */
	@Override
	public Transpose deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}


	public int getChromatic()
	{
		return chromatic;
	}


	@Override
	public boolean equals(Object o)
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
