package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.zong.musicxml.util.Parse.getEnumValueNamed;

import com.xenoage.util.annotations.MaybeNull;



/**
 * MusicXML css-font-size.
 * 
 * @author Andreas Wenger
 */
public enum MxlCSSFontSize
	implements EnumWithXMLNames
{
	
	XXSmall("xx-small"),
	XSmall("x-small"),
	Small("small"),
	Medium("medium"),
	Large("large"),
	XLarge("x-large"),
	XXLarge("xx-large");
	
	
	private final String xmlName;
	
	
	private MxlCSSFontSize(String xmlName)
	{
		this.xmlName = xmlName;
	}
	
	
	@Override public String getXMLName()
	{
		return xmlName;
	}
	
	
	@MaybeNull public static MxlCSSFontSize read(String s)
	{
		return getEnumValueNamed(s, values());
	}
	

}
