package com.xenoage.zong.util.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * Filter for MusicXML files (.mxl, .xml).
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class MusicXMLFileFilter
	extends FileFilter
{

	@Override public boolean accept(File file)
	{
		String name = file.getName().toLowerCase();
		return file.isDirectory() || name.endsWith(".mxl") || name.endsWith(".xml");
	}


	@Override public String getDescription()
	{
		return "MusicXML (.mxl, .xml)";
	}
	
}
