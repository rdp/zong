package com.xenoage.zong.musiclayout.continued;

import com.xenoage.zong.core.music.direction.Wedge;


/**
 * Continued {@link Wedge}.
 * 
 * @author Andreas Wenger
 */
public final class ContinuedWedge
	implements ContinuedElement
{
	
	private final Wedge wedge;
	private final int staffIndex;
	
	
	public ContinuedWedge(Wedge wedge, int staffIndex)
	{
		this.wedge = wedge;
		this.staffIndex = staffIndex;
	}
	
	
	public Wedge getMusicElement()
	{
		return wedge;
	}
	
	
	public int getStaffIndex()
	{
		return staffIndex;
	}

}
