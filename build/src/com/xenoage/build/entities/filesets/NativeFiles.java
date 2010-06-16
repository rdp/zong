package com.xenoage.build.entities.filesets;


public class NativeFiles
	extends FileSet
{
	
	public static NativeFiles nativeFiles(String... files)
	{
		return new NativeFiles(files);
	}
	
	
	private NativeFiles(String... files)
	{
		super(files);
	}

}
