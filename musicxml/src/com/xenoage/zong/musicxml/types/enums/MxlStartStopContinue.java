package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValue;

import org.w3c.dom.Element;


/**
 * MusicXML start-stop-continue.
 * 
 * @author Andreas Wenger
 */
public enum MxlStartStopContinue
{
	
	Start,
	Stop,
	Continue;
	
	
	public static MxlStartStopContinue read(String s, Element parent)
	{
		return throwNull(getEnumValue(s, values()), parent);
	}
	
	
	public String write()
	{
		return toString().toLowerCase();
	}
	

}
