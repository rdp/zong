package com.xenoage.zong.core.music.volta;

import com.xenoage.util.Range;
import com.xenoage.zong.core.header.ColumnHeader;
import com.xenoage.zong.core.music.ColumnElement;


/**
 * Class for a volta (also informally called "Haus" in German).
 * 
 * A volta is never used within a voice, but only in the {@link ColumnHeader}.
 * Voltas span over whole measures, at least one. The number of spanned
 * measures is saved in this class.
 * 
 * Each volta has a numbers attribute, which is a range which
 * tells in which repetitions it should be entered,
 * and optionally an arbitrary caption.
 * A downward hook on the right side is optional.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class Volta
  implements ColumnElement
{
	
	private final int length;
	private final Range numbers; //[1,1] for the 1st time, ..., null for "else"
	private final String caption;
	private final boolean rightHook;
	
	
	/**
	 * Creates a volta.
	 * @param length     the number of measures this volta spans, at least one
	 * @param numbers    the repetitions (beginning with 1) where this volta is entered
	 * @param caption    the caption, or null, to use a default caption, generated from
	 *                   the numbers: [x,x] results to "x.", [x,y] to "x.–y."
	 * @param rightHook  true, if there is a downward hook on the right side, otherwise false
	 */
  public Volta(int length, Range numbers, String caption, boolean rightHook)
  {
  	if (length < 1)
  		throw new IllegalArgumentException("Volta must span at least 1 measure");
  	this.length = length;
  	this.numbers = numbers;
  	//caption
  	if (caption != null)
  	{
  		this.caption = caption;
  	}
  	else if (numbers == null)
  	{
  		//TODO
  		this.caption = "";
  	}
  	else if (numbers.getCount() == 1)
  	{
  		this.caption = numbers.getStart() + ".";
  	}
  	else
  	{
  		this.caption = numbers.getStart() + ".–" + numbers.getStop() + ".";
  	}
  	this.rightHook = rightHook;
  }


	/**
	 * Gets the number of measures this volta spans.
	 */
	public int getLength()
	{
		return length;
	}


	/**
	 * Gets the range of repetitions (beginning with 1) where this
	 * volta is entered.
	 */
	public Range getNumbers()
	{
		return numbers;
	}


	/**
	 * Gets the caption of this volta.
	 * This is never null, but may be the empty string.
	 */
	public String getCaption()
	{
		return caption;
	}
	
	
	/**
	 * Returns true, if there is a downward hook on the right side, otherwise false.
	 */
	public boolean isRightHook()
	{
		return rightHook;
	}


}
