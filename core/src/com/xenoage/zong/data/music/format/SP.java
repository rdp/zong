package com.xenoage.zong.data.music.format;


/**
 * Staff position.
 * 
 * This means a position with a horizontal coordinate in mm
 * and a vertical coordinate as a line position (LP).
 * 
 * @author Andreas Wenger
 */
public final class SP
{
	
	public final float xMm;
	public final float yLp;
	
	
	public SP(float xMm, float yLp)
	{
		this.xMm = xMm;
		this.yLp = yLp;
	}
	

}
