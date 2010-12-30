package com.xenoage.zong.core.music.direction;

import com.xenoage.zong.core.music.format.Position;


/**
 * Class for a pedal direction. Start or stop.
 *
 * @author Andreas Wenger
 */
public final class Pedal
  extends Direction
{
	
	public enum Type
	{
		Start, Stop;
	}
	
	private final Type type;

	
	/**
	 * Creates a new {@link Pedal} direction.
	 * @param type  start or stop
	 */
	public Pedal(Type type, Position position)
	{
		super(position);
		this.type = type;
	}
	
	
	public Type getType()
	{
		return type;
	}
	
  
}
