package com.xenoage.zong.io.musicxml.in.readers;

import java.util.EnumSet;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.util.font.FontInfo;
import com.xenoage.util.font.FontStyle;
import com.xenoage.zong.musicxml.types.attributes.MxlFont;
import com.xenoage.zong.musicxml.types.attributes.MxlFontSize;
import com.xenoage.zong.musicxml.types.enums.MxlCSSFontSize;
import com.xenoage.zong.musicxml.types.enums.MxlFontStyle;
import com.xenoage.zong.musicxml.types.enums.MxlFontWeight;


/**
 * This class reads elements containing a font-group into
 * {@link FontInfo} objects.
 * 
 * @author Andreas Wenger
 */
public final class FontInfoReader
{
	
	
	@NeverNull public static FontInfo readFontInfo(MxlFont mxlFont)
	{
		//size
		Float size = null;
		MxlFontSize mxlSize = mxlFont.getFontSize();
		if (mxlSize != null)
		{
			if (mxlSize.isSizePt())
			{
				size = mxlSize.getValuePt();
			}
			else
			{
				size = readCSSFontSize(mxlSize.getValueCSS());
			}
		}
		
		//style and weight
		EnumSet<FontStyle> style = FontStyle.getNone();
		if (mxlFont.getFontStyle() == MxlFontStyle.Italic)
		{
			style.add(FontStyle.Italic);
		}
		if (mxlFont.getFontWeight() == MxlFontWeight.Bold)
		{
			style.add(FontStyle.Bold);
		}
		
		return new FontInfo(mxlFont.getFontFamily(), size, style);
	}
	
	
	private static float readCSSFontSize(MxlCSSFontSize mxlCSSSize)
	{
		switch (mxlCSSSize)
		{
			case XXSmall: return 6f;
			case XSmall: return 8f;
			case Small: return 10f;
			case Medium: return 12f;
			case Large: return 16f;
			case XLarge: return 20f;
			case XXLarge: return 28f;
		}
		throw new IllegalArgumentException("Unknown font-size: " + mxlCSSSize);
	}
	

}
