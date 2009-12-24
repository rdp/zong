package com.xenoage.zong.io.musicxml.in;

import java.math.BigDecimal;

import proxymusic.SystemMargins;

import com.xenoage.zong.data.format.SystemLayout;


/**
 * This class reads system-layout elements into
 * {@link SystemLayout} objects.
 * 
 * @author Andreas Wenger
 */
class MxlSystemLayout
{
	
	private final SystemLayout systemLayout;
	private final Float topSystemDistance;


	/**
	 * Reads a system-layout element.
	 */
	public MxlSystemLayout(proxymusic.SystemLayout mxlSystemLayout, float tenthsMm)
	{
		systemLayout = SystemLayout.createEmptySystemLayout();

		//system-margins
		SystemMargins mxlSystemMargins = mxlSystemLayout.getSystemMargins();
		if (mxlSystemMargins != null)
		{
			systemLayout.setSystemMarginLeft(tenthsMm * mxlSystemMargins.getLeftMargin().floatValue());
			systemLayout.setSystemMarginRight(tenthsMm * mxlSystemMargins.getRightMargin().floatValue());
		}

		//system-distance
		BigDecimal mxlSystemDistance = mxlSystemLayout.getSystemDistance();
		if (mxlSystemDistance != null)
			systemLayout.setSystemDistance(tenthsMm * mxlSystemDistance.floatValue());
		
		//top-system-distance
		BigDecimal xmlTopSystemDistance = mxlSystemLayout.getTopSystemDistance();
		if (xmlTopSystemDistance != null)
			topSystemDistance = tenthsMm * xmlTopSystemDistance.floatValue();
		else
			topSystemDistance = null;
	}
	
	
	/**
	 * Gets the {@link SystemLayout}.
	 */
	public SystemLayout getSystemLayout()
	{
		return systemLayout;
	}
	
	
	/**
	 * Gets the top system distance in mm, or null if unknown.
	 */
	public Float getTopSystemDistance()
	{
		return topSystemDistance;
	}

}
