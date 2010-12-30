package com.xenoage.zong.core.music.util;

import static com.xenoage.util.Range.range;

import java.util.ArrayList;

import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.Measure;


/**
 * Just a wrapper for a list of measures
 * within one column.
 * 
 * @author Andreas Wenger
 */
public final class Column
	extends ArrayList<Measure>
{
	
	
	private Column(int size)
	{
		super(size);
	}
	
	
	public static Column column(Score score, int measure)
	{
		Column ret = new Column(score.getStavesCount());
		for (int iStaff : range(score.getStavesCount()))
		{
			ret.add(score.getStavesList().getStaff(iStaff).getMeasures().get(measure));
		}
		return ret;
	}


}
