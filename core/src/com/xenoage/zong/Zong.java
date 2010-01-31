package com.xenoage.zong;


/**
 * Product and version information.
 * 
 * @author Andreas Wenger
 */
public class Zong
{

	//the version of this program as an integer number
	public static final int PROJECT_ITERATION = 49;
	
	//general information on this program
  public static final String PROJECT_FAMILY_NAME = "Zong!";
  public static final String PROJECT_VERSION = "p0.4";
  public static final String PROJECT_ITERATION_NAME = "Rousseau";
  
  //package path
  public static final String PACKAGE = "com.xenoage.zong";
  
  //copyright information
  public static final String COPYRIGHT =
  	"Copyright © 2006-2010 by Andreas Wenger, Uli Teschemacher, Xenoage Software";
  
  //other information
  public static final String EMAIL_ERROR_REPORTS = "support@zong-music.com";
  
  
  /**
   * Gets the name of the program as a String,
   * using the given "first" name of the project (like "Viewer" or "Editor").
   */
  public static String getName(String firstName)
  {
  	return Zong.PROJECT_FAMILY_NAME + " " + firstName;
  }
  
  
  /**
   * Gets the name and version of the program as a String,
   * using the given "first" name of the project (like "Viewer" or "Editor").
   */
  public static String getNameAndVersion(String firstName)
  {
  	return Zong.PROJECT_FAMILY_NAME + " " + firstName + " " + Zong.PROJECT_VERSION + "." +
  		Zong.PROJECT_ITERATION + " ALPHA";
  }

}
