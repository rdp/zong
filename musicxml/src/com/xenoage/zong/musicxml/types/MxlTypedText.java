package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.util.xml.XMLReader.text;
import static com.xenoage.util.xml.XMLWriter.addAttribute;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML typed-text.
 * 
 * @author Andreas Wenger
 */
public final class MxlTypedText
{
	
	@NeverNull private final String value;
	@MaybeNull private final String type;
	
	
	public MxlTypedText(String value, String type)
	{
		this.value = value;
		this.type = type;
	}

	
	@NeverNull public String getValue()
	{
		return value;
	}

	
	@MaybeNull public String getType()
	{
		return type;
	}
	
	
	@NeverNull public static MxlTypedText read(Element e)
	{
		return new MxlTypedText(text(e), attribute(e, "type"));
	}
	
	
	public void write(Element e)
	{
		e.setTextContent(value);
		addAttribute(e, "type", type);
	}
	

}
