package com.xenoage.util.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Useful methods for working with ZIP files.
 * 
 * @author Andreas Wenger
 */
public class ZipTools
{

	
	/**
	 * Extracts all files within the given ZIP stream
	 * into the given directory.
	 */
	public static void extractAll(InputStream zipStream, File destDir)
		throws IOException
	{
		byte[] buf = new byte[1024];
		ZipInputStream zipinputstream = new ZipInputStream(zipStream);
		ZipEntry zipentry;
		
		//extract all files
		while ((zipentry = zipinputstream.getNextEntry()) != null)
		{
			String entryName = zipentry.getName();
			File newFile = new File(destDir, entryName);
			if (zipentry.isDirectory())
			{
				newFile.mkdirs();
			}
			else
			{
				FileOutputStream fileoutputstream;
				fileoutputstream = new FileOutputStream(newFile);
				int n;
				while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
				{
					fileoutputstream.write(buf, 0, n);
				}
				fileoutputstream.close();
				zipinputstream.closeEntry();
			}
		}

		zipinputstream.close();
	}

}
