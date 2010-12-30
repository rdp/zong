package com.xenoage.zong.core.music.group;


/**
 * Group of staves with a common bracket.
 * 
 * @author Andreas Wenger
 */
public final class BracketGroup
{
	
	public enum Style
	{
	  
	  /** Curly brackets. */
	  Brace,
	  
	  /** Square brackets. */
	  Bracket;

	}
	
	private final StavesRange stavesRange;
	private final Style style;
	
	
	/**
   * Creates a new staves group with the given start and end indices
   * and the given style.
   */
  public BracketGroup(StavesRange stavesRange, Style style)
  {
  	this.stavesRange = stavesRange;
  	this.style = style;
  }
  

  /**
	 * Gets the range of staves.
	 */
	public StavesRange getStavesRange()
	{
		return stavesRange;
	}
	
	
	/**
   * Gets the style of this group.
   */
  public Style getStyle()
  {
  	return style;
  }
  
  
  /**
   * Shifts the indices by the given amount.
   */
  public BracketGroup shift(int amount)
  {
  	return new BracketGroup(stavesRange.shift(amount), style);
  }

  
  /**
	 * Shifts the end index by the given amount.
	 */
	public BracketGroup shiftEnd(int amount)
	{
		return new BracketGroup(stavesRange.shiftEnd(amount), style);
	}
	

}
