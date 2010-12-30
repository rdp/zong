package com.xenoage.zong.util;

import java.util.ArrayList;
import java.util.List;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.musiclayout.BeatOffset;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.spacing.ColumnSpacing;


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
  
  
  public static ColumnSpacing[] toMeasureColumnSpacingArray(List<ColumnSpacing> a)
  {
    if (a == null)
      return new ColumnSpacing[0];
    ColumnSpacing[] ret = new ColumnSpacing[a.size()];
    int i = 0;
    for (ColumnSpacing t : a)
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
  

}
