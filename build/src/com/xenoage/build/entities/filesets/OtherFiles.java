package com.xenoage.build.entities.filesets;


public class OtherFiles
	extends FileSet
{
	
	public static OtherFiles otherFiles(String... files)
	{
		return new OtherFiles(files);
	}
	
	
	private OtherFiles(String... files)
	{
		super(files);
	}

}
