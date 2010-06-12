package com.xenoage.zong.musicxml.types.attributes;

import static com.xenoage.util.ColorTools.getColor;
import static com.xenoage.util.ColorTools.getHex;
import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML color.
 * 
 * @author Andreas Wenger
 */
public final class MxlColor
{
	
	public static final String ATTR_NAME = "color";
	
	@NeverNull private final java.awt.Color value;

	
	public MxlColor(java.awt.Color value)
	{
		this.value = value;
	}

	
	@NeverNull public java.awt.Color getValue()
	{
		return value;
	}
	
	
	@MaybeNull public static MxlColor read(Element e)
	{
		String s = attribute(e, ATTR_NAME);
		if (s != null)
		{
			try
			{
				return new MxlColor(getColor(s));
			}
			catch (NumberFormatException ex)
			{
				throw invalid(e);
			}
		}
		else
		{
			return null;
		}
	}
	
	
	public void write(Element e)
	{
		e.setAttribute(ATTR_NAME, getHex(value));
	}

}
