package com.xenoage.zong.data.music.directions;

import com.xenoage.zong.data.music.Direction;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.DeepCopyCache;


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
	
	
	@Override public WedgeEnd deepCopy(Voice parentVoice, DeepCopyCache cache)
	{
		return this;
	}
	
	
	public Wedge getWedge()
	{
		return wedge;
	}
	
  
}
