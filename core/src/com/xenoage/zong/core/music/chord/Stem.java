package com.xenoage.zong.core.music.chord;


/**
 * Class for a stem, that is attached to a note.
 *
 * @author Andreas Wenger
 */
public final class Stem
{
	
	private final StemDirection direction;
	private final Float length;
	
	
	/**
	 * Creates a new {@link Stem}.
	 * @param direction  the direction of the stem, or null for default
	 * @param length     the length of the stem, measured from the outermost chord not at the stem side
	 *                   to the end of the stem, in interline spaces, or null for default
	 */
	public Stem(StemDirection direction, Float length)
	{
		this.direction = direction;
		this.length = length;
	}
	
	
	/**
	 * Gets the direction of the stem, or null for default.
	 */
	public StemDirection getDirection()
	{
		return direction;
	}


	/**
	 * Gets the length of the stem, measured from the outermost chord not at the stem side
	 * to the end of the stem, in interline spaces, or null for default.
	 */
	public Float getLength()
	{
		return length;
	}
	

}
