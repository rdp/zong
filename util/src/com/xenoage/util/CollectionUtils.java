package com.xenoage.util;

import java.util.ArrayList;
import java.util.HashMap;


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
	
	
	public static <T1, T2> HashMap<T1, T2> map()
	{
		return new HashMap<T1, T2>();
	}

}
