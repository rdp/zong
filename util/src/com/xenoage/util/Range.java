package com.xenoage.util;

import java.util.Collection;
import java.util.Iterator;


/**
 * Class for a range of integers.
 * It can be used like this:
 * 
 * <pre>
 * Range r = new Range(0, 4);
 * for (int i : r)
 *   System.out.print(i + " "); // 0 1 2 3 4
 * </pre>
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class Range
	implements Iterable<Integer>
{
	
	private final int start;
	private final int stop;
	
	
	/**
	 * Creates a new range between the given two values (both inclusive).
	 */
	public static Range range(int start, int stop)
	{
		return new Range(start, stop);
	}
	
	
	/**
	 * Creates a new range between 0 and the given value (exclusive).
	 */
	public static Range range(int count)
	{
		return new Range(0, count - 1);
	}
	
	
	/**
	 * Creates a new range for all indices within the given collection.
	 */
	public static <T> Range range(Collection<T> collection)
	{
		return new Range(0, collection.size() - 1);
	}
	
	
	/**
	 * Creates a new range for all indices within the given array.
	 */
	public static <T> Range range(T... a)
	{
		return new Range(0, a.length - 1);
	}
	
	
	/**
	 * Creates a new range between the given two values (both inclusive).
	 */
	private Range(int start, int stop)
	{
		this.start = start;
		this.stop = stop;
	}
	
	
	/**
   * Returns an iterator over this range.
   */
  public Iterator<Integer> iterator()
  {
    return new Iterator<Integer>()
    {
      private int i = start;

      public boolean hasNext()
      {
        return i <= stop;
      }

      public Integer next()
      {
        i++;
        return i-1;
      }

      public void remove()
      {
      }
    };
  }
  
  
  /**
   * Gets the first number.
   */
  public int getStart()
	{
		return start;
	}


  /**
   * Gets the last number.
   */
	public int getStop()
	{
		return stop;
	}
	
	
	/**
	 * Gets the number of numbers.
	 */
	public int getCount()
	{
		return stop - start + 1;
	}
	
	
	/**
	 * Returns whether the given number is in the Range or not.
	 */
	public boolean isInRange(int number)
	{
		if (number >= start && number <= stop)
		{
			return true;
		}
		return false;
	}
}