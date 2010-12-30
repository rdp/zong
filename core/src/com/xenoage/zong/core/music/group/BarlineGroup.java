package com.xenoage.zong.core.music.group;


/**
 * Group of staves with a common barline.
 * 
 * @author Andreas Wenger
 */
public final class BarlineGroup
{
	
	public enum Style
	{
	  
	  /** Each staff has its own barlines. */
	  Single,
	  
	  /** The barlines of the staves are connected. */
	  Common,
	  
	  /** Barlines are shown between the staves but not on them. */
	  Mensurstrich;

	}
	
	private final StavesRange stavesRange;
	private final Style style;
	
	
	/**
   * Creates a new staves group with the given start and end indices
   * and the given style.
   */
  public BarlineGroup(StavesRange stavesRange, Style style)
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
  public BarlineGroup shift(int amount)
  {
  	return new BarlineGroup(stavesRange.shift(amount), style);
  }

  
  /**
	 * Shifts the end index by the given amount.
	 */
	public BarlineGroup shiftEnd(int amount)
	{
		return new BarlineGroup(stavesRange.shiftEnd(amount), style);
	}
	

}
