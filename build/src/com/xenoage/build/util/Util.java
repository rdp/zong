package com.xenoage.build.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class Util
{
	
	
	/**
	 * Copies a file.
	 */
	public static void copyFile(String in, String out)
	{
		try
		{
			FileInputStream fis = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1)
			{
				fos.write(buf, 0, i);
			}
			fis.close();
			fos.close();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
  }

	
	
	/**
   * Deletes the given directory, which may be non-empty.
   */
  public static boolean deleteDirectory(File path)
  {
		if (path.exists())
		{
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else
				{
					files[i].delete();
				}
			}
		}
		return path.delete();
	}
  
  
  /**
   * Gets the directory for temporary files.
   */
  public static File getTempFolder()
  {
  	return new File(System.getProperty("java.io.tmpdir"));
  }

}
