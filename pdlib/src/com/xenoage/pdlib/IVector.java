package com.xenoage.pdlib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.xenoage.pdlib.It;


/**
 * Immutable vector.
 * 
 * A very simple collection containing an array with a fixed size,
 * that is immutable.
 * 
 * If a persistent vector with producers is needed, use
 * {@link PVector} instead.
 * 
 * Deprecated annotations are used to warn the programmer of calling
 * unsupported methods.
 * 
 * @author Andreas Wenger
 */
public final class IVector<T>
	implements Vector<T>
{
	
	private final ArrayList<T> array;
	private final int hashCode;
	
	
	public IVector(Collection<T> data)
	{
		array = new ArrayList<T>(data.size());
		int hashCode = 0;
		for (T e : data)
		{
			array.add(e);
			hashCode += array.hashCode();
		}
		this.hashCode = hashCode;
	}
	
	
	public IVector()
	{
		array = new ArrayList<T>(0);
		this.hashCode = 0;
	}
	
	
	public static <T2> IVector<T2> ivec(List<T2> data)
	{
		return new IVector<T2>(data);
	}
	
	
	public static <T2> IVector<T2> ivec(T2... data)
	{
		ArrayList<T2> array = new ArrayList<T2>(data.length);
		for (T2 o : data)
			array.add(o);
		return new IVector<T2>(array);
	}
	
	
	@Override public T getFirst()
	{
		return array.get(0);
	}
	
	
	@Override public T getLast()
	{
		return array.get(array.size() - 1);
	}

	
	@Deprecated @Override public boolean add(T e)
	{
		throw new UnsupportedOperationException();
	}

	
	@Deprecated @Override public void add(int index, T element)
	{
		throw new UnsupportedOperationException();
	}

	
	@Deprecated @Override public boolean addAll(Collection<? extends T> c)
	{
		throw new UnsupportedOperationException();
	}
	
	
	@Deprecated @Override public boolean addAll(int index, Collection<? extends T> c)
	{
		throw new UnsupportedOperationException();
	}
	

	@Deprecated @Override public void clear()
	{
		throw new UnsupportedOperationException();
	}

	
	@Override public boolean contains(Object o)
	{
		return array.contains(o);
	}
	

	@Override public boolean containsAll(Collection<?> c)
	{
		return array.containsAll(c);
	}
	
	
	@Override public T get(int index)
	{
		return array.get(index);
	}


	@Override public int indexOf(Object o)
	{
		return array.indexOf(o);
	}

	
	@Override public boolean isEmpty()
	{
		return array.isEmpty();
	}

	
	@Override public Iterator<T> iterator()
	{
		return new It<T>(array);
	}
	
	
	@Override public int lastIndexOf(Object o)
	{
		return array.lastIndexOf(o);
	}


	@Override public ListIterator<T> listIterator()
	{
		return array.listIterator();
	}


	@Override public ListIterator<T> listIterator(int index)
	{
		return array.listIterator(index);
	}

	
	@Deprecated @Override public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	
	@Deprecated @Override public T remove(int index)
	{
		throw new UnsupportedOperationException();
	}
	

	@Deprecated @Override public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	
	@Deprecated @Override public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	
	@Deprecated @Override public T set(int index, T element)
	{
		throw new UnsupportedOperationException();
	}

	
	@Override public int size()
	{
		return array.size();
	}
	
	
	@Override public List<T> subList(int fromIndex, int toIndex)
	{
		return array.subList(fromIndex, toIndex);
	}
	

	@Override public Object[] toArray()
	{
		return array.toArray();
	}

	
	@Override public <T2> T2[] toArray(T2[] a)
	{
		return array.toArray(a);
	}
	
	
	/**
	 * Returns true, if the given collection has the same values as this one,
	 * otherwise false.
	 */
	@Override public boolean equals(Object o)
	{
		return array.equals(o);
	}
	
	
	@Override public int hashCode()
	{
		return hashCode;
	}
	
	
	@Override public String toString()
	{
		return "{size:" + array.size() + ", data:" + array.toString() + "}";
	}
	

}
