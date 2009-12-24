package com.xenoage.zong.data.music.directions;

import com.xenoage.zong.data.music.Direction;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Class for a dynamics sign like forte, piano, sforzando and so on.
 *
 * @author Andreas Wenger
 */
public final class Dynamics
  extends Direction
{
	
	private final DynamicsType type;
	
	
	public Dynamics(DynamicsType type)
	{
		this.type = type;
	}
	
	
	@Override public Dynamics deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}
	
	
	public DynamicsType getType()
	{
		return type;
	}
	
  
}
