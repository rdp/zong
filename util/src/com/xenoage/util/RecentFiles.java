package com.xenoage.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Manages a list of the recently opened files.
 * It is loaded from and saved from a text file called
 * "data/recentfiles". Only existing files
 * are listed, other ones are automatically removed.
 * 
 * @author Andreas Wenger
 */
public class RecentFiles
{
	
	static final String DATA_FILE = "data/recentfiles";
	static final int MAX_ENTRIES_COUNT = 5;
	
	
	/**
	 * Gets the list of recent files. If an error occurs, an empty
	 * list is returned (no error reporting is done).
	 */
	public static ArrayList<File> getRecentFiles()
	{
		ArrayList<File> ret = new ArrayList<File>(MAX_ENTRIES_COUNT);
		String list = FileTools.readFile(DATA_FILE);
		if (list != null)
		{
			String[] files = list.split("\n");
			for (int i = 0; i < MAX_ENTRIES_COUNT && i < files.length; i++)
			{
				File file = new File(files[i]);
				if (file.exists())
					ret.add(file);
			}
		}
		return ret;
	}
	
	
	/**
	 * Adds a new file to the beginning of the list.
	 * It is automatically stored on disk.
	 * Errors are not reported.
	 */
	public static void addRecentFile(File file)
	{
		if (!file.exists())
			return;
		file = file.getAbsoluteFile();
		//get list and add given file
		ArrayList<File> files = getRecentFiles();
		files.remove(file);
		files.add(0, file);
		//trim list
		while (files.size() > MAX_ENTRIES_COUNT)
			files.remove(files.size() - 1);
		//save list
		try
		{
			FileWriter writer = new FileWriter(DATA_FILE);
			for (File f : files)
			{
				writer.append(f.getAbsolutePath() + "\n");
			}
			writer.close();
		}
		catch (IOException ex)
		{
		}
	}
	

}
