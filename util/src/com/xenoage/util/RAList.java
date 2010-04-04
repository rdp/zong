package com.xenoage.util;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * List based on {@link ArrayList}, but with no modification
 * methods. That means, this is a read-only list.
 * 
 * This is better than just using </code>Collections.unmodifiableList(...)</code>,
 * because trying to call a modification method leads to a compiler
 * error instead of a runtime error.
 * @deprecated Anyway, use the Vector class instead since it implements the
 * Collection interface.
 * 
 * @author Andreas
 */
public class RAList<T>
	implements Iterable<T>
{
	
	private final ArrayList<T> list;
	
	
	public RAList(ArrayList<T> list)
	{
		this.list = list;
	}
	
	
	public int size()
	{
		return list.size();
	}
	
	
	public T get(int index)
	{
		return list.get(index);
	}
	
	
	public Iterator<T> iterator()
	{
		return list.iterator();
	}
	

}
