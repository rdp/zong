package com.xenoage.zong.core.music.util;

import com.xenoage.zong.core.music.MP;


/**
 * Musically positioned element.
 * 
 * This is a wrapper class to combine any object with
 * the {@link MP} it belongs to.
 * 
 * @author Andreas Wenger
 */
public class MPE<T>
{
	
	protected final T element;
	protected final MP mp;
	
	
	public MPE(T element, MP position)
	{
		this.element = element;
		this.mp = position;
	}
	
	
	public static <T2> MPE<T2> mpe(T2 element, MP position)
	{
		return new MPE<T2>(element, position);
	}
	
	
	public MP getMP()
	{
		return mp;
	}
	
	
	public T getElement()
	{
		return element;
	}
	
	
	/**
	 * Returns the latest of the given elements. If none are given or
	 * only null items are given, null is returned.
	 */
	public static <T2> MPE<T2> selectLatest(MPE<T2>... elements)
	{
		MPE<T2> ret = null;
		for (MPE<T2> element : elements)
		{
			if (element != null)
			{
				if (ret != null)
				{
					if (element.mp.compareTo(ret.mp) > 0)
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
		return element.toString() + " at MP " + mp.toString();
	}
	

}
