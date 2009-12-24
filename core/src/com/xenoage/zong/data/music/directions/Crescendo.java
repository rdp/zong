package com.xenoage.zong.data.music.directions;

import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.format.Position;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Crescendo.
 * 
 * @author Andreas Wenger
 */
public class Crescendo
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
	
	
	@Override public Crescendo deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}

}
