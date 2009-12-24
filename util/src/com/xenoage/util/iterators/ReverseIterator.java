package com.xenoage.util.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;


/**
 * Iterable iterator around a {@link List} with {@link RandomAccess},
 * that begins with the last element and ends with the first one,
 * allowing no modifications.
 * It can be used within a foreach statement.
 * 
 * @author Andreas Wenger
 */
public final class ReverseIterator<T>
	implements Iterator<T>, Iterable<T>
{
	
	private final List<T> list;
	private int currentIndex;
	

	/**
	 * Creates a new {@link ReverseIterator} for the given {@link List}.
	 * If null is given, a valid iterator with no elements is returned.
	 */
	public ReverseIterator(List<T> list)
	{
		if (list != null)
		{
			if (!(list instanceof RandomAccess))
				throw new IllegalArgumentException("Use this iterator only for RandomAccess lists!");
			this.list = list;
		}
		else
		{
			this.list = new ArrayList<T>(0);
		}
		this.currentIndex = this.list.size();
	}


	public boolean hasNext()
	{
		return (this.currentIndex > 0);
	}


	public T next()
		throws NoSuchElementException
	{
		if (hasNext())
		{
			this.currentIndex--;
			return list.get(this.currentIndex);
		}
		else
		{
			throw new NoSuchElementException();
		}
	}


	public void remove()
	{
		throw new UnsupportedOperationException();
	}
	
	
	public Iterator<T> iterator()
	{
    return this;
	}

	
}
