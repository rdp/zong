package com.xenoage.util.io;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;


/**
 * Input/output implementation for an applet.
 *
 * @author Andreas Wenger
 */
public class AppletIO
  implements IOInterface
{
  
	private URL codeBase;
  private HashSet<String> dataFiles;
  
  
  /**
   * Creates a new input/output implementation
   * for an applet.
   */
  public AppletIO(URL codeBase)
  {
  	this.codeBase = codeBase;
    //read the file .filelist. it contains
    //all filepaths of the jar
    try
    {
      dataFiles = new HashSet<String>();
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(openInputStream(".filelist")));
      String line;
      while ((line = reader.readLine()) != null)
      {
        dataFiles.add(line.replaceAll("\\\\", "/"));
      }
    }
    catch (Exception ex)
    {
      /* AppError.showError(AppError.FATAL,
        ".filelist not found! The data archive seems to be corrupt " +
        "or it is not in the classpath!"); */
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null, ex.toString());
      System.exit(0);
    }
  }
  
  
  /**
   * Creates a new input/output implementation
   * for an applet with the given list of data files.
   */
  AppletIO(URL codeBase, HashSet<String> dataFiles)
  {
  	this.codeBase = codeBase;
    this.dataFiles = dataFiles;
  }
  
  
  /**
   * Returns true, when the given data file exists,
   * otherwise false. Only files listed in the .filelist file
   * are known, so the return value false does not mean that
   * the data file exists anyway.
   */
  public boolean existsDataFile(String filepath)
  {
    for (String dataFile : dataFiles)
    {
      if (dataFile.equals(filepath))
        return true;
    }
    return false;
  }
  
  
  /**
   * Returns true, when the given data directory exists,
   * otherwise false. Only files listed in the .filelist file
   * are known, so the return value false does not mean that
   * the data file exists anyway.
   */
  public boolean existsDataDirectory(String directory)
  {
    if (!directory.endsWith("/"))
      directory += "/";
    for (String dataFile : dataFiles)
    {
      if (dataFile.startsWith(directory))
        return true;
    }
    return false;
  }
  
  
  /**
   * Returns null, since an applet can not read file dates.
   */
  @Override public Date getDataFileModificationDate(String filepath)
  {
  	return null;
  }
  
  
  /**
   * Opens and returns an input stream for the data file with
   * the given relative path. The file is searched in the
   * class path first (that means, e.g. in included JAR files).
   * If not found, it is searched relative to the code base.
   */
  public InputStream openInputStream(String filepath)
    throws IOException
  {
    if (filepath.length() == 0)
      throw new IOException("Empty filepath is not allowed!");
    //try to load from classpath
    String classpathFilePath = filepath;
    if (classpathFilePath.charAt(0) != '/')
    	classpathFilePath = '/' + classpathFilePath;
    URL url = getClass().getResource(classpathFilePath);
    if (url != null)
    {
    	return url.openStream();
    }
    else
    {
    	//load external (not in classpath) file
    	URL externURL = new URL(codeBase, filepath);
    	try
    	{
    		return externURL.openStream();
    	}
    	catch (IOException ex)
    	{
    		throw new IOException("\"" + filepath + "\" not found!", ex);
    	}
    }
  }
  
  
  /**
   * Opens and returns an input stream for the data file with
   * the given absolute or relative path. The path is guaranteed to
   * be untouched (no automatic rerouting to home directory or something
   * like that).
   */
  @Override public InputStream openInputStreamPreservePath(String filepath)
    throws IOException
  {
  	return openInputStream(filepath);
  }
  
  
  /**
   * Throws an exception, since an applet may not write files.
   */
  @Override public OutputStream openOutputStream(String filepath)
    throws IOException
  {
    throw new IOException("Applets can not write files");
  }
  
  
  /**
   * Does nothing, since files can not be deleted in the applet version.
   */
  public void deleteDataFile(String filepath, boolean system)
  {
  }
  
  
  /**
   * Finds and returns the data files in the given directory.
   */
  public Set<String> listDataFiles(String directory)
    throws IOException
  {
    return listDataFiles(directory, null);
  }
  
  
  /**
   * Finds and returns the data files in the given directory
   * matching the given filename filter.
   */
  public Set<String> listDataFiles(String directory, FilenameFilter filter)
    throws IOException
  {
  	Set<String> ret = new HashSet<String>();
    if (!directory.endsWith("/"))
      directory += "/";
    for (String dataFile : dataFiles)
    {
      //is the filepath long enough? (at least 1 character more)
      if (dataFile.length() > directory.length())
      {
        //is the file in the right directory?
        if (dataFile.startsWith(directory))
        {
          //is it in exactly this directory? (no further "/" allowed)
          if (dataFile.substring(directory.length()).indexOf('/') == -1)
          {
            //does the file match the filter?
            File file = new File(dataFile);
            if (filter == null ||
              filter.accept(file.getParentFile(), file.getName()))
            {
              ret.add(file.getName());
            }
          }
        }
      }
    }
    return ret;
  }
  
  
  /**
   * Finds and returns the data directories in the given directory.
   */
  public Set<String> listDataDirectories(String directory)
    throws IOException
  {
  	Set<String> ret = new HashSet<String>();
    if (!directory.endsWith("/"))
      directory += "/";
    for (String dataFile : dataFiles)
    {
      //is the filepath long enough? (at least 1 character more)
      if (dataFile.length() > directory.length())
      {
        //is the dir in the right directory?
        if (dataFile.startsWith(directory))
        {
          //is it in exactly this directory? (must end with "/", which may be the only "/")
        	String fileName = dataFile.substring(directory.length());
          if (fileName.indexOf('/') == fileName.length() - 1 &&
          	fileName.lastIndexOf('/') == fileName.length() - 1)
          {
            File file = new File(dataFile);
            ret.add(file.getName());
          }
        }
      }
    }
    return ret;
  }

}
