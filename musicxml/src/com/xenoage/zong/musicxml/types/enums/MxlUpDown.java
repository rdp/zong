package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.zong.musicxml.util.Parse.getEnumValue;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;


/**
 * MusicXML up-down.
 * 
 * @author Andreas Wenger
 */
public enum MxlUpDown
{
	
	Up,
	Down;
	
	
	@MaybeNull public static MxlUpDown read(String s, Element parent)
	{
		return getEnumValue(s, values());
	}
	
	
	public String write()
	{
		return toString().toLowerCase();
	}
	

}
