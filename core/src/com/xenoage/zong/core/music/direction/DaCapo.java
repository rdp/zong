package com.xenoage.zong.core.music.direction;


/**
 * Class for a DaCapo. This element has no sign but only text.
 * 
 * @author Uli Teschemacher
 */
public final class DaCapo
	extends Direction
{

	private final Direction fine;

	
	public DaCapo(Direction fine)
	{
		this.fine = fine;
	}
	
	
	public Direction getFine()
	{
		return fine;
	}
	
}
