package com.xenoage.zong.data.text;

import static com.xenoage.util.NullTools.notNull;

import java.awt.Color;
import java.awt.Font;
import java.util.EnumSet;

import com.xenoage.util.font.FontInfo;
import com.xenoage.util.font.FontStyle;


/**
 * Style of a {@link FormattedTextString}.
 * 
 * TODO: add derive-methods which return new FormattedTextStyle instances.
 * 
 * Default style: Serif 12pt normal black.
 *
 * @author Andreas Wenger
 */
public class FormattedTextStyle
{
	
	//default values
	private static final Font defaultFont = new Font("Times New Roman", Font.PLAIN, 10);
	private static final Superscript defaultSuperscript = Superscript.Normal;
	private static final Color defaultColor = Color.black;
  
  //style properties
  private final FontInfo fontInfo; //may be null when font was given
  private final Color color;
  private final Superscript superscript;
  
  //cache
  private Font font = null;
  
  
  /**
   * Creates a new {@link FormattedTextStyle} with the given font information.
   */
  public FormattedTextStyle(FontInfo fontInfo, Color color, Superscript superscript)
  {
  	this.fontInfo = fontInfo;
  	if (fontInfo == null)
  	{
  		this.font = defaultFont;
  	}
  	this.color = notNull(color, defaultColor);
  	this.superscript = notNull(superscript, defaultSuperscript);
  }
  
  
  /**
   * Creates a new {@link FormattedTextStyle} with the given font information.
   * For the missing information the default style is used.
   */
  public FormattedTextStyle(FontInfo fontInfo)
  {
  	this(fontInfo, defaultColor, defaultSuperscript);
  }
  
  
  /**
   * Creates a new {@link FormattedTextStyle} with the given font size.
   * For the missing information the default style is used.
   */
  public FormattedTextStyle(float fontSize)
  {
  	this(new FontInfo(fontSize), defaultColor, defaultSuperscript);
  }
  
  
  /**
   * Creates a new {@link FormattedTextStyle} with the given font information.
   * For the missing information the default style is used.
   */
  public FormattedTextStyle(Font font, Color color, Superscript superscript)
  {
  	this.fontInfo = null; //not needed, since font is given
  	this.color = notNull(color, defaultColor);
  	this.superscript = notNull(superscript, defaultSuperscript);
  	this.font = notNull(font, defaultFont);
  }
  
  
  /**
   * Creates a new {@link FormattedTextStyle} with the given font information.
   * For the missing information the default style is used.
   */
  public FormattedTextStyle(Font font)
  {
  	this(font, null, null);
  }

  
  /**
   * Creates a new {@link FormattedTextStyle} with the given font information.
   * For the missing information the default style is used.
   */
  public FormattedTextStyle()
  {
  	this.fontInfo = null; //not needed, since font is given
  	this.superscript = defaultSuperscript;
  	this.color = defaultColor;
  	this.font = defaultFont;
  }
  
  
  /**
   * Gets the font used by this style.
   */
  public Font getFont()
  {
  	if (font == null)
  	{
  		font = fontInfo.createFont(defaultFont);
  	}
  	return font;
  }
  
  
  /**
   * Gets the color (never null).
   */
  public Color getColor()
  {
  	return color;
  }
  
  
  /**
   * Gets the {@link Superscript} style (never null).
   */
  public Superscript getSuperscript()
  {
  	return superscript;
  }
  
  
  /**
   * Gets the {@link FontStyle}s (never null).
   */
  public EnumSet<FontStyle> getFontStyle()
  {
  	if (fontInfo != null)
  	{
  		return notNull(fontInfo.getStyle(), FontStyle.getNone());
  	}
  	else
  	{
  		return FontStyle.fromFont(font);
  	}
  }
  
  
  /**
   * Gets the default font for {@link FormattedTextStyle}.
   */
  public static Font getDefaultFont()
  {
  	return defaultFont;
  }
  

}
