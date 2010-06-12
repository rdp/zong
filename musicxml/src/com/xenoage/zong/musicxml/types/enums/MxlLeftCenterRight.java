package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValue;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;


/**
 * MusicXML left-center-right.
 * 
 * @author Andreas Wenger
 */
public enum MxlLeftCenterRight
{
	
	Left,
	Center,
	Right;
	
	
	@MaybeNull public static MxlLeftCenterRight read(Element e, String attrName)
	{
		String s = attribute(e, attrName);
		if (s != null)
		{
			return throwNull(getEnumValue(s, values()), e);
		}
		else
		{
			return null;
		}
	}
	
	
	public void write(Element e, String attrName)
	{
		e.setAttribute(attrName, toString().toLowerCase());
	}
	

}
