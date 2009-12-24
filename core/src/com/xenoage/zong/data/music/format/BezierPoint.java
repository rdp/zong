package com.xenoage.zong.data.music.format;


/**
 * Formatting of an endpoint of a bezier curve.
 * 
 * @author Andreas Wenger
 */
public final class BezierPoint
{
	
	public final SP point;
	public final SP control;
	
	
	/**
	 * Creates a new bezier point.
	 * @param point    the position of the end point relative to a reference point
	 * @param control  the position of the control point relative to the end point
	 */
	public BezierPoint(SP point, SP control)
	{
		this.point = point;
		this.control = control;
	}
	

}
