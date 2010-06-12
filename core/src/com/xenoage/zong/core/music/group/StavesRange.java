package com.xenoage.zong.core.music.group;


/**
 * Range of adjacent staves.
 *
 * @author Andreas Wenger
 */
public final class StavesRange
{
  
  private final int startIndex;
  private final int endIndex;
  
  
  /**
   * Creates a new staves range with the given start and end indices.
   */
  public StavesRange(int startIndex, int endIndex)
  {
    this.startIndex = startIndex;
    this.endIndex = endIndex;
  }
  

  /**
   * Gets the index of the first staff of the range.
   */
  public int getStartIndex()
  {
    return startIndex;
  }
  
  
  /**
   * Gets the index of the last staff of the range.
   */
  public int getEndIndex()
  {
    return endIndex;
  }
  
  
  /**
   * Sets the index of the last staff of the range.
   */
  public StavesRange withEndIndex(int endIndex)
  {
    return new StavesRange(startIndex, endIndex);
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
  public StavesRange shift(int amount)
  {
  	return new StavesRange(startIndex + amount, endIndex + amount);
  }

  
  /**
	 * Shifts the end index by the given amount.
	 */
	public StavesRange shiftEnd(int amount)
	{
		return new StavesRange(startIndex, endIndex + amount);
	}
  
}
