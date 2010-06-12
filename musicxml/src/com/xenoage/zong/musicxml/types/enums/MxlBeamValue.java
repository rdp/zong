package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.zong.musicxml.util.Parse.getEnumValueNamed;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;


/**
 * MusicXML beam-value.
 * 
 * @author Andreas Wenger
 */
public enum MxlBeamValue
	implements EnumWithXMLNames
{
	
	Begin("begin"),
	Continue("continue"),
	End("end"),
	ForwardHook("forward hook"),
	BackwardHook("backward hook");
	
	
	private final String xmlName;
	
	
	private MxlBeamValue(String xmlName)
	{
		this.xmlName = xmlName;
	}
	
	
	@Override public String getXMLName()
	{
		return xmlName;
	}
	
	
	@MaybeNull public static MxlBeamValue read(Element e)
	{
		return getEnumValueNamed(e.getTextContent(), values());
	}
	
	
	public void write(Element e)
	{
		e.setTextContent(xmlName);
	}

}
