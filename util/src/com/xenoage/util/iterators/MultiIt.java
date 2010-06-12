package com.xenoage.util.iterators;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Iterator for multiple collections.
 * They are iterated one after the other.
 * 
 * @author Andreas Wenger
 */
@SuppressWarnings("unchecked")
public class MultiIt<T>
	implements Iterator<T>, Iterable<T>
{
	
	private final Collection[] collections;
	
	private Iterator<T> currentIterator = null;
	private int index = 0;
	
	
	public MultiIt(Collection... collections)
	{
		if (collections.length == 0)
			throw new IllegalArgumentException("At least one collection must be given");
		this.collections = collections;
		this.currentIterator = collections[0].iterator();
	}
	
	
	public static MultiIt multiIt(Collection... collections)
	{
		return new MultiIt(collections);
	}
	

	public boolean hasNext()
	{
		if (!currentIterator.hasNext())
		{
			if (index + 1 < collections.length)
			{
				index++;
				currentIterator = collections[index].iterator();
				return hasNext();
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}

	
	public T next()
	{
		if (!currentIterator.hasNext())
		{
			if (index + 1 < collections.length)
			{
				index++;
				currentIterator = collections[index].iterator();
				return (T) next();
			}
			else
			{
				throw new NoSuchElementException();
			}
		}
		else
		{
			return currentIterator.next();
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
