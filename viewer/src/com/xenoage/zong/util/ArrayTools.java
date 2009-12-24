package com.xenoage.zong.util;

import com.xenoage.util.SortedList;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.musiclayout.BeatMarker;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.BeatOffset;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;
import com.xenoage.zong.musiclayout.stampings.Stamping;

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
  
  public static BeatOffset[] toBeatOffsetArray(List<BeatOffset> a)
  {
    if (a == null)
      return new BeatOffset[0];
    BeatOffset[] ret = new BeatOffset[a.size()];
    int i = 0;
    for (BeatOffset beatOffset : a)
    {
      ret[i] = beatOffset;
      i++;
    }
    return ret;
  }
  
  
  public static ArrayList<BeatOffset> toBeatOffsetArrayList(BeatOffset[] a)
  {
    if (a == null)
      return new ArrayList<BeatOffset>(0);
    ArrayList<BeatOffset> ret = new ArrayList<BeatOffset>(a.length);
    for (BeatOffset beatOffset : a)
    {
      ret.add(beatOffset);
    }
    return ret;
  }
  
  
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
  
  
  public static FrameArrangement[] toFrameArrangementArray(List<FrameArrangement> a)
  {
    if (a == null)
      return new FrameArrangement[0];
    FrameArrangement[] ret = new FrameArrangement[a.size()];
    int i = 0;
    for (FrameArrangement t : a)
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
  
  
  public static MeasureColumnSpacing[] toMeasureColumnSpacingArray(List<MeasureColumnSpacing> a)
  {
    if (a == null)
      return new MeasureColumnSpacing[0];
    MeasureColumnSpacing[] ret = new MeasureColumnSpacing[a.size()];
    int i = 0;
    for (MeasureColumnSpacing t : a)
    {
      ret[i] = t;
      i++;
    }
    return ret;
  }
  
  
  public static BeatMarker[] toBeatMarkerArray(SortedList<BeatMarker> a)
  {
    if (a == null)
      return new BeatMarker[0];
    BeatMarker[] ret = new BeatMarker[a.getSize()];
    int i = 0;
    for (BeatMarker t : a)
    {
      ret[i] = t;
      i++;
    }
    return ret;
  }
  
  
  public static Pitch[] toPitchArray(List<Pitch> a)
  {
    if (a == null)
      return new Pitch[0];
    Pitch[] ret = new Pitch[a.size()];
    int i = 0;
    for (Pitch t : a)
    {
      ret[i] = t;
      i++;
    }
    return ret;
  }
  
  
  public static SpacingElement[] toSpacingElementArray(List<SpacingElement> a)
  {
    if (a == null)
      return new SpacingElement[0];
    SpacingElement[] ret = new SpacingElement[a.size()];
    int i = 0;
    for (SpacingElement t : a)
    {
      ret[i] = t;
      i++;
    }
    return ret;
  }
  
  
  public static Stamping[] toStampingArray(List<Stamping> a)
  {
    if (a == null)
      return new Stamping[0];
    Stamping[] ret = new Stamping[a.size()];
    int i = 0;
    for (Stamping t : a)
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
  
  
  public static Symbol[] toSymbolArray(List<Symbol> a)
  {
    if (a == null)
      return new Symbol[0];
    Symbol[] ret = new Symbol[a.size()];
    int i = 0;
    for (Symbol t : a)
    {
      ret[i] = t;
      i++;
    }
    return ret;
  }
  
  
  public static SystemArrangement[] toSystemArrangementArray(List<SystemArrangement> a)
  {
    if (a == null)
      return new SystemArrangement[0];
    SystemArrangement[] ret = new SystemArrangement[a.size()];
    int i = 0;
    for (SystemArrangement t : a)
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
  

}
