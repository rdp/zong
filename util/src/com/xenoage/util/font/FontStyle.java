package com.xenoage.util.font;

import java.awt.Font;
import java.util.EnumSet;


/**
 * Enumeration of different font styles like bold or italic.
 * 
 * @author Andreas Wenger
 */
public enum FontStyle
{
	Bold,
	Italic,
	Underline,
	Strikethrough;
	
	
	public static EnumSet<FontStyle> getNone()
	{
		return EnumSet.noneOf(FontStyle.class);
	}
	
	
	public static EnumSet<FontStyle> fromFont(Font font)
	{
		EnumSet<FontStyle> ret = EnumSet.noneOf(FontStyle.class);
		if (font.isBold()) ret.add(Bold);
		if (font.isItalic()) ret.add(Italic);
		return ret;
	}
	
}
