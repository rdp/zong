package com.xenoage.zong.data.music.barline;

import com.xenoage.zong.data.music.NoVoiceElement;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a barline.
 * 
 * Though barlines can be placed in the middle of a measure (always in voice 0 like
 * all other NoVoiceElements), usually they are used as the explicit start
 * or end barline of a measure.
 * 
 * They are never placed within a {@link Voice}, but
 * always within a {@link MeasureColumnHeader}.
 *
 * @author Andreas Wenger
 */
public final class Barline
  extends NoVoiceElement
{
	
	private final BarlineStyle style;
	private final BarlineRepeat repeat;
	private final int repeatTimes;
	
	
	/**
	 * Creates a regular barline.
	 */
	public static Barline createRegularBarline()
	{
		return new Barline(BarlineStyle.Regular, BarlineRepeat.None, 0);
	}
	
	
	/**
	 * Creates a barline without repeat.
	 * @param style        the style of the line(s)
	 */
	public static Barline createBarline(BarlineStyle style)
	{
		return new Barline(style, BarlineRepeat.None, 0);
	}
	
	
	/**
	 * Creates a barline with forward repeat.
	 * @param style        the style of the line(s)
	 */
	public static Barline createForwardRepeatBarline(BarlineStyle style)
	{
		return new Barline(style, BarlineRepeat.Forward, 0);
	}
	
	
	/**
	 * Creates a barline with backward repeat.
	 * @param style        the style of the line(s)
	 * @param repeatTimes  the number of repeats
	 */
	public static Barline createBackwardRepeatBarline(BarlineStyle style, int repeatTimes)
	{
		return new Barline(style, BarlineRepeat.Backward, repeatTimes);
	}
	
	
	/**
	 * Creates a barline with repeat at both sides. This barline is only
	 * supported as a mid-measure barline!
	 * @param style        the style of the line(s)
	 * @param repeatTimes  the number of repeats
	 */
	public static Barline createBothRepeatBarline(BarlineStyle style, int repeatTimes)
	{
		return new Barline(style, BarlineRepeat.Both, repeatTimes);
	}
	
	
	/**
	 * Creates a barline.
	 * @param style        the style of the line(s)
	 * @param repeat       the repeat style
	 * @param repeatTimes  the number of repeats. Only used for backward repeats.
	 */
  private Barline(BarlineStyle style, BarlineRepeat repeat, int repeatTimes)
  {
  	this.style = style;
  	this.repeat = repeat;
  	this.repeatTimes = repeatTimes;
  }
  
  
  /**
	 * Return this barline, since this class is immutable.
	 */
	@Override public Barline deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}
	
	
	/**
	 * Gets the style of the barline.
	 */
	public BarlineStyle getStyle()
	{
		return style;
	}
	
	
	/**
	 * Gets the repeat type of the barline.
	 */
	public BarlineRepeat getRepeat()
	{
		return repeat;
	}
	
	
	/**
	 * Gets the repeat times of this barline, or 0 if it is
	 * no backward repeat barline.
	 */
	public int getRepeatTimes()
	{
		return repeatTimes;
	}


}
