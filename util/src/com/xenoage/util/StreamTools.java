package com.xenoage.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Useful methods to work with streams.
 * 
 * @author Andreas Wenger
 */
public class StreamTools
{
	
	
	/**
	 * Mark an {@link BufferedInputStream}),
	 * with enough memory so that the mark never gets invalid if less
	 * than 32k are read.
	 */
	public static void markInputStream(BufferedInputStream is)
	{
		is.mark(8192 * 4); //8192 is the default buffer size in BufferedInputStream
	}


	/**
	 * Reads the given input stream into a byte array.
	 */
	public static byte[] readInputStream(InputStream in) throws IOException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);
		byte[] bytes = new byte[4096];
		int readBytes;
		while ((readBytes = in.read(bytes)) > 0)
		{
			outputStream.write(bytes, 0, readBytes);
		}
		return outputStream.toByteArray();
	}


}
