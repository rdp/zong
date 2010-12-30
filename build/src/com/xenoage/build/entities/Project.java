package com.xenoage.build.entities;

import static com.xenoage.build.entities.Dependencies.depends;

import com.xenoage.build.entities.filesets.OtherFiles;


/**
 * Java project.
 * 
 * @author Andreas Wenger
 */
public class Project
	extends CodeItem
{
	
	private boolean dist = false;
	private OtherFiles otherFiles;
	private Dependencies allDependencies = depends();


	public static Project project(String name)
	{
		return new Project(name, depends(), OtherFiles.otherFiles());
	}
	
	
	public static Project project(String name, Dependencies dependencies)
	{
		return new Project(name, dependencies, OtherFiles.otherFiles());
	}
	
	
	public static Project distProject(String name, Dependencies dependencies, OtherFiles otherFiles)
	{
		Project ret = new Project(name, dependencies, otherFiles);
		ret.dist = true;
		return ret;
	}
	
	
	private Project(String name, Dependencies dependencies, OtherFiles otherFiles)
	{
		super(name, dependencies);
		this.otherFiles = otherFiles;
		this.allDependencies.addAll(dependencies);
	}
	
	
	public OtherFiles getOtherFiles()
	{
		return otherFiles;
	}
	
	
	public boolean isDist()
	{
		return dist;
	}
	
	
	/**
	 * Returns all (direct and indirect) dependencies of this project.
	 */
	public Dependencies getAllDependencies()
	{
		return allDependencies;
	}


}
