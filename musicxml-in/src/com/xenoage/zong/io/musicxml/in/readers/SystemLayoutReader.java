package com.xenoage.zong.io.musicxml.in.readers;

import com.xenoage.zong.core.format.SystemLayout;
import com.xenoage.zong.musicxml.types.MxlSystemLayout;
import com.xenoage.zong.musicxml.types.groups.MxlLeftRightMargins;


/**
 * This class reads system-layout elements into
 * {@link SystemLayout} objects.
 * 
 * @author Andreas Wenger
 */
public final class SystemLayoutReader
{
	
	public static final class Value
	{
		public final SystemLayout systemLayout;
		public final Float topSystemDistance;
		
		public Value(SystemLayout systemLayout, Float topSystemDistance)
		{
			this.systemLayout = systemLayout;
			this.topSystemDistance = topSystemDistance;
		}
	}


	/**
	 * Reads a {@link MxlSystemLayout}.
	 */
	public static Value read(MxlSystemLayout mxlSystemLayout, float tenthMm)
	{
		SystemLayout systemLayout = SystemLayout.defaultValue;

		//system-margins
		MxlLeftRightMargins mxlMargins = mxlSystemLayout.getSystemMargins();
		if (mxlMargins != null)
		{
			systemLayout = systemLayout.withSystemMarginLeft(
				tenthMm * mxlMargins.getLeftMargin()).withSystemMarginRight(
					tenthMm * mxlMargins.getRightMargin());
		}

		//system-distance
		Float mxlSystemDistance = mxlSystemLayout.getSystemDistance();
		if (mxlSystemDistance != null)
		{
			systemLayout = systemLayout.withSystemDistance(tenthMm * mxlSystemDistance);
		}
		
		//top-system-distance
		Float topSystemDistance = null;
		Float xmlTopSystemDistance = mxlSystemLayout.getTopSystemDistance();
		if (xmlTopSystemDistance != null)
			topSystemDistance = tenthMm * xmlTopSystemDistance.floatValue();
		
		return new Value(systemLayout, topSystemDistance);
	}

}
