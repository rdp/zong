package com.xenoage.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


/**
 * Sorted list, based on a {@link LinkedList}.
 * The list may contain duplicate entries or not.
 * 
 * @author Andreas Wenger
 */
public class SortedList<T extends Comparable<T>>
	implements Iterable<T>
{
	
	private LinkedList<T> list = new LinkedList<T>();
	private final boolean duplicates;
	
	
	/**
	 * Creates a new sorted list.
	 * @param duplicates  true, if duplicates (entries for which the equals method returns true),
	 *                    or false, to ignore duplicate entries.
	 */
	public SortedList(boolean duplicates)
	{
		this.duplicates = duplicates;
	}
	
	
	/**
	 * Creates a new sorted list from presorted elements.
	 * If they are not sorted, an {@link IllegalArgumentException} is thown.
	 */
	public SortedList(T[] entries, boolean duplicates)
	{
		this.duplicates = duplicates;
		T last = null;
		for (T e : entries)
		{
			if (last != null)
			{
				if ((duplicates && last.compareTo(e) > 0) ||
					(!duplicates && last.compareTo(e) >= 0))
				{
					throw new IllegalArgumentException("Elements are not presorted!");
				}
			}
			list.addLast(e);
			last = e;
		}
	}
	
	
	/**
	 * Merges this list with the given list and returns the result.
	 */
	public SortedList<T> merge(SortedList<T> sortedList, boolean duplicates)
	{
		SortedList<T> ret = new SortedList<T>(duplicates);
		Iterator<T> l1 = this.list.iterator();
		Iterator<T> l2 = sortedList.list.iterator();
		T e1 = (l1.hasNext() ? l1.next() : null);
		T e2 = (l2.hasNext() ? l2.next() : null);
		while (e1 != null || e2 != null)
		{
			if (e1 == null) //l1 queue is empty
			{
				ret.addLast(e2);
				e2 = (l2.hasNext() ? l2.next() : null);
			}
			else if (e2 == null) //l2 queue is empty
			{
				ret.addLast(e1);
				e1 = (l1.hasNext() ? l1.next() : null);
			}
			else
			{
				//both queues are non-empty, choose the lower one
				if (e1.compareTo(e2) <= 0)
				{
					//e1 first
					ret.addLast(e1);
					e1 = (l1.hasNext() ? l1.next() : null);
				}
				else
				{
					//e2 first
					ret.addLast(e2);
					e2 = (l2.hasNext() ? l2.next() : null);
				}
			}
		}
		return ret;
	}
	
	
	/**
	 * Adds the given entry at the correct position.
	 * If duplicates are not allowed but the given entry is a duplicate,
	 * it is not inserted.
	 * 
	 * Runtime: O(n)
	 */
	public void add(T entry)
	{
		add(entry, false);
	}
	
	
	/**
	 * Adds the given entry at the correct position.
	 * If duplicates are not allowed but the given entry is a duplicate,
	 * it is not replaced.
	 * 
	 * Runtime: O(n)
	 */
	public void addOrReplace(T entry)
	{
		add(entry, true);
	}
	
	
	
	/**
	 * Adds the given entry at the correct position.
	 * If duplicates are not allowed but the given entry is a duplicate,
	 * it is not inserted if <code>replace</code> is false, otherwise
	 * it is replaced.
	 * 
	 * Runtime: O(n)
	 */
	private void add(T entry, boolean replace)
	{
		ListIterator<T> iterator = list.listIterator();
		while (iterator.hasNext())
		{
			T e = iterator.next();
			int compare = e.compareTo(entry);
			if (compare > 0)
			{
				//add before this entry
				iterator.previous();
				iterator.add(entry);
				return;
			}
			else if (compare == 0 && !duplicates)
			{
				if (replace)
					iterator.set(entry);
				return;
			}
		}
		list.addLast(entry);
	}


	/**
	 * Gets an iterator for this list.
	 */
	public Iterator<T> iterator()
	{
		return list.iterator();
	}
	
	
	/**
	 * Gets the entry at the given index.
	 * 
	 * Runtime: O(n)
	 */
	public T get(int index)
	{
		return list.get(index);
	}
	
	
	/**
	 * Gets the first entry.
	 * 
	 * Runtime: O(1)
	 */
	public T getFirst()
	{
		return list.getFirst();
	}
	
	
	/**
	 * Gets the last entry.
	 * 
	 * Runtime: O(1)
	 */
	public T getLast()
	{
		return list.getFirst();
	}
	
	
	/**
	 * Gets the {@link LinkedList} behind this sorted list
	 * for further computation.
	 * Changing this list may destroy this instance of a sorted list,
	 * which has unpredictable results.
	 */
	public LinkedList<T> getLinkedList()
	{
		return list;
	}
	
	
	/**
	 * Gets the number of entries.
	 */
	public int getSize()
	{
		return list.size();
	}
	
	
	/**
	 * Adds the given element as the last one, but only if it is
	 * greater than the last element.
	 */
	private void addLast(T e)
	{
		if (duplicates || list.size() == 0 || e.compareTo(list.getLast()) > 0)
		{
			list.addLast(e);
		}
	}
	
	
	@Override public String toString()
	{
		return list.toString();
	}
	

}
