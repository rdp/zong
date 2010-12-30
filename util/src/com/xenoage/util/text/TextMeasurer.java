package com.xenoage.util.text;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import com.xenoage.util.Units;


/**
 * This class provides a method to calculate the width of a given String with a given font
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class TextMeasurer
{
	
	private final TextLayout layout;
	
	
	/**
	 * Creates a measurer for the given {@link Font}.
	 */
	public TextMeasurer(Font font, String text)
	{
		layout = new TextLayout(text, font, new FontRenderContext(null, false, true));
	}
	
	
	/**
	 * Gets the ascent of this font in mm.
	 */
	public float getAscent()
	{
		return Units.pxToMm(layout.getAscent(), 1);
	}
	
	
	/**
	 * Gets the descent of this font in mm.
	 */
	public float getDescent()
	{
		return Units.pxToMm(layout.getDescent(), 1);
	}
	
	
	/**
	 * Gets the leading of this font in mm.
	 */
	public float getLeading()
	{
		return Units.pxToMm(layout.getLeading(), 1);
	}
	
	
	/**
	 * Measure the width of this text in mm.
	 */
	public float getWidth()
	{
		return Units.pxToMm((float)layout.getBounds().getWidth(), 1);
	}

	
}
