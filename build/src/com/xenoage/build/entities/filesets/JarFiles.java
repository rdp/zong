package com.xenoage.build.entities.filesets;


public class JarFiles
	extends FileSet
{
	
	public static JarFiles jarFiles(String... files)
	{
		return new JarFiles(files);
	}
	
	
	private JarFiles(String... files)
	{
		super(files);
	}

}
