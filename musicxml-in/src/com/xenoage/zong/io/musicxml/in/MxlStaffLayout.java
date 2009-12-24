package com.xenoage.zong.io.musicxml.in;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.xenoage.zong.data.format.StaffLayout;


/**
 * This class reads a staff-layout element into a
 * {@link StaffLayout} object.
 * 
 * @author Andreas Wenger
 */
class MxlStaffLayout
{

	private StaffLayout staffLayout;
	private Integer number;
	
	
	/**
	 * Reads a staff-layout element.
	 */
	public MxlStaffLayout(proxymusic.StaffLayout mxlStaffLayout, float tenthsMm)
	{
		staffLayout = StaffLayout.createEmptyStaffLayout();
		
		//staff-distance
		BigDecimal mxlStaffDistance = mxlStaffLayout.getStaffDistance();
		if (mxlStaffDistance != null)
		{
			staffLayout.setStaffDistance(tenthsMm * mxlStaffDistance.floatValue());
		}
		
		//number
		BigInteger mxlNumber = mxlStaffLayout.getNumber();
		number = (mxlNumber != null ? mxlNumber.intValue() : null);
	}

	
	/**
	 * Gets the read {@link StaffLayout}.
	 */
	public StaffLayout getStaffLayout()
	{
		return staffLayout;
	}
	
	
	/**
	 * Gets the number of the staff, or null if unknown.
	 * It refers to staff numbers within the part, from top to bottom on the system.
	 */
	public Integer getNumber()
	{
		return number;
	}


}
