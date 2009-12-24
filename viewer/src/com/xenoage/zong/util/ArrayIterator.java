package com.xenoage.zong.util;

import java.util.NoSuchElementException;


/**
 * An iterator for an array.
 * 
 * @author Andreas Wenger
 */
public class ArrayIterator<T>
	implements java.util.Iterator<T>
{

	private T array[];
	private int pos = 0;


	public ArrayIterator(T array[])
	{
		this.array = array;
	}


	public boolean hasNext()
	{
		return pos < array.length;
	}


	public T next()
		throws NoSuchElementException
	{
		if (hasNext())
			return array[pos++];
		else
			throw new NoSuchElementException();
	}


	public void remove()
	{
		throw new UnsupportedOperationException();
	}
	
}
