package com.xenoage.zong.data;


/**
 * Abstract class for a group of adjacent staves.
 * 
 * Examples for staves groups are
 * bracket groups or barline groups.
 * 
 * A stave group has a start and end index.
 *
 * @author Andreas Wenger
 */
public abstract class StavesGroup
{
  
  protected int startIndex;
  protected int endIndex;
  
  
  /**
   * Creates a new staves group with the given
   * start and end indices.
   */
  StavesGroup(int startIndex, int endIndex)
  {
    this.startIndex = startIndex;
    this.endIndex = endIndex;
  }
  

  /**
   * Gets the index of the first staff of the group.
   */
  public int getStartIndex()
  {
    return startIndex;
  }
  
  
  /**
   * Gets the index of the last staff of the group.
   */
  public int getEndIndex()
  {
    return endIndex;
  }
  
  
  /**
   * Returns true, if the group contains the staff
   * with the given index.
   */
  public boolean contains(int index)
  {
    return (index >= startIndex && index <= endIndex);
  }

  
}
