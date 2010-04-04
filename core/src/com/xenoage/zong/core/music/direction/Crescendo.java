package com.xenoage.zong.core.music.direction;

import com.xenoage.zong.core.music.format.Position;


/**
 * Crescendo.
 * 
 * @author Andreas Wenger
 */
public final class Crescendo
	extends Wedge
{
	
	
	/**
	 * Creates a new {@link Crescendo} with the given spread in IS,
	 * or null for default.
	 */
	public Crescendo(Float spread, Position position)
	{
		super(spread, position);
	}

}
