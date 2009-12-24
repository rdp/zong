package com.xenoage.zong.util;

import java.awt.Color;


/**
 * Useful functions for working with {@link Color}.
 *
 * @author Andreas Wenger
 */
public class ColorTools
{
	
	
	/**
	 * Returns the hexadecimal HTML-style value of a color
	 * with a leading # (format: #rrggbb).
	 */
	public static String toHTMLColor(Color color)
	{
		String rgb = Integer.toHexString(color.getRGB());
		rgb = "#" + rgb.substring(2, rgb.length());
		return rgb;
	}


}
