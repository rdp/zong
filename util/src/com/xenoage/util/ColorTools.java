package com.xenoage.util;

import java.awt.Color;


/**
 * Useful methods to work with colors.
 * 
 * @author Andreas Wenger
 */
public class ColorTools
{
	
	/**
	 * Reads a hexadecimal color in either the form
	 * "#aarrggbb" or "#rrggbb".
	 * a, r, g and b may be lowercase or uppercase.
	 */
	public static Color getColor(String s)
		throws NumberFormatException
	{
		try
		{
			boolean withAlpha = (s.length() == 9);
			if (s.length() != 7 && !withAlpha)
			{
				throw new IllegalArgumentException("Illegal length");
			}
			int a = (withAlpha ? Integer.parseInt(s.substring(1, 3), 16) : 0xFF);
			int offset = (withAlpha ? 2 : 0);
			int r = Integer.parseInt(s.substring(offset + 1, offset + 3), 16);
			int g = Integer.parseInt(s.substring(offset + 3, offset + 5), 16);
			int b = Integer.parseInt(s.substring(offset + 5, offset + 7), 16);
			return new Color(r, g, b, a);
		}
		catch (RuntimeException ex)
		{
			throw new NumberFormatException();
		}
	}
	
	
	/**
	 * Gets the given color as a hex string in either the form
	 * "#aarrggbb" if alpha < 0xFF, or "#rrggbb" otherwise.
	 * a, r, g and b are lowercase.
	 */
	public static String getHex(Color color)
	{
		StringBuilder s = new StringBuilder("#");
		if (color.getAlpha() < 0xFF)
		{
			s.append(toHex2Digits(color.getAlpha()));
		}
		s.append(toHex2Digits(color.getRed()));
		s.append(toHex2Digits(color.getGreen()));
		s.append(toHex2Digits(color.getBlue()));
		return s.toString();
	}
	
	
	private static String toHex2Digits(int i)
	{
		String ret = Integer.toHexString(i);
		return (ret.length() == 1 ? "0" + ret : ret);
	}

}
