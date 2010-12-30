package com.xenoage.util.io;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.xenoage.util.FileTools;


/**
 * Input/output implementation for the application.
 * 
 * The application uses two directories for the data file:
 * The user's application settings directory and the directory
 * where the program was installed.
 * 
 * When listing files, both directories are listed. When reading
 * files, first the user's directory is read. When writing files,
 * always the user's directory is written to.
 * 
 * This allows files to be overwritten by individual users, e.g.
 * to replace some symbols with other ones, without destroying
 * the original installation.
 *
 * @author Andreas Wenger
 */
public class ApplicationIO
  implements IOInterface
{
	
	private final File userDir;
	private final File systemDir;
	
	
	/**
	 * Creates a new IO implementation for an application.
	 */
	public ApplicationIO(String programName)
	{
		userDir = FileTools.getUserAppDataDirectory(programName);
		if (!userDir.exists())
		{
			userDir.mkdirs();
		}
		systemDir = new File(System.getProperty("user.dir"));
	}
	
  
  
  /**
   * Returns true, when the given data file exists,
   * otherwise false.
   */
  public boolean existsDataFile(String filepath)
  {
    return new File(userDir, filepath).exists() ||
    	new File(systemDir, filepath).exists();
  }
  
  
  /**
   * Returns true, when the given data directory exists,
   * otherwise false.
   */
  public boolean existsDataDirectory(String directory)
  {
    File userFile = new File(userDir, directory);
    File systemFile = new File(systemDir, directory);
    return (userFile.exists() && userFile.isDirectory()) ||
    	(systemFile.exists() && systemFile.isDirectory());
  }
  
  
  /**
   * Gets the modification date of the given data file,
   * or null, if the date is unavailable.
   */
  public Date getDataFileModificationDate(String filepath)
  {
  	File file = new File(userDir, filepath);
  	if (!file.exists())
  	{
  		file = new File(systemDir, filepath);
  	}
  	if (file.exists())
  	{
  		return new Date(file.lastModified());
  	}
  	else
  	{
  		return null;
  	}
  }
  
  
  /**
   * Opens and returns an input stream for the data file with
   * the given relative path. The file is searched in the user's
   * application settings directory first, and if not found, in
   * the directory where the program was installed.
   */
  public InputStream openInputStream(String filepath)
    throws IOException
  {
  	File file = new File(userDir, filepath);
  	if (!file.exists())
  	{
  		file = new File(systemDir, filepath);
  	}
  	if (file.exists())
  	{
  		return new FileInputStream(file);
  	}
  	else
  	{
  		throw new FileNotFoundException(filepath);
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
  	return new FileInputStream(filepath);
  }
  
  
  /**
   * Opens and returns an output stream for the data file with
   * the given relative path. It is always opened in the user's
   * application settings folder.
   */
  @Override public OutputStream openOutputStream(String filepath)
    throws IOException
  {
  	File file = new File(userDir, filepath);
  	//create the parent directory on demand
  	File parent = file.getParentFile();
  	if (parent != null && !parent.exists())
  	{
  		parent.mkdirs();
  	}
  	//open output stream
    return new FileOutputStream(file);
  }
  
  
  /**
   * Removes the data file with the given relative path.
   * @param system  if true, not only the user's private settings file is deleted
   *                but also the system data file
   */
  public void deleteDataFile(String filepath, boolean system)
  {
  	File file = new File(userDir, filepath);
  	if (file.exists())
  		file.delete();
  	if (system)
  	{
  		file = new File(systemDir, filepath);
    	if (file.exists())
    		file.delete();
  	}
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
  	for (int iDir = 0; iDir < 2; iDir++)
  	{
  		File baseDir = (iDir == 0 ? userDir : systemDir);
  		File dir = new File(baseDir, directory);
  		String[] files = (filter != null ? dir.list(filter) : dir.list());
	    if (files != null)
    	{
    		ret.addAll(Arrays.asList(files));
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
  	for (int iDir = 0; iDir < 2; iDir++)
  	{
  		File baseDir = (iDir == 0 ? userDir : systemDir);
	  	File[] dirs = new File(baseDir, directory).listFiles(FileTools.getDirectoriesFilter());
	  	if (dirs != null)
	  	{
		  	for (int i = 0; i < dirs.length; i++)
		  	{
		  		ret.add(dirs[i].getName());
	  		}
	  	}
  	}
  	return ret;
  }

}
