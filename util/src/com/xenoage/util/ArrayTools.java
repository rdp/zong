package com.xenoage.util;

import static com.xenoage.util.Range.range;

import com.xenoage.util.math.Fraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Some useful functions for arrays and ArrayLists.
 * 
 * PERFORMANCE: don't use them. work with Lists only if possible.
 *
 * @author Andreas Wenger
 */
public class ArrayTools
{
  
  
  public static Fraction[] toFractionArray(List<Fraction> a)
  {
    if (a == null)
      return new Fraction[0];
    Fraction[] ret = new Fraction[a.size()];
    int i = 0;
    for (Fraction t : a)
    {
      ret[i] = t;
      i++;
    }
    return ret;
  }

  
  public static int[] toIntArray(List<Integer> a)
  {
    if (a == null)
      return new int[0];
    int[] ret = new int[a.size()];
    int i = 0;
    for (int t : a)
    {
      ret[i] = t;
      i++;
    }
    return ret;
  }
  
  
  public static String[] toStringArray(List<String> a)
  {
    if (a == null)
      return new String[0];
    String[] ret = new String[a.size()];
    int i = 0;
    for (String t : a)
    {
      ret[i] = t;
      i++;
    }
    return ret;
  }
  
  
  public static float[] copy(float[] a)
  {
    float[] ret = new float[a.length];
    for (int i = 0; i < a.length; i++)
      ret[i] = a[i];
    return ret;
  }
  
  
  public static <T> int indexOf(T[] a, T e)
  {
  	for (int i : range(a))
  	{
  		if (a[i] == null)
  		{
  			if (e == null)
  			{
  				return i;
  			}
  		}
  		else if (a[i].equals(e))
  		{
  			return i;
  		}
  	}
  	return -1;
  }
  
  
  public static float sum(float[] a)
  {
    float ret = 0;
    for (int i = 0; i < a.length; i++)
      ret += a[i];
    return ret;
  }
  
  
  public static float sum(ArrayList<Float> a)
  {
    float ret = 0;
    for (int i = 0; i < a.size(); i++)
      ret += a.get(i);
    return ret;
  }
  
  
  /**
   * Returns true, if the given array contains the given object
   * (same reference, not same contents).
   */
  public static boolean contains(Object[] a, Object o)
  {
    for (Object e : a)
      if (e == o) return true;
    return false;
  }
  
  
  /**
   * Returns true, if the given collection contains the given object
   * (same reference, not same contents).
   */
  @SuppressWarnings("unchecked") public static boolean contains(Collection c, Object o)
  {
    for (Object e : c)
      if (e == o) return true;
    return false;
  }
  
  
  /**
   * Returns true, if the given array contains a <code>null</code> element.
   */
  public static boolean containsNull(Object[] a)
  {
    return contains(a, null);
  }
  
  
  /**
   * Returns true, if the given collection contains a <code>null</code> element.
   */
  @SuppressWarnings("unchecked") public static boolean containsNull(Collection c)
  {
    return contains(c, null);
  }
  
  
  /**
   * Returns true, if the given collection contains only <code>null</code> elements.
   */
  public static <T> boolean containsOnlyNull(T[] c)
  {
  	for (Object e : c)
      if (e != null) return false;
    return true;
  }
  

}
