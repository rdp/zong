package com.xenoage.zong.data.music.directions;

import com.xenoage.zong.data.music.Direction;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.format.Position;
import com.xenoage.zong.data.music.util.DeepCopyCache;


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
	
	
	@Override public Pedal deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}
	
	
	public Type getType()
	{
		return type;
	}
	
  
}
