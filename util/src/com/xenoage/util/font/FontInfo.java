package com.xenoage.util.font;

import static com.xenoage.util.NullTools.notNull;

import java.awt.Font;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;


/**
 * Information about a font.
 * 
 * Other than an AWT font, this class allows to save multiple
 * font names/families and allows the attributes to be unset.
 * 
 * @author Andreas Wenger
 */
public final class FontInfo
{
	
	private static Font defaultFont = new Font("Serif", Font.PLAIN, 12);
	
	private final List<String> families;
	private final Float size;
	private final EnumSet<FontStyle> style;
	
	
	/**
	 * Creates a new {@link FontInfo}.
	 * @param families  list of family names. May also be empty or null.
	 * @param size      size of the font in pt. May be null.
	 * @param style     set of styles. Empty for normal style, or null for unknown.
	 */
	public FontInfo(List<String> families, Float size, EnumSet<FontStyle> style)
	{
		this.families = (families != null ? families : new LinkedList<String>());
		this.size = size;
		this.style = notNull(style, EnumSet.noneOf(FontStyle.class));
	}
	
	
	/**
	 * Creates a new {@link FontInfo}.
	 * @param family    family name. May also be null.
	 * @param size      size of the font in pt. May be null.
	 * @param style     set of styles. Empty for normal style, or null for unknown.
	 */
	public FontInfo(String family, Float size, EnumSet<FontStyle> style)
	{
		List<String> f = new LinkedList<String>();
		if (family != null)
			f.add(family);
		this.families = f;
		this.size = size;
		this.style = style;
	}
	
	
	/**
	 * Creates a new {@link FontInfo}.
	 * @param size      size of the font in pt. May be null.
	 */
	public FontInfo(Float size)
	{
		this((String) null, size, null);
	}
	
	
	/**
	 * Creates a new {@link FontInfo}.
	 * @param font      the AWT font
	 */
	public FontInfo(Font font)
	{
		List<String> f = new LinkedList<String>();
		f.add(font.getFamily());
		this.families = f;
		this.size = font.getSize2D();
		EnumSet<FontStyle> style = EnumSet.noneOf(FontStyle.class);
		if (font.isBold())
			style.add(FontStyle.Bold);
		if (font.isItalic())
			style.add(FontStyle.Italic);
		this.style = style;
	}
	
	
	/**
	 * Merges this font information with the given one.
	 * The font families, size and style is only taken from
	 * the given font if they are unknown for this font.
	 */
	public FontInfo merge(FontInfo f)
	{
		return new FontInfo(notNull(this.families, f.families),
			notNull(this.size, f.size), notNull(this.style, f.style));
	}

	
	public List<String> getFamilies()
	{
		return families;
	}

	
	public Float getSize()
	{
		return size;
	}

	
	public EnumSet<FontStyle> getStyle()
	{
		return style;
	}
	
	
	/**
	 * Gets the {@link Font} that matches best to the values of this object.
	 */
	public Font createFont()
	{
		return createFont(defaultFont);
	}
	
	
	/**
	 * Gets the {@link Font} that matches best to the values of this object.
	 * Unset attributes are taken from the given base font.
	 */
	public Font createFont(Font baseFont)
	{
		
		//find an appropriate family:
		//go through all families, until a known family is found. if no family
		//is found, look for replacements. If also not found, take the base font family.
		String fontFamily = null;
		for (String family : families)
		{
			if (FontTools.getInstance().isFontFamilySupported(family))
			{
				fontFamily = family;
				break;
			}
		}
		if (fontFamily == null)
		{
			for (String family : families)
			{
				String replacement = FontReplacements.getInstance().getReplacement(family);
				if (replacement != family && FontTools.getInstance().isFontFamilySupported(replacement))
				{
					fontFamily = replacement;
					break;
				}
			}
		}
		if (fontFamily == null)
		{
			fontFamily = baseFont.getFamily();
		}
		
		//size
		float fontSize = (size != null ? size : baseFont.getSize2D());
		
		//style
		int fontStyle = Font.PLAIN;
		if (style != null)
		{
			fontStyle |= (style.contains(FontStyle.Bold) ? Font.BOLD : 0);
			fontStyle |= (style.contains(FontStyle.Italic) ? Font.ITALIC : 0);
		}
		else
		{
			fontStyle = baseFont.getStyle();
		}
		
		return new Font(fontFamily, fontStyle, Math.round(fontSize));
	}
	

}
