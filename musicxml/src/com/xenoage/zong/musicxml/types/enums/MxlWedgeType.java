package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValue;

import org.w3c.dom.Element;


/**
 * MusicXML wedge-type.
 * 
 * @author Andreas Wenger
 */
public enum MxlWedgeType
{
	
	Crescendo,
	Diminuendo,
	Stop;
	
	
	public static MxlWedgeType read(String s, Element parent)
	{
		return throwNull(getEnumValue(s, values()), parent);
	}
	
	
	public String write()
	{
		return toString().toLowerCase();
	}
	

}
