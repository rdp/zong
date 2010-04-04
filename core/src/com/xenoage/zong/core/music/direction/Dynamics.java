package com.xenoage.zong.core.music.direction;


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
	
	
	public DynamicsType getType()
	{
		return type;
	}
	
  
}
