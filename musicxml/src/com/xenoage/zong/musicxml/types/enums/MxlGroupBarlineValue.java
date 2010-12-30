package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.util.xml.XMLReader.text;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.util.Parse;


/**
 * MusicXML group-barline-value.
 * 
 * @author Andreas Wenger
 */
public enum MxlGroupBarlineValue
	implements EnumWithXMLNames
{
	
	Yes("yes"),
	No("no"),
	Mensurstrich("Mensurstrich");
	
	
	private final String xmlName;
	
	
	private MxlGroupBarlineValue(String xmlName)
	{
		this.xmlName = xmlName;
	}
	
	
	@Override public String getXMLName()
	{
		return xmlName;
	}
	
	
	@NeverNull public static MxlGroupBarlineValue read(Element e)
	{
		String s = text(e);
		if (s != null)
		{
			return throwNull(Parse.getEnumValueNamed(s, values()), e);
		}
		else
		{
			throw invalid(e);
		}
	}
	
	
	public void write(Element e)
	{
		e.setTextContent(xmlName);
	}
	

}
