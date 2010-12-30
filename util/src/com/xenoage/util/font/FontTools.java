package com.xenoage.util.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.xenoage.util.FileTools;
import com.xenoage.util.SortedList;
import com.xenoage.util.io.IO;
import com.xenoage.util.logging.Log;


/**
 * Useful methods to work with fonts.
 * 
 * @author Andreas Wenger
 */
public class FontTools
{
	
	private static FontTools instance = null;
	
	private SortedList<String> supportedFontFamiliesSorted;
	private HashSet<String> supportedFontFamilies;
	
	
	public static FontTools getInstance()
	{
		if (instance == null)
			instance = new FontTools();
		return instance;
	}
	
	
	private FontTools()
	{
		//sorted array for the GUI
		String[] systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    Arrays.sort(systemFonts);
    //add to sorted list
    supportedFontFamiliesSorted = new SortedList<String>(systemFonts, false);
    //hash set for fast queries
    supportedFontFamilies = new HashSet<String>(supportedFontFamiliesSorted.getSize() * 2);
    for (String s : systemFonts)
    {
    	supportedFontFamilies.add(s);
    }
	}
	
	
	/**
	 * Loads the shipped fonts in "data/fonts".
	 * If an exception occurs during loading, it is thrown.
	 */
	public void loadShippedFonts()
		throws Exception
	{
  	String fontPath = "data/fonts";
  	Set<String> ttfFiles = IO.listDataFiles(fontPath, FileTools.getTTFFilter());
  	for (String file : ttfFiles)
  	{
  		Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "/" + file));
  		String fontName = font.getFamily();
  		if (!isFontFamilySupported(fontName))
  		{
  			Log.log(Log.MESSAGE, "Registering font: " + fontName);
  			supportedFontFamiliesSorted.add(fontName);
  			supportedFontFamilies.add(fontName);
  			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
  		}
  	}
	}
	
	
	/**
	 * Gets an alphabetically sorted list of all supported font families.
	 */
	public SortedList<String> getSupportedFontFamilies()
	{
		return supportedFontFamiliesSorted;
	}
	
	
	/**
	 * Returns, if the given font family is supported by this system.
	 */
	public boolean isFontFamilySupported(String fontFamily)
	{
		return supportedFontFamilies.contains(fontFamily);
	}
	

}
