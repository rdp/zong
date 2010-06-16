package com.xenoage.build;

import java.io.PrintStream;

import com.xenoage.build.entities.Project;


/**
 * Interface for build configurations.
 * 
 * @author Andreas Wenger
 */
public abstract class BuildConfig
	extends org.apache.tools.ant.Task
{
	
	public enum BuildType
	{
		Build, //just build
		Jar, //build and jar
		Dist, //built, jar and dist
	}
	BuildType buildType = BuildType.Dist;
	
	String projectname = null;
	
	
	/**
	 * Ant: buildtype attribute
	 */
	public void setBuildtype(String value)
	{
		if (value.equals("build"))
			buildType = BuildType.Build;
		else if (value.equals("jar"))
			buildType = BuildType.Jar;
		else if (value.equals("dist"))
			buildType = BuildType.Dist;
		else
			throw new IllegalArgumentException("Unknown buildtype: " + value);
	}
	
	
	/**
	 * Ant: projectname attribute
	 */
	public void setProjectname(String value)
	{
		projectname = value;
	}
	
	
	public String getName()
	{
		return "unnamed";
	}
	
	
	public String getPrefix()
	{
		return "";
	}
	
	
	public String getVersion()
	{
		return "0.0";
	}
	
	public void distCustomStep(Project pro)
	{
	}
	
	public void finalStep(PrintStream out)
	{
	}

}
