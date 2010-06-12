package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValueNamed;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;


/**
 * MusicXML margin-type.
 * 
 * @author Andreas Wenger
 */
public enum MxlMarginType
	implements EnumWithXMLNames
{
	
	Odd("odd"),
	Even("even"),
	Both("both");
	
	
	public static final String ATTR_NAME = "type";
	
	private final String xmlName;
	
	
	private MxlMarginType(String xmlName)
	{
		this.xmlName = xmlName;
	}
	
	
	@Override public String getXMLName()
	{
		return xmlName;
	}
	
	
	@MaybeNull public static MxlMarginType read(Element e)
	{
		return getEnumValueNamed(attribute(e, ATTR_NAME), values());
	}
	
	
	public void write(Element e)
	{
		addAttribute(e, ATTR_NAME, xmlName);
	}

}
