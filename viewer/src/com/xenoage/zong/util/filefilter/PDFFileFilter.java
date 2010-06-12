package com.xenoage.zong.util.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * Filter for PDF files (.pdf).
 * 
 * @author Andreas Wenger
 */
public final class PDFFileFilter
	extends FileFilter
{

	@Override public boolean accept(File file)
	{
		String name = file.getName().toLowerCase();
		return file.isDirectory() || name.endsWith("pdf");
	}


	@Override public String getDescription()
	{
		return "PDF (.pdf)";
	}
	
}
