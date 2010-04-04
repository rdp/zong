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

	
}
