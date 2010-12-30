package com.xenoage.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Useful methods for working with collections.
 * 
 * @author Andreas Wenger
 */
public final class CollectionUtils
{
	
	
	public static <T> ArrayList<T> alist()
	{
		return new ArrayList<T>();
	}
	
	
	public static <T> ArrayList<T> alist(int initialCapacity)
	{
		return new ArrayList<T>(initialCapacity);
	}
	
	
	public static <T> ArrayList<T> alistInit(T value, int size)
	{
		ArrayList<T> ret = new ArrayList<T>(size);
		for (int i = 0; i < size; i++)
			ret.add(value);
		return ret;
	}
	
	
	public static <T1, T2> HashMap<T1, T2> map()
	{
		return new HashMap<T1, T2>();
	}
	
	
	public static <T> LinkedList<T> llist()
	{
		return new LinkedList<T>();
	}

}
