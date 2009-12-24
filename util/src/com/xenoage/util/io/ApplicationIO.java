package com.xenoage.util.io;

import java.io.*;

import com.xenoage.util.FileTools;


/**
 * Input/output implementation for the
 * application.
 *
 * @author Andreas Wenger
 */
public class ApplicationIO
  implements IOInterface
{
  
  
  /**
   * Returns true, when the given data file exists,
   * otherwise false.
   */
  public boolean existsDataFile(String filepath)
  {
    return new File(filepath).exists();
  }
  
  
  /**
   * Returns true, when the given data directory exists,
   * otherwise false.
   */
  public boolean existsDataDirectory(String directory)
  {
    File file = new File(directory);
    return file.exists() && file.isDirectory();
  }
  
  
  /**
   * Opens and returns an input stream for the data file with
   * the given relative path.
   */
  public InputStream openDataFile(String filepath)
    throws IOException
  {
    return new FileInputStream(new File(filepath));
  }
  
  
  /**
   * Finds and returns the data files in the given directory.
   */
  public String[] listDataFiles(String directory)
    throws IOException
  {
    return listDataFiles(directory, null);
  }
  
  
  /**
   * Finds and returns the data files in the given directory
   * matching the given filename filter.
   */
  public String[] listDataFiles(String directory, FilenameFilter filter)
    throws IOException
  {
    if (filter != null)
      return new File(directory).list(filter);
    else
      return new File(directory).list();
  }
  
  
  /**
   * Finds and returns the data directories in the given directory.
   */
  public String[] listDataDirectories(String directory)
    throws IOException
  {
  	File[] dirs = new File(directory).listFiles(FileTools.getDirectoriesFilter());
  	String[] ret = new String[dirs.length];
  	for (int i = 0; i < ret.length; i++)
  		ret[i] = dirs[i].getName();
  	return ret;
  }

}
