package com.xenoage.zong.musicxml.types.enums;

import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.getEnumValue;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML backward-forward.
 * Only named "direction" in repeat element.
 * 
 * @author Andreas Wenger
 */
public enum MxlBackwardForward
{
	
	Backward,
	Forward;
	
	
	public static final String ATTR_NAME = "direction";
	
	
	@NeverNull public static MxlBackwardForward read(Element e)
	{
		String s = attribute(e, ATTR_NAME);
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
		e.setAttribute(ATTR_NAME, toString().toLowerCase());
	}
	

}
