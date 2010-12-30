package com.xenoage.build.entities.filesets;


public class Includes
	extends FileSet
{
	
	public static Includes includes(String... files)
	{
		return new Includes(files);
	}
	
	
	private Includes(String... files)
	{
		super(files);
	}

}
