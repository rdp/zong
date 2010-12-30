package com.xenoage.build.entities;



/**
 * Interface for projects and libraries.
 * 
 * @author Andreas Wenger
 */
public abstract class CodeItem
{
	
	String name;
	Dependencies dependencies;
	
	
	public CodeItem(String name, Dependencies dependencies)
	{
		this.name = name;
		this.dependencies = dependencies;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * Gets the direct dependencies of this item.
	 */
	public Dependencies getDependendies()
	{
		return dependencies;
	}
	
	
	@Override public String toString()
	{
		return name;
	}

}
