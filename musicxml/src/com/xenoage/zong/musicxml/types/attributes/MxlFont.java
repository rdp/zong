package com.xenoage.zong.musicxml.types.attributes;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.StringTools.concatenate;
import static com.xenoage.util.xml.XMLReader.attributeNotNull;

import org.w3c.dom.Element;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.annotations.MaybeEmpty;
import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.enums.MxlFontStyle;
import com.xenoage.zong.musicxml.types.enums.MxlFontWeight;


/**
 * MusicXML font.
 * 
 * @author Andreas Wenger
 */
public final class MxlFont
{
	
	@MaybeEmpty private final PVector<String> fontFamily;
	@MaybeNull private final MxlFontStyle fontStyle;
	@MaybeNull private final MxlFontSize fontSize;
	@MaybeNull private final MxlFontWeight fontWeight;
	
	public static final MxlFont empty = new MxlFont(new PVector<String>(), null, null, null);
	
	
	public MxlFont(PVector<String> fontFamily, MxlFontStyle fontStyle, MxlFontSize fontSize,
		MxlFontWeight fontWeight)
	{
		this.fontFamily = fontFamily;
		this.fontStyle = fontStyle;
		this.fontSize = fontSize;
		this.fontWeight = fontWeight;
	}

	
	/**
	 * Generated from the comma-separated list of font names.
	 */
	@MaybeEmpty public PVector<String> getFontFamily()
	{
		return fontFamily;
	}

	
	@MaybeNull public MxlFontStyle getFontStyle()
	{
		return fontStyle;
	}

	
	@MaybeNull public MxlFontSize getFontSize()
	{
		return fontSize;
	}

	
	@MaybeNull public MxlFontWeight getFontWeight()
	{
		return fontWeight;
	}
	
	
	@NeverNull public static MxlFont read(Element e)
	{
		PVector<String> fontFamily = pvec();
		String fontFamilies = attributeNotNull(e, "font-family");
		for (String s : fontFamilies.split(","))
		{
			fontFamily = fontFamily.plus(s.trim());
		}
		MxlFontStyle fontStyle = MxlFontStyle.read(e);
		MxlFontSize fontSize = MxlFontSize.read(e);
		MxlFontWeight fontWeight = MxlFontWeight.read(e);
		if (fontFamily.size() > 0 || fontStyle != null ||
			fontSize != null || fontWeight != null)
		{
			return new MxlFont(fontFamily, fontStyle, fontSize, fontWeight);
		}
		else
		{
			return empty;
		}
	}
	
	
	public void write(Element e)
	{
		if (fontFamily.size() > 0)
			e.setAttribute("font-family", concatenate(fontFamily, ","));
		if (fontStyle != null)
			fontStyle.write(e);
		if (fontSize != null)
			fontSize.write(e);
		if (fontWeight != null)
			fontWeight.write(e);
	}
	

}
