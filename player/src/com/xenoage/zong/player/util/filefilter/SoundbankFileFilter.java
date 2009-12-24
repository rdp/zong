package com.xenoage.zong.player.util.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * Filter for soundbank files.
 * 
 * @author Andreas Wenger
 */
public class SoundbankFileFilter
	extends FileFilter
{
	
	private static final String[] extensions = {"sf2", "dls", "pat", "cfg", "wav", "au", "aif"};
	

	@Override public boolean accept(File file)
	{
		String name = file.getName().toLowerCase();
		boolean b = file.isDirectory();
		for (String extension : extensions)
		{
			b |= name.endsWith("." + extension);
		}
		return b;
	}


	@Override public String getDescription()
	{
		StringBuilder s = new StringBuilder();
		s.append("Soundbank (");
		for (int i = 0; i < extensions.length; i++)
		{
			s.append("*." + extensions[i]);
			if (i < extensions.length - 1)
				s.append(",");
		}
		s.append(")");
		return s.toString();
	}
	
}
