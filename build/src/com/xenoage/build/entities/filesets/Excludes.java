package com.xenoage.build.entities.filesets;


public class Excludes
	extends FileSet
{
	
	public static Excludes excludes(String... files)
	{
		return new Excludes(files);
	}
	
	
	private Excludes(String... files)
	{
		super(files);
	}

}
