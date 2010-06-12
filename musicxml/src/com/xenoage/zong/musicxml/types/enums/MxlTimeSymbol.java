package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValueNamed;

import org.w3c.dom.Element;


/**
 * MusicXML time-symbol.
 * 
 * @author Andreas Wenger
 */
public enum MxlTimeSymbol
	implements EnumWithXMLNames
{
	
	Common("common"),
	Cut("cut"),
	SingleNumber("single-number"),
	Normal("normal");
	
	public static final String ATTR_NAME = "symbol";
	
	private final String xmlName;
	
	
	private MxlTimeSymbol(String xmlName)
	{
		this.xmlName = xmlName;
	}
	
	
	@Override public String getXMLName()
	{
		return xmlName;
	}
	
	
	/**
	 * Returns the time-symbol from the attribute
	 * or returns null, if there is none.
	 */
	public static MxlTimeSymbol read(Element e)
	{
		String s = attribute(e, ATTR_NAME);
		if (s != null)
		{
			MxlTimeSymbol ret = getEnumValueNamed(s, values());
			return throwNull(ret, e);
		}
		else
		{
			return null;
		}
	}
	
	
	public void write(Element e)
	{
		e.setAttribute(ATTR_NAME, xmlName);
	}
	

}
