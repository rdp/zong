package com.xenoage.build.entities.filesets;

import java.util.ArrayList;


public class FileSet
	extends ArrayList<String>
{

	public FileSet(String... files)
	{
		for (int i = 0; i < files.length; i++)
			this.add(files[i]);
	}
	
}
