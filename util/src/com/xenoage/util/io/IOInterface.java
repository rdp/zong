package com.xenoage.util.io;

import java.io.*;


/**
 * Interface for a input/output class.
 *
 * @author Andreas Wenger
 */
public interface IOInterface
{

  
  /**
   * Returns true, when the given data file exists,
   * otherwise false.
   */
  public boolean existsDataFile(String filepath);
  
  
  /**
   * Returns true, when the given data directory exists,
   * otherwise false.
   */
  public boolean existsDataDirectory(String directory);
  
  
  /**
   * Opens and returns an input stream for the data file with
   * the given relative path.
   */
  public InputStream openDataFile(String filepath)
    throws IOException;
  
  
  /**
   * Finds and returns the data files in the given directory.
   */
  public String[] listDataFiles(String directory)
    throws IOException;
  
  
  /**
   * Finds and returns the data files in the given directory
   * matching the given filename filter.
   */
  public String[] listDataFiles(String directory, FilenameFilter filter)
    throws IOException;
  
  
  /**
   * Finds and returns the data directories in the given directory.
   */
  public String[] listDataDirectories(String directory)
    throws IOException;
  
}
