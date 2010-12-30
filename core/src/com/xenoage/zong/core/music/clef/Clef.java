package com.xenoage.zong.core.music.clef;

import com.xenoage.zong.core.music.MeasureElement;


/**
 * Class for a clef.
 * 
 * @author Andreas Wenger
 */
public final class Clef
	implements MeasureElement
{

	private final ClefType type;


	/**
	 * Creates a new clef.
	 * @param typeID the type of the clef
	 */
	public Clef(ClefType type)
	{
		this.type = type;
	}


	public ClefType getType()
	{
		return type;
	}


}
