package com.xenoage.util;


/**
 * Some useful functions dealing with {@code null} values.
 * 
 * @author Andreas Wenger
 */
public class NullTools
{

	
	/**
	 * Returns the given object, if not null, otherwise the given replacement.
	 */
	public static <T> T notNull(T object, T replacement)
	{
		return (object != null ? object : replacement);
	}
	
	
	/**
	 * Returns the given object, if not null, otherwise an empty string.
	 */
	public static String notNull(String s)
	{
		return (s != null ? s : "");
	}
	
	
	/**
	 * Throws an IllegalArgumentException if one of the given arguments is null.
	 */
	public static void throwNullArg(Object... o)
	{
		for (int i = 0; i < o.length; i++)
		{
			if (o[i] == null)
			{
				throw new IllegalArgumentException("Argument may not be null (checked argument with index " + i +")");
			}
		}
	}
	
	
}
