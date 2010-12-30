package com.xenoage.zong.musicxml.types.attributes;

import static com.xenoage.util.xml.XMLReader.attribute;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.zong.musicxml.types.enums.MxlCSSFontSize;


/**
 * MusicXML font-size.
 * 
 * @author Andreas Wenger
 */
public final class MxlFontSize
{
	
	public static final String ATTR_NAME = "font-size";
	
	@MaybeNull private final Float valuePt;
	@MaybeNull private final MxlCSSFontSize valueCSS;
	
	
	public MxlFontSize(Float valuePt)
	{
		this.valuePt = valuePt;
		this.valueCSS = null;
	}
	
	
	public MxlFontSize(MxlCSSFontSize valueCSS)
	{
		this.valuePt = null;
		this.valueCSS = valueCSS;
	}


	/**
	 * Gets the font-size in points.
	 * If null, {@link #getValueCSS()} returns a non-null value.
	 */
	@MaybeNull public Float getValuePt()
	{
		return valuePt;
	}


	/**
	 * Gets the font-size as a CSS font size.
	 * If null, {@link #getValueFloat()} returns a non-null value.
	 */
	@MaybeNull public MxlCSSFontSize getValueCSS()
	{
		return valueCSS;
	}
	
	
	/**
	 * Returns true, if size is in pt, otherwise if size is in CSS, false.
	 */
	public boolean isSizePt()
	{
		return valuePt != null;
	}
	
	
	/**
	 * Returns the font-size from the attribute
	 * or returns null, if there is none.
	 */
	@MaybeNull public static MxlFontSize read(Element e)
	{
		String s = attribute(e, ATTR_NAME);
		if (s != null)
		{
			if (Character.isDigit(s.charAt(0)))
			{
				return new MxlFontSize(Float.parseFloat(s));
			}
			else
			{
				return new MxlFontSize(MxlCSSFontSize.read(s));
			}
		}
		else
		{
			return null;
		}
	}
	
	
	public void write(Element e)
	{
		e.setAttribute(ATTR_NAME, (valuePt != null ? ""+valuePt : valueCSS.getXMLName()));
	}
	

}
