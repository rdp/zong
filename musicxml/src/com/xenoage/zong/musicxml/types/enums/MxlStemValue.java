package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.util.xml.XMLReader.text;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValue;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;


/**
 * MusicXML stem-value.
 * 
 * @author Andreas Wenger
 */
public enum MxlStemValue
{
	
	Down,
	Up,
	Double,
	None;
	
	
	@MaybeNull public static MxlStemValue read(Element e)
	{
		String s = text(e);
		return throwNull(getEnumValue(s, values()), e);
	}
	
	
	public void write(Element e)
	{
		e.setTextContent(toString().toLowerCase());
	}

}
