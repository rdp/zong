package com.xenoage.zong.musicxml.util;

import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.util.xml.XMLReader.element;
import static com.xenoage.util.xml.XMLReader.text;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;

import org.w3c.dom.Element;

import com.xenoage.zong.musicxml.types.enums.EnumWithXMLNames;


/**
 * Useful parsing methods.
 * 
 * @author Andreas Wenger
 */
public final class Parse
{
	
	
	/**
	 * Gets the enum value from the given array, that
	 * matches (lower case) the given text, or null if not found.
	 */
	public static <T> T getEnumValue(String text, T[] values)
	{
		if (text != null)
		{
			for (T value : values)
			{
				if (text.equals(value.toString().toLowerCase()))
				{
					return value;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Gets the enum value from the given array, that
	 * matches (lower case) the given text, or null if not found.
	 */
	public static <T extends EnumWithXMLNames> T getEnumValueNamed(String text, T[] values)
	{
		if (text != null)
		{
			for (T value : values)
			{
				if (text.equals(value.getXMLName()))
				{
					return value;
				}
			}
		}
		return null;
	}

	
	/**
	 * Gets the text of the child element with the given name
	 * of the given parent element as an float.
	 */
	public static float parseChildFloat(Element parent, String eName)
	{
		Element e = element(parent, eName);
		if (e != null)
		{
			return parseFloat(e);
		}
		throw invalid(e);
	}
	
	
	/**
	 * Gets the text of the child element with the given name
	 * of the given parent element as an int.
	 */
	public static int parseChildInt(Element parent, String eName)
	{
		Element e = element(parent, eName);
		if (e != null)
		{
			return parseInt(e);
		}
		throw invalid(parent);
	}
	
	
	/**
	 * Gets the text of the child element with the given name
	 * of the given parent element as an Integer, or null if the
	 * element does not exist.
	 */
	public static Integer parseChildIntNull(Element parent, String eName)
	{
		Element e = element(parent, eName);
		if (e != null)
		{
			return parseInt(e);
		}
		return null;
	}
	
	
	public static Float parseAttrFloat(Element e, String attrName)
	{
		return throwNull(parseAttrFloatNull(e, attrName), e);
	}
	
	
	public static Float parseAttrFloatNull(Element e, String attrName)
	{
		try
		{
			String value = attribute(e, attrName);
			return value != null ? Float.parseFloat(value) : null;
		}
		catch (NumberFormatException ex)
		{
			throw invalid(e);
		}
	}
	
	
	public static int parseAttrInt(Element e, String attrName)
	{
		return throwNull(parseAttrIntNull(e, attrName), e);
	}
	
	
	public static Integer parseAttrIntNull(Element e, String attrName)
	{
		try
		{
			String value = attribute(e, attrName);
			return value != null ? parseInt(value) : null;
		}
		catch (NumberFormatException ex)
		{
			throw invalid(e);
		}
	}
	
	
	/**
	 * Gets the text of the given element as a float.
	 */
	public static float parseFloat(Element e)
	{
		try
		{
			return Float.parseFloat(text(e));
		}
		catch (NumberFormatException ex)
		{
			throw invalid(e);
		}
	}
	
	
	/**
	 * Gets the text of the child element with the given name
	 * of the given parent element as a float.
	 */
	public static float parseFloatInt(Element parent, String eName)
	{
		Element e = element(parent, eName);
		if (e != null)
		{
			return parseFloat(e);
		}
		throw invalid(e);
	}
	
	
	/**
	 * Gets the text of the child element with the given name
	 * of the given parent element as a Float, or null if the
	 * element does not exist.
	 */
	public static Float parseChildFloatNull(Element parent, String eName)
	{
		Element e = element(parent, eName);
		if (e != null)
		{
			return parseFloat(e);
		}
		return null;
	}
	
	
	/**
	 * Gets the text of the given element as an int.
	 */
	public static int parseInt(Element e)
	{
		try
		{
			return parseInt(text(e));
		}
		catch (NumberFormatException ex)
		{
			throw invalid(e);
		}
	}
	
	
	/**
	 * Parses an integer from a string. Also values with ".0", ".00" and so
	 * on are allowed.
	 */
	private static int parseInt(String value)
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException ex)
		{
			//also allow .0 values
			float v = Float.parseFloat(value);
			if (v == (int) v)
				return (int) v;
			else
				throw new NumberFormatException("No integer");
		}
	}
	

}
