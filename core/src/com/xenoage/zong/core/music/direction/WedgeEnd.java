package com.xenoage.zong.core.music.direction;


/**
 * End marker for a {@link Wedge}.
 *
 * @author Andreas Wenger
 */
public final class WedgeEnd
  extends Direction
{
	
	private final Wedge wedge;
	
	
	/**
	 * Creates an end marker for the given {@link Wedge}.
	 */
	public WedgeEnd(Wedge wedge)
	{
		this.wedge = wedge;
	}
	
	
	public Wedge getWedge()
	{
		return wedge;
	}
	
  
}
