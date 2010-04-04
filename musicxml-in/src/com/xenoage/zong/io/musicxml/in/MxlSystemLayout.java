package com.xenoage.zong.io.musicxml.in;

import java.math.BigDecimal;

import proxymusic.SystemMargins;

import com.xenoage.zong.core.format.SystemLayout;


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
		SystemLayout defaultSystemLayout = SystemLayout.getDefault();

		//system-margins
		float systemMarginLeft, systemMarginRight;
		SystemMargins mxlSystemMargins = mxlSystemLayout.getSystemMargins();
		if (mxlSystemMargins != null)
		{
			systemMarginLeft = tenthsMm * mxlSystemMargins.getLeftMargin().floatValue();
			systemMarginRight = tenthsMm * mxlSystemMargins.getRightMargin().floatValue();
		}
		else
		{
			systemMarginLeft = defaultSystemLayout.getSystemMarginLeft();
			systemMarginRight = defaultSystemLayout.getSystemMarginRight();
		}

		//system-distance
		float systemDistance;
		BigDecimal mxlSystemDistance = mxlSystemLayout.getSystemDistance();
		if (mxlSystemDistance != null)
		{
			systemDistance = tenthsMm * mxlSystemDistance.floatValue();
		}
		else
		{
			systemDistance = defaultSystemLayout.getSystemDistance();
		}
		
		//top-system-distance
		BigDecimal xmlTopSystemDistance = mxlSystemLayout.getTopSystemDistance();
		if (xmlTopSystemDistance != null)
			topSystemDistance = tenthsMm * xmlTopSystemDistance.floatValue();
		else
			topSystemDistance = null;
		
		systemLayout = new SystemLayout(systemDistance, systemMarginLeft,
			systemMarginRight, null);
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
