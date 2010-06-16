package com.xenoage.pdlib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import org.pcollections.TreePVector;

import com.xenoage.pdlib.It;


/**
 * Persistent vector.
 * 
 * This vector contains efficient producers to create modified
 * versions of this one.
 * 
 * If this is unneeded, because the vector will never change, use
 * {@link IVector} instead for performance reasons.
 * 
 * Currently logarithmic-time querying, setting, insertion and removal.
 * 
 * Deprecated annotations are used to warn the programmer of calling
 * unsupported methods.
 * 
 * @author Andreas Wenger
 */
public final class PVector<T>
	implements Vector<T>
{
	
	private final TreePVector<T> data;
	
	
	public PVector(Collection<T> data)
	{
		this.data = TreePVector.from(data);
	}
	
	
	public PVector(T... data)
	{
		ArrayList<T> array = new ArrayList<T>(data.length);
		for (T o : data)
			array.add(o);
		this.data = TreePVector.from(array);
	}
	
	
	public PVector()
	{
		this.data = TreePVector.empty();
	}
	
	
	public static <T2> PVector<T2> pvec()
	{
		return new PVector<T2>();
	}
	
	
	public static <T2> PVector<T2> pvec(Collection<T2> data)
	{
		return new PVector<T2>(data);
	}
	
	
	public static <T2> PVector<T2> pvec(T2... data)
	{
		return new PVector<T2>(data);
	}
	
	
	private PVector(TreePVector<T> data)
	{
		this.data = data;
	}
	
	
	@Override public T getFirst()
	{
		return data.get(0);
	}
	
	
	@Override public T getLast()
	{
		return data.get(data.size() - 1);
	}

	
	@Deprecated @Override public boolean add(T e)
	{
		throw new UnsupportedOperationException("Use plus method instead");
	}
	
	
	/**
	 * Adds the given element at the end of this vector.
	 */
	public PVector<T> plus(T e)
	{
		return new PVector<T>(data.plus(e));
	}
	
	
	/**
	 * Adds the given element at the end of this vector if it is not null.
	 */
	public PVector<T> plusNotNull(T e)
	{
		if (e != null)
			return plus(e);
		else
			return this;
	}

	
	@Deprecated @Override public void add(int index, T element)
	{
		throw new UnsupportedOperationException("Use plus method instead");
	}
	
	
	public PVector<T> plus(int index, T element)
	{
		return new PVector<T>(data.plus(index, element));
	}

	
	@Deprecated @Override public boolean addAll(Collection<? extends T> c)
	{
		throw new UnsupportedOperationException("Use plusAll method instead");
	}
	
	
	public PVector<T> plusAll(Collection<? extends T> c)
	{
		return new PVector<T>(data.plusAll(c));
	}
	
	
	@Deprecated @Override public boolean addAll(int index, Collection<? extends T> c)
	{
		throw new UnsupportedOperationException("Use plusAll method instead");
	}
	
	
	public PVector<T> plusAll(int index, Collection<? extends T> c)
	{
		return new PVector<T>(data.plusAll(index, c));
	}
	

	@Deprecated @Override public void clear()
	{
		throw new UnsupportedOperationException("Create an empty instance instead");
	}

	
	@Override public boolean contains(Object o)
	{
		return data.contains(o);
	}
	

	@Override public boolean containsAll(Collection<?> c)
	{
		return data.containsAll(c);
	}
	
	
	@Override public T get(int index)
	{
		return data.get(index);
	}


	@Override public int indexOf(Object o)
	{
		return data.indexOf(o);
	}

	
	@Override public boolean isEmpty()
	{
		return data.isEmpty();
	}

	
	@Override public Iterator<T> iterator()
	{
		return new It<T>(data);
	}
	
	
	@Override public int lastIndexOf(Object o)
	{
		return data.lastIndexOf(o);
	}


	@Override public ListIterator<T> listIterator()
	{
		return data.listIterator();
	}


	@Override public ListIterator<T> listIterator(int index)
	{
		return data.listIterator(index);
	}

	
	@Deprecated @Override public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("Use minus method instead");
	}
	
	
	public PVector<T> minus(Object o)
	{
		return new PVector<T>(data.minus(o));
	}
	
	
	public PVector<T> minusFirst()
	{
		return new PVector<T>(data.minus(0));
	}
	
	
	@Deprecated @Override public T remove(int index)
	{
		throw new UnsupportedOperationException("Use minus method instead");
	}
	
	
	public PVector<T> minus(int index)
	{
		return new PVector<T>(data.minus(index));
	}
	

	@Deprecated @Override public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Use minusAll method instead");
	}
	
	
	public PVector<T> minusAll(Collection<?> c)
	{
		return new PVector<T>(data.minusAll(c));
	}

	
	@Deprecated @Override public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Use intersect method instead");
	}
	
	
	public PVector<T> intersect(Collection<?> c)
	{
		LinkedList<T> ret = new LinkedList<T>();
		boolean modified = false;
		for (T e : data)
		{
			if (c.contains(e))
			{
				ret.add(e);
				modified = true;
		  }
		}
		if (modified)
			return new PVector<T>(ret);
		else
			return this;
	}
	
	
	@Deprecated @Override public T set(int index, T element)
	{
		throw new UnsupportedOperationException("Use with method instead");
	}
	
	
	public PVector<T> with(int index, T element)
	{
		return new PVector<T>(data.with(index, element));
	}

	
	@Override public int size()
	{
		return data.size();
	}
	
	
	@Override public PVector<T> subList(int fromIndex, int toIndex)
	{
		return new PVector<T>(data.subList(fromIndex, toIndex));
	}
	

	@Override public Object[] toArray()
	{
		return data.toArray();
	}

	
	@Override public <T2> T2[] toArray(T2[] a)
	{
		return data.toArray(a);
	}
	
	
	/**
	 * Returns true, if the given collection has the same values as this one,
	 * otherwise false.
	 */
	@Override public boolean equals(Object o)
	{
		return data.equals(o);
	}
	
	
	@Override public int hashCode()
	{
		return data.hashCode();
	}
	
	
	@Override public String toString()
	{
		return "{size:" + data.size() + ", data:" + data.toString() + "}";
	}
	

}
