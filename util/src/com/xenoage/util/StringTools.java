package com.xenoage.util;


/**
 * Functions to work with strings.
 *
 * @author Andreas Wenger
 */
public class StringTools
{
	
	public static final char[] LINEBREAK_CHARS = new char[]{' ', '-', '–'};
	
	
	/**
	 * Returns a copy of the string, with trailing spaces omitted.
	 */
	public static String trimRight(String s)
	{
		for (int i = s.length() - 1; i >= 0; i--)
		{
			if (s.charAt(i) != ' ')
				return s.substring(0, i + 1);
		}
		return "";
	}
	
	
	/**
	 * Returns true, if the given character is a line break character,
	 * otherwise false.
	 */
	public static boolean isLineBreakCharacter(char c)
	{
		for (char lbc : LINEBREAK_CHARS)
			if (lbc == c)
				return true;
		return false;
	}
	
	
	/**
	 * Returns true, if there is a line break character in the given string,
	 * otherwise false.
	 */
	public static boolean containsLineBreakCharacter(String s)
	{
		for (int i = 0; i < s.length(); i++)
			if (isLineBreakCharacter(s.charAt(i)))
				return true;
		return false;
	}
	
	
	/**
	 * Returns count times the given string concatenated.
	 */
	public static String repeat(String s, int count)
	{
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < count; i++)
			ret.append(s);
		return ret.toString();
	}

}
