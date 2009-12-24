package com.xenoage.util.io;

import java.io.*;
import java.net.URL;


/**
 * This class provides genereal input and
 * output methods.
 * 
 * It can be initialized for an desktop application
 * or for an applet.
 * 
 * TODO: very, very bad. Replace this whole stuff as soon
 * as possible (if possible, use Java's new I/O also for
 * applets)
 *
 * @author Andreas Wenger
 */
public class IO
{
  
  //the implementation of the file reader: application or applet
  private static IOInterface implementation = new ApplicationIO();
  
  
  /**
   * Initialized the file reader for the application.
   */
  public static void initApplication()
  {
    implementation = new ApplicationIO();
  }
  
  
  /**
   * Initialized the file reader for the applet.
   */
  public static void initApplet(URL codeBase)
  {
    implementation = new AppletIO(codeBase);
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
   * Opens and returns an input stream for the data file with
   * the given relative path.
   */
  public static InputStream openDataFile(String filepath)
    throws IOException
  {
    return implementation.openDataFile(filepath);
  }
  
  
  /**
   * Finds and returns the data files in the given directory.
   */
  public static String[] listDataFiles(String directory)
    throws IOException
  {
    return implementation.listDataFiles(directory);
  }
  
  
  /**
   * Finds and returns the data directories in the given directory.
   */
  public static String[] listDataDirectories(String directory)
    throws IOException
  {
    return implementation.listDataDirectories(directory);
  }
  
  
  /**
   * Finds and returns the data files in the given directory
   * matching the given filename filter.
   */
  public static String[] listDataFiles(String directory, FilenameFilter filter)
    throws IOException
  {
    return implementation.listDataFiles(directory, filter);
  }

}
