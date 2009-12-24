package com.xenoage.zong.data.music.directions;

import com.xenoage.zong.data.music.Direction;
import com.xenoage.zong.data.music.format.Position;


/**
 * Base class for crescendos and diminuendos.
 * 
 * To create a wedge, create an instance of a subclass of this class,
 * retrieve its end marker by calling <code>getWedgeEnd()</code> and
 * place it anywhere after the instance of this class. The wedge ends
 * at the element after which it is placed.
 * 
 * @author Andreas Wenger
 */
public abstract class Wedge
	extends Direction
{
	
	private final WedgeEnd wedgeEnd;
	private final Float spread;
	
	
	/**
	 * Creates a new {@link Wedge} with the given spread in IS,
	 * or null for default.
	 */
	public Wedge(Float spread, Position position)
	{
		super(position);
		this.wedgeEnd = new WedgeEnd(this);
		this.spread = spread;
	}
	
	
	public Float getSpread()
	{
		return spread;
	}
	
	
	public WedgeEnd getWedgeEnd()
	{
		return wedgeEnd;
	}
	

}
