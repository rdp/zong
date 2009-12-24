package com.xenoage.zong.io.musicxml.in;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import proxymusic.EmptyFont;
import proxymusic.FormattedText;
import proxymusic.LyricFont;

import com.xenoage.util.font.FontInfo;
import com.xenoage.util.font.FontStyle;


/**
 * This class reads elements containing a font-group into
 * {@link FontInfo} objects.
 * 
 * @author Andreas Wenger
 */
class MxlFontInfo
{
	
	private final FontInfo fontInfo;
	
	
	public MxlFontInfo(EmptyFont font)
	{
		this(font.getFontFamily(), font.getFontSize(), font.getFontStyle(), font.getFontWeight());
	}
	
	
	public MxlFontInfo(LyricFont font)
	{
		this(font.getFontFamily(), font.getFontSize(), font.getFontStyle(), font.getFontWeight());
	}
	
	
	public MxlFontInfo(FormattedText formattedText)
	{
		this(formattedText.getFontFamily(), formattedText.getFontSize(), formattedText.getFontStyle(), formattedText.getFontWeight());
	}
	
	
	private MxlFontInfo(String xmlFamilies, String xmlSize,
		proxymusic.FontStyle xmlStyle, proxymusic.FontWeight xmlWeight)
	{
		//families
		List<String> families = null;
		if (xmlFamilies != null)
		{
			families = Arrays.asList(xmlFamilies.split(","));
		}
		
		//size
		Float size = null;
		if (xmlSize != null && xmlSize.length() > 0)
		{
			if (Character.isDigit(xmlSize.charAt(0)))
			{
				size = Float.parseFloat(xmlSize);
			}
			else
			{
				size = readCSSFontSize(xmlSize);
			}
		}
		
		//style and weight
		EnumSet<FontStyle> style = FontStyle.getNone();
		if (xmlStyle == proxymusic.FontStyle.ITALIC)
		{
			style.add(FontStyle.Italic);
		}
		if (xmlWeight == proxymusic.FontWeight.BOLD)
		{
			style.add(FontStyle.Bold);
		}
		
		fontInfo = new FontInfo(families, size, style);
	}
	
	
	/**
	 * Gets the font information that was read.
	 */
	public FontInfo getFontInfo()
	{
		return fontInfo;
	}
	
	
	private float readCSSFontSize(String size)
	{
		if (size.equals("xx-small"))
			return 6f;
		if (size.equals("x-small"))
			return 8f;
		if (size.equals("small"))
			return 10f;
		if (size.equals("medium"))
			return 12f;
		if (size.equals("large"))
			return 16f;
		if (size.equals("x-large"))
			return 20f;
		if (size.equals("xx-large"))
			return 28f;
		throw new IllegalArgumentException("Unknown font-size: " + size);
	}
	

}
