package com.xenoage.util.font;

import java.util.HashMap;

import com.xenoage.util.settings.Settings;


/**
 * This class helps to find a replacement for a font which
 * is not installed on the system.
 * 
 * The font name is looked up in the file
 * "data/config/font-replacements.settings".
 * 
 * @author Andreas Wenger
 */
public class FontReplacements
{
	
	private static FontReplacements instance = null;
	
	private HashMap<String, String> replacements;
	
	
	public static FontReplacements getInstance()
	{
		if (instance == null)
			instance = new FontReplacements();
		return instance;
	}
	
	
	private FontReplacements()
	{
		replacements = Settings.getInstance().getAllSettings("font-replacements");
	}
	
	
	/**
	 * Gets the replacement for the given font family.
	 * If unknown, the given font family is returned.
	 */
	public String getReplacement(String fontFamily)
	{
		String ret = replacements.get(fontFamily);
		if (ret == null)
			ret = fontFamily;
		return ret;
	}
	

}
