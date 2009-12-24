package com.xenoage.zong.data.music.util;

import java.util.ArrayList;

import com.xenoage.zong.data.music.Measure;


/**
 * Just a wrapper for a list of measures
 * within one column.
 * 
 * @author Andreas Wenger
 */
public final class MeasureColumn
	extends ArrayList<Measure>
{
	
	
	public MeasureColumn(int size)
	{
		super(size);
	}


}
