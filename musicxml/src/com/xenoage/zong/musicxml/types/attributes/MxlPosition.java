package com.xenoage.zong.musicxml.types.attributes;

import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.zong.musicxml.util.Parse.parseAttrFloatNull;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML x-position and y-position.
 * 
 * @author Andreas Wenger
 */
public final class MxlPosition
{

	@MaybeNull private final Float defaultX, defaultY, relativeX, relativeY;
	
	public static final MxlPosition empty = new MxlPosition(null, null, null, null);
	
	
	public MxlPosition(Float defaultX, Float defaultY, Float relativeX, Float relativeY)
	{
		this.defaultX = defaultX;
		this.defaultY = defaultY;
		this.relativeX = relativeX;
		this.relativeY = relativeY;
	}

	
	@MaybeNull public Float getDefaultX()
	{
		return defaultX;
	}

	
	@MaybeNull public Float getDefaultY()
	{
		return defaultY;
	}

	
	@MaybeNull public Float getRelativeX()
	{
		return relativeX;
	}

	
	@MaybeNull public Float getRelativeY()
	{
		return relativeY;
	}


	@NeverNull public static MxlPosition read(Element e)
	{
		Float defaultX = parseAttrFloatNull(e, "default-x");
		Float defaultY = parseAttrFloatNull(e, "default-y");
		Float relativeX = parseAttrFloatNull(e, "relative-x");
		Float relativeY = parseAttrFloatNull(e, "relative-y");
		if (defaultX != null || defaultY != null ||
			relativeX != null || relativeY != null)
		{
			return new MxlPosition(defaultX, defaultY, relativeX, relativeY);
		}
		else
		{
			return empty;
		}
	}
	
	
	public void write(Element e)
	{
		if (this != empty)
		{
			addAttribute(e, "default-x", defaultX);
			addAttribute(e, "default-y", defaultY);
			addAttribute(e, "relative-x", relativeX);
			addAttribute(e, "relative-y", relativeY);
		}
	}
	

}
