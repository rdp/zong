package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.util.xml.XMLReader.text;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValue;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML group-symbol-value.
 * 
 * @author Andreas Wenger
 */
public enum MxlGroupSymbolValue
{
	
	None,
	Brace,
	Line,
	Bracket;
	
	
	@NeverNull public static MxlGroupSymbolValue read(Element e)
	{
		String s = text(e);
		if (s != null)
		{
			return throwNull(getEnumValue(s, values()), e);
		}
		else
		{
			throw invalid(e);
		}
	}
	
	
	public void write(Element e)
	{
		e.setTextContent(toString().toLowerCase());
	}
	

}
