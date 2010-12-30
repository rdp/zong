package com.xenoage.zong.musicxml.types.choice;

import org.w3c.dom.Element;

import com.xenoage.zong.musicxml.types.MxlNotations;


/**
 * Interface for all elements that can be children of
 * {@link MxlNotations}.
 * 
 * @author Andreas Wenger
 */
public interface MxlNotationsContent
{
	
	/**
	 * This enum allows using quick switch-case statements
	 * for finding out the type of the element.
	 */
	public enum MxlNotationsContentType
	{
		Articulations,
		Dynamics,
		/**
		 * Slur and tied.
		 */
		CurvedLine;
	}
	
	
	/**
	 * Gets the type of this {@link MxlNotationsContent}.
	 */
	public MxlNotationsContentType getNotationsContentType();
	
	
	public void write(Element parent);

}
