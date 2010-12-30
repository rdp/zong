package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValue;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;


/**
 * MusicXML valign-image.
 * Only named "valign" in the valign-image group.
 * 
 * @author Andreas Wenger
 */
public enum MxlVAlignImage
{
	
	Top,
	Middle,
	Bottom;
	
	
	public static final String ATTR_NAME = "valign";
	
	
	@MaybeNull public static MxlVAlignImage read(Element e)
	{
		String s = attribute(e, ATTR_NAME);
		if (s != null)
		{
			return throwNull(getEnumValue(s, values()), e);
		}
		else
		{
			return null;
		}
	}
	
	
	public void write(Element e)
	{
		e.setAttribute(ATTR_NAME, toString().toLowerCase());
	}
	

}
