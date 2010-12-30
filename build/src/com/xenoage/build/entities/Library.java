package com.xenoage.build.entities;

import static com.xenoage.build.entities.Dependencies.depends;

import com.xenoage.build.entities.filesets.JarFiles;
import com.xenoage.build.entities.filesets.NativeFiles;
import com.xenoage.build.entities.filesets.OtherFiles;


/**
 * Java library.
 * 
 * @author Andreas Wenger
 */
public class Library
	extends CodeItem
{
	
	private JarFiles jarFiles;
	private OtherFiles otherFiles;
	private NativeFiles nativeFiles;
	
	
	public static Library library(String name, JarFiles jarFiles)
	{
		return new Library(name, jarFiles, OtherFiles.otherFiles(), NativeFiles.nativeFiles());
	}
	
	
	public static Library library(String name, JarFiles jarFiles, OtherFiles otherFiles)
	{
		return new Library(name, jarFiles, otherFiles, NativeFiles.nativeFiles());
	}
	
	
	public static Library library(String name, JarFiles jarFiles, OtherFiles otherFiles,
		NativeFiles nativeFiles)
	{
		return new Library(name, jarFiles, otherFiles, nativeFiles);
	}
	
	
	private Library(String name, JarFiles jarFiles, OtherFiles otherFiles, NativeFiles nativeFiles)
	{
		super(name, depends());
		this.jarFiles = jarFiles;
		this.otherFiles = otherFiles;
		this.nativeFiles = nativeFiles;
	}
	
	
	public JarFiles getJarFiles()
	{
		return jarFiles;
	}

	
	public OtherFiles getOtherFiles()
	{
		return otherFiles;
	}
	
	
	public NativeFiles getNativeFiles()
	{
		return nativeFiles;
	}
	
}
