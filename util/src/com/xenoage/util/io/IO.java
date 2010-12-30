package com.xenoage.util.io;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.Set;


/**
 * This class provides genereal input and
 * output methods.
 * 
 * It can be initialized for an desktop application
 * or for an applet.
 *
 * @author Andreas Wenger
 */
public class IO
{
  
  //the implementation of the file reader: application or applet
  private static IOInterface implementation = null;
  
  
  /**
   * Initializes the file reader for the application.
   */
  public static void initApplication(String programName)
  {
    implementation = new ApplicationIO(programName);
  }
  
  
  /**
   * Initializes the file reader for the applet.
   */
  public static void initApplet(URL codeBase)
  {
    implementation = new AppletIO(codeBase);
  }
  
  
  /**
   * Initializes the file reader for testing.
   * The application mode is used, and the program name is
   * composed of "xenoage" and the name of the calling class.
   */
  public static void initTest()
  {
    implementation = new ApplicationIO("xenoage/" +
    	Thread.currentThread().getStackTrace()[2].getClassName());
  }
  
  
  /**
   * Returns true, when the given data file exists,
   * otherwise false.
   */
  public static boolean existsDataFile(String filepath)
  {
  	return implementation.existsDataFile(filepath);
  }
  
  
  /**
   * Returns true, when the given data directory exists,
   * otherwise false.
   */
  public static boolean existsDataDirectory(String directory)
  {
    return implementation.existsDataDirectory(directory);
  }
  
  
  /**
   * Gets the modification date of the given data file,
   * or null, if the date is unavailable.
   */
  public static Date getDataFileModificationDate(String filepath)
  {
  	return implementation.getDataFileModificationDate(filepath);
  }
  
  
  /**
   * Opens and returns an input stream for the data file with
   * the given relative path.
   */
  public static InputStream openInputStream(String filepath)
    throws IOException
  {
    return implementation.openInputStream(filepath);
  }
  
  
  /**
   * Opens and returns an input stream for the data file with
   * the given absolute or relative path. The path is guaranteed to
   * be untouched (no automatic rerouting to home directory or something
   * like that).
   */
  public static InputStream openInputStreamPreservePath(String filepath)
    throws IOException
  {
    return implementation.openInputStreamPreservePath(filepath);
  }
  
  
  /**
   * Opens and returns an output stream for the data file with
   * the given relative path.
   */
  public static OutputStream openOutputStream(String filepath)
    throws IOException
  {
    return implementation.openOutputStream(filepath);
  }
  
  
  /**
   * Removes the data file with the given relative path.
   * @param system  if true, not only the user's private settings file is deleted
   *                but also the system data file
   */
  public static void deleteDataFile(String filepath, boolean system)
  {
    implementation.deleteDataFile(filepath, system);
  }
  
  
  /**
   * Finds and returns the data files in the given directory.
   */
  public static Set<String> listDataFiles(String directory)
    throws IOException
  {
    return implementation.listDataFiles(directory);
  }
  
  
  /**
   * Finds and returns the data directories in the given directory.
   */
  public static Set<String> listDataDirectories(String directory)
    throws IOException
  {
    return implementation.listDataDirectories(directory);
  }
  
  
  /**
   * Finds and returns the data files in the given directory
   * matching the given filename filter.
   */
  public static Set<String> listDataFiles(String directory, FilenameFilter filter)
    throws IOException
  {
    return implementation.listDataFiles(directory, filter);
  }

}
