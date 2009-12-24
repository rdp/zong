package com.xenoage.util.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * Useful methods to work with exceptions and errors.
 * 
 * @author Andreas Wenger
 */
public class ThrowableTools
{

	public static String getStackTrace(Throwable error)
	{
    final Writer result = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(result);
    error.printStackTrace(printWriter);
    return result.toString();
  }
	
	
	public static void throwNullArg(Object... o)
	{
		for (int i = 0; i < o.length; i++)
		{
			if (o[i] == null)
			{
				throw new IllegalArgumentException("Argument may not be null (checked argument with index " + i +")");
			}
		}
	}

	
}
