package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValue;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;


/**
 * MusicXML placement.
 * 
 * @author Andreas Wenger
 */
public enum MxlPlacement
{
	
	Above,
	Below;
	
	
	public static final String ATTR_NAME = "placement";
	
	
	@MaybeNull public static MxlPlacement read(Element e)
	{
		String s = attribute(e, ATTR_NAME);
		return getEnumValue(s, values());
	}
	
	
	public void write(Element e)
	{
		addAttribute(e, ATTR_NAME, toString().toLowerCase());
	}

}
