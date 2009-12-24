package com.xenoage.zong.data.music.format;


/**
 * Custom positioning of an object.
 * 
 * @author Andreas Wenger
 */
public class Position
{
	
	public final Float x, y, relativeX, relativeY;
	
	
	/**
	 * Creates a new custom position.
	 * @param x          the x coordinate in mm, or null for default
	 * @param y          the y coordinate in LP, or null for default
	 * @param relativeX  an additional horizontal offset in mm, or null for 0
	 * @param relativeY  an additional vertical offset in LP, or null for 0
	 */
	public Position(Float x, Float y, Float relativeX, Float relativeY)
	{
		this.x = x;
		this.y = y;
		this.relativeX = relativeX;
		this.relativeY = relativeY;
	}
	
	
	/**
	 * Returns the additional horizontal offset of the given {@link Position},
	 * or 0 if the position is null or the offset is null.
	 */
	public static float getRelativeX(Position p)
	{
		return (p == null || p.relativeX == null ? 0 : p.relativeX);
	}
	
	
	/**
	 * Returns the additional vertical offset of the given {@link Position},
	 * or 0 if the position is null or the offset is null.
	 */
	public static float getRelativeY(Position p)
	{
		return (p == null || p.relativeY == null ? 0 : p.relativeY);
	}
	

}
