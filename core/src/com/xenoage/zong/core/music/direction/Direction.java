package com.xenoage.zong.core.music.direction;

import com.xenoage.zong.core.music.Attachable;
import com.xenoage.zong.core.music.MeasureElement;
import com.xenoage.zong.core.music.format.Position;


/**
 * Base class for all directions, like dynamics,
 * word directions, segno or pedals.
 * 
 * Directions are either attached to chords (if they belong
 * to a certain chord and possibly following ones) or to the
 * score header (TODO), if they belong to all staves and voices,
 * like segnos.
 * 
 * All directions must be immutable and persistent.
 *
 * @author Andreas Wenger
 */
public abstract class Direction
  implements MeasureElement, Attachable
{
	
	
	private final Position position;
	
	
	/**
	 * Creates a new {@link Direction} with default positioning.
	 */
	public Direction()
	{
		this.position = null;
	}
	
	
	/**
	 * Creates a new {@link Direction} with custom positioning.
	 */
	public Direction(Position position)
	{
		this.position = position;
	}
	
	
	/**
	 * Gets the custom positioning of this direction, or null for default.
	 */
	public Position getPosition()
	{
		return position;
	}
	

}
