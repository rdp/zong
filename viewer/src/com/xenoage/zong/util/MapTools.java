package com.xenoage.zong.util;

import com.xenoage.util.lang.Tuple2;


/**
 * Useful methods to map values.
 * 
 * @author Andreas Wenger
 */
public class MapTools
{
	
	
	/**
	 * This methods compares the given object with the first components
	 * of the given map. The second component of the first value that
	 * equals the given object is returned, otherwise null.
	 * For example, a translation can be done in this way:
	 * <code>map(myWord, t("chair", "Stuhl"), t("table", "Tisch"))</code>.
	 */
	public static <T1, T2> T2 map(T1 object, Tuple2<T1, T2>... map)
	{
		for (Tuple2<T1, T2> pair : map)
		{
			if (pair.get1().equals(object))
				return pair.get2();
		}
		return null;
	}
	

}
