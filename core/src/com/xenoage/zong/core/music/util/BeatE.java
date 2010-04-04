package com.xenoage.zong.core.music.util;

import com.xenoage.util.math.Fraction;


/**
 * Element at a beat.
 * 
 * This is a wrapper class to combine any object with
 * the beat (represented as a {@link Fraction}) it belongs to.
 * 
 * @author Andreas Wenger
 */
public class BeatE<T>
{
	
	protected final T element;
	protected final Fraction beat;
	
	
	public BeatE(T element, Fraction beat)
	{
		this.element = element;
		this.beat = beat;
	}
	
	
	public static <T2> BeatE<T2> beate(T2 element, Fraction beat)
	{
		return new BeatE<T2>(element, beat);
	}
	
	
	public Fraction getBeat()
	{
		return beat;
	}
	
	
	public T getElement()
	{
		return element;
	}
	
	
	/**
	 * Returns the latest of the given elements. If none are given or
	 * only null items are given, null is returned.
	 */
	public static <T2> BeatE<T2> selectLatest(BeatE<T2>... elements)
	{
		BeatE<T2> ret = null;
		for (BeatE<T2> element : elements)
		{
			if (element != null)
			{
				if (ret != null)
				{
					if (element.beat.compareTo(ret.beat) > 0)
					{
						ret = element;
					}
				}
				else
				{
					ret = element;
				}
			}
		}
		return ret;
	}
	
	
	@Override public String toString()
	{
		return element.toString() + " at beat " + beat.toString();
	}
	

}
