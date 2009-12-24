package com.xenoage.zong.musiclayout.continued;

import com.xenoage.zong.data.music.Volta;


/**
 * Continued volta.
 * 
 * @author Andreas Wenger
 */
public class ContinuedVolta
	implements ContinuedElement
{
	
	private final Volta volta;
	private final int startMeasureIndex;
	private final int staffIndex;
	
	
	public ContinuedVolta(Volta volta, int startMeasureIndex, int staffIndex)
	{
		this.volta = volta;
		this.startMeasureIndex = startMeasureIndex;
		this.staffIndex = staffIndex;
	}
	
	
	public Volta getMusicElement()
	{
		return volta;
	}
	
	
	public int getStartMeasureIndex()
	{
		return startMeasureIndex;
	}
	
	
	public int getStaffIndex()
	{
		return staffIndex;
	}

}
