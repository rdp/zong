package com.xenoage.zong.core.music.util;

import static com.xenoage.util.Range.range;
import static com.xenoage.util.iterators.ReverseIterator.reverseIt;
import static com.xenoage.util.lang.Tuple2.t;
import static com.xenoage.zong.core.music.util.BeatE.beate;

import java.util.Iterator;
import java.util.LinkedList;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;


/**
 * This is a wrapper class to combine a list of objects with
 * the beats they belong to.
 * 
 * @author Andreas Wenger
 */
public class BeatEList<T>
	implements Iterable<BeatE<T>>
{
	
	private final PVector<BeatE<T>> elements;
	
	
	public BeatEList()
	{
		this.elements = new PVector<BeatE<T>>();
	}
	
	
	private BeatEList(PVector<BeatE<T>> elements)
	{
		this.elements = elements;
	}
	
	
	/**
	 * Gets all elements.
	 */
	public PVector<BeatE<T>> getElements()
	{
		return elements;
	}
	
	
	/**
	 * Gets the first element at the given beat, or null if there is none.
	 */
	public T get(Fraction beat)
	{
		for (BeatE<T> e : elements)
		{
			if (e.getBeat().equals(beat))
				return e.getElement();
		}
		return null;
	}
	
	
	/**
	 * Gets all elements at the given beat, or an empty list if there are none.
	 */
	public LinkedList<T> getAll(Fraction beat)
	{
		LinkedList<T> ret = new LinkedList<T>();
		for (BeatE<T> e : elements)
		{
			int compare = e.getBeat().compareTo(beat);
			if (compare == 0)
				ret.add(e.getElement());
			else if (compare > 0)
				break;
		}
		return ret;
	}
	
	
	/**
	 * Adds the given positioned element. If there is already
	 * one, it is added before the existing one, but nothing is removed.
	 */
	public BeatEList<T> plus(BeatE<T> element)
	{
		for (int i : range(elements))
		{
			if (element.getBeat().compareTo(elements.get(i).getBeat()) <= 0)
				return new BeatEList<T>(elements.plus(i, element));
		}
		return new BeatEList<T>(elements.plus(element));
	}
	
	
	/**
	 * Adds the given positioned element. If there is already
	 * one, it is added before the existing one, but nothing is removed.
	 */
	public BeatEList<T> plus(T element, Fraction beat)
	{
		return plus(beate(element, beat));
	}
	
	
	/**
	 * Adds the given element at the given beat. If there is already
	 * a element, it is replaced by the given one and returned (otherwise null).
	 */
	public Tuple2<BeatEList<T>, T> with(BeatE<T> element)
	{
		for (int i : range(elements))
		{
			BeatE<T> e = elements.get(i);
			int compare = element.getBeat().compareTo(e.getBeat());
			if (compare == 0)
				return t(new BeatEList<T>(elements.with(i, element)), e.getElement());
			else if (compare < 0)
				return t(new BeatEList<T>(elements.plus(i, element)), null);
		}
		return t(new BeatEList<T>(elements.plus(element)), null);
	}
	
	
	/**
	 * Adds the given element at the given beat. If there is already
	 * a element, it is replaced by the given one and returned (otherwise null).
	 */
	public Tuple2<BeatEList<T>, T> with(T element, Fraction beat)
	{
		return with(beate(element, beat));
	}
	
	
	/**
	 * Removes the first occurrence of the given element.
	 */
	public BeatEList<T> minus(T element)
	{
		for (int i : range(elements))
		{
			if (elements.get(i).getElement() == element)
				new BeatEList<T>(elements.minus(i));
		}
		return this;
	}
	
	
	/**
	 * Removes the first element at the given beat (if there is any).
	 */
	public BeatEList<T> minus(Fraction beat)
	{
		T e = get(beat);
		if (e != null)
			return minus(e);
		else
			return this;
	}
	
	
	/**
	 * Gets the last element, or null if the list is empty.
	 */
	public BeatE<T> getLast()
	{
		if (elements.size() > 0)
			return elements.getLast();
		else
			return null;
	}
	
	
	/**
   * Returns the last element at or before the given beat.
   * If there is none, null is returned.
   */
  public BeatE<T> getLastBefore(BeatInterval endpoint, Fraction beat)
  {
    for (BeatE<T> e : reverseIt(elements))
    {
      if (endpoint.isInInterval(e.beat, beat))
      	return e;
    }
    return null;
  }
  
  
  /**
   * Gets the number of elements in this list.
   */
  public int size()
  {
  	return elements.size();
  }
  
  
  @Override public String toString()
	{
		return elements.toString();
	}


	@Override public Iterator<BeatE<T>> iterator()
	{
		return elements.iterator();
	}
  

}
