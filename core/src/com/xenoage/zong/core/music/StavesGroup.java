package com.xenoage.zong.core.music;


/**
 * Group of adjacent staves.
 * 
 * Examples for staves groups are
 * bracket groups or barline groups.
 * 
 * A stave group has a start and end index.
 *
 * @author Andreas Wenger
 */
public final class StavesGroup<T>
{
  
  private final int startIndex;
  private final int endIndex;
  private final T style;
  
  
  /**
   * Creates a new staves group with the given
   * start and end indices and the given style.
   */
  public StavesGroup(int startIndex, int endIndex, T style)
  {
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.style = style;
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
   * Gets the style of this group.
   */
  public T getStyle()
  {
  	return style;
  }
  
  
  /**
   * Returns true, if the group contains the staff
   * with the given index.
   */
  public boolean contains(int index)
  {
    return (index >= startIndex && index <= endIndex);
  }
  
  
  /**
   * Shifts the indices by the given amount.
   */
  public StavesGroup<T> shift(int amount)
  {
  	return new StavesGroup<T>(startIndex + amount, endIndex + amount, style);
  }

  
  /**
	 * Shifts the end index by the given amount.
	 */
	public StavesGroup<T> shiftEnd(int amount)
	{
		return new StavesGroup<T>(startIndex, endIndex + amount, style);
	}
  
}
