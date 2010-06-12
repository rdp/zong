package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.util.xml.XMLReader.text;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValueNamed;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;



/**
 * MusicXML clef-sign.
 * 
 * @author Andreas Wenger
 */
public enum MxlClefSign
	implements EnumWithXMLNames
{
	
	G("G"),
	F("F"),
	C("C"),
	Percussion("percussion"),
	TAB("TAB"),
	None("none");
	
	
	public static final String ELEM_NAME = "sign";
	
	private final String xmlName;
	
	
	private MxlClefSign(String xmlName)
	{
		this.xmlName = xmlName;
	}
	
	
	@Override public String getXMLName()
	{
		return xmlName;
	}
	
	
	@NeverNull public static MxlClefSign read(Element e)
	{
		return throwNull(getEnumValueNamed(text(e), values()), e);
	}
	
	
	public void write(Element parent)
	{
		addElement(ELEM_NAME, xmlName, parent);
	}
	

}
