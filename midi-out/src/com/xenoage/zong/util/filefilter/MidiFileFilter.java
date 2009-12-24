package com.xenoage.zong.util.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * Filter for Midi files (.mid).
 * 
 * @author Andreas Wenger
 */
public class MidiFileFilter
	extends FileFilter
{

	@Override public boolean accept(File file)
	{
		String name = file.getName().toLowerCase();
		return file.isDirectory() || name.endsWith(".mid");
	}


	@Override public String getDescription()
	{
		return "MIDI (.mid)";
	}
	
}
