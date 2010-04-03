package com.xenoage.util;


/**
 * Useful methods to work with operating system specific tasks.
 * 
 * @author Andreas Wenger
 */
public class OSTools
{
	
	public enum OS
	{
		Linux, MacOSX, Solaris, Windows, Other;
	}
	
	private static OS os = null;
	
	
	/**
	 * Gets the current operating system.
	 */
	public static OS getOS()
	{
		if (os == null)
		{
			String osName = System.getProperty("os.name");
			if (osName.contains("Linux"))
				os = OS.Linux;
			else if (osName.contains("Mac OS X"))
				os = OS.MacOSX;
			else if (osName.contains("Solaris"))
				os = OS.Solaris;
			else if (osName.contains("Windows"))
				os = OS.Windows;
			else
				os = OS.Other;
		}
		return os;
	}
	

}
