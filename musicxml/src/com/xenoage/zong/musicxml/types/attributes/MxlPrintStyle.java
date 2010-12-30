package com.xenoage.zong.musicxml.types.attributes;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML print-style.
 * 
 * @author Andreas Wenger
 */
public final class MxlPrintStyle
{
	
	@NeverNull private final MxlPosition position;
	@NeverNull private final MxlFont font;
	@MaybeNull private final MxlColor color;
	
	public static final MxlPrintStyle empty = new MxlPrintStyle(MxlPosition.empty,
		MxlFont.empty, null);
	
	
	public MxlPrintStyle(MxlPosition position, MxlFont font, MxlColor color)
	{
		this.position = position;
		this.font = font;
		this.color = color;
	}

	
	@NeverNull public MxlPosition getPosition()
	{
		return position;
	}

	
	@NeverNull public MxlFont getFont()
	{
		return font;
	}

	
	@MaybeNull public MxlColor getColor()
	{
		return color;
	}
	
	
	@NeverNull public static MxlPrintStyle read(Element e)
	{
		MxlPosition position = MxlPosition.read(e);
		MxlFont font = MxlFont.read(e);
		MxlColor color = MxlColor.read(e);
		if (position != MxlPosition.empty || font != MxlFont.empty || color != null)
			return new MxlPrintStyle(position, font, color);
		else
			return empty;
	}
	
	
	public void write(Element e)
	{
		if (this != empty)
		{
			position.write(e);
			font.write(e);
			if (color != null)
				color.write(e);
		}
	}
	

}
