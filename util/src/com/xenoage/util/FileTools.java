package com.xenoage.util;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import com.xenoage.util.OSTools.OS;
import com.xenoage.util.io.IO;
import com.xenoage.util.lang.Tuple2;


/**
 * Some useful file system functions.
 *
 * @author Andreas Wenger
 */
public class FileTools
{
  
  private static FileFilter directoriesFilter = null;

  
  /**
   * Returns a filter for directories.
   * Directories beginning with a "." are ignored
   * (e.g. ".svn").
   */
  public static FileFilter getDirectoriesFilter()
  {
    if (directoriesFilter == null)
    {
      directoriesFilter = new FileFilter()
      {
        public boolean accept(File file)
        {
          return file.isDirectory() && !file.getName().startsWith(".");
        }
      };
    }
    return directoriesFilter;
  }
  
  
  /**
   * Returns a filename filter for files with ".xml" ending.
   */
  public static FilenameFilter getXMLFilter()
  {
    FilenameFilter ret = new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
          return name.toLowerCase().endsWith(".xml");
      }
    };
    return ret;
  }
  
  
  /**
   * Returns a filename filter for files with ".voc" ending.
   */
  public static FilenameFilter getVocFilter()
  {
    FilenameFilter ret = new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
          return name.toLowerCase().endsWith(".voc");
      }
    };
    return ret;
  }
  
  
  /**
   * Returns a filename filter for files with ".svg" ending.
   */
  public static FilenameFilter getSVGFilter()
  {
    FilenameFilter ret = new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
          return name.toLowerCase().endsWith(".svg");
      }
    };
    return ret;
  }
  
  
  /**
   * Returns a filename filter for files with ".ttf" ending.
   */
  public static FilenameFilter getTTFFilter()
  {
    FilenameFilter ret = new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
          return name.toLowerCase().endsWith(".ttf");
      }
    };
    return ret;
  }
  
  
  /**
   * Reads the given file into a string and returns it.
   * If the file does not exist or there is another error,
   * null is returned.
   */
  public static String readFile(String filepath)
  {
    try
    {
      StringBuffer fileData = new StringBuffer(1024);
      BufferedReader reader = new BufferedReader(new InputStreamReader(
      	IO.openInputStream(filepath)));
      char[] buf = new char[1024];
      int numRead = 0;
      while((numRead=reader.read(buf)) != -1){
          String readData = String.valueOf(buf, 0, numRead);
          fileData.append(readData);
          buf = new char[1024];
      }
      reader.close();
      return fileData.toString();
    }
    catch (Exception ex)
    {
      return null;
    }
  }
  
  
  /**
   * Gets the name of the file without any extensions
   * (ends before the first dot, but a dot on the very
   * first position is allowed)
   */
  public static String getNameWithoutExt(File file)
  {
    return getNameWithoutExt(file.getName());
  }
  
  
  /**
   * Gets the name of the file without any extensions
   * (ends before the first dot, but a dot on the very
   * first position is allowed)
   */
  public static String getNameWithoutExt(String filename)
  {
    int dotPos = filename.indexOf(".", 1);
    if (dotPos == -1)
      return filename;
    else
      return filename.substring(0, dotPos);
  }
  
  
  /**
   * Gets the directory and the filename of the given path.
   * For example, when "1/2/3.pdf" is given, "1/2" and "3.pdf"
   * is returned. For "4.xml", "" and "4.xml" is returned.
   */
  public static Tuple2<String, String> splitDirectoryAndFilename(String path)
  {
  	String p = path.replaceAll("\\\\", "/"); //use only / for the moment
  	int endPos = p.lastIndexOf('/');
  	if (endPos > -1)
  	{
  		String dir = p.substring(0, endPos);
  		dir = dir.replaceAll("/", Matcher.quoteReplacement(File.separator));
  		String file = p.substring(endPos + 1);
  		return new Tuple2<String, String>(dir, file);
  	}
  	else
  	{
  		//only filename
  		return new Tuple2<String, String>("", p);
  	}
  }
  
  
  /**
   * Gets the directory name of the given path.
   * For example, when "1/2/3.pdf" is given, "1/2"
   * is returned. For "4.xml", "" is returned.
   */
  public static String getDirectoryName(String path)
  {
  	return splitDirectoryAndFilename(path).get1();
  }
  
  
  /**
   * Gets the filename of the given path.
   * For example, when "1/2/3.pdf" is given, "3.pdf"
   * is returned. For "4.xml", "4.xml" is returned.
   */
  public static String getFileName(String path)
  {
  	return splitDirectoryAndFilename(path).get2();
  }
  
  
  /**
   * Gets the directory for temporary files.
   */
  public static File getTempFolder()
  {
  	return new File(System.getProperty("java.io.tmpdir"));
  }
  
  
  /**
   * Deletes the given directory, which may be non-empty.
   */
  public static boolean deleteDirectory(File path)
  {
		if (path.exists())
		{
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else
				{
					files[i].delete();
				}
			}
		}
		return path.delete();
	}
  
  
  /**
   * Lists all files in a given directory.
   * @param dir        the directory
   * @param subdirs    true, if recursive search in subdirectories, otherwise false 
   */
  public static List<File> listFiles(File dir, boolean subdirs)
  {
  	LinkedList<File> list = new LinkedList<File>();
    listFiles(list, dir, subdirs);
    return list;
  }


  private static void listFiles(LinkedList<File> list, File dir, boolean subdirs)
  {
    File[] children = dir.listFiles();
    if (children != null)
    {
      for (int i = 0; i < children.length; i++)
      {
        if (children[i].isDirectory() && subdirs)
        	listFiles(list, children[i], subdirs);
        else
          list.add(children[i]);
      }
    }
  }
  
  
  /**
   * Returns a list of all files below the given directory (including
   * subdirectories in any depth) ending with the given extension
   * (also provide the dot in the parameter).
   */
  public static LinkedList<File> listFilesDeep(File directory, String suffix)
  {
  	LinkedList<File> ret = new LinkedList<File>();
  	String[] files = directory.list();
  	for (String fileName : files)
  	{
  		File file = new File(directory, fileName);
  		if (file.isDirectory())
  		{
  			ret.addAll(listFilesDeep(file, suffix));
  		}
  		else if (fileName.endsWith(suffix))
  		{
  			ret.add(file);
  		}
  	}
  	return ret;
  }
  
  
  /**
   * Gets the default directory of the operating system to store application
   * data for the current user and the given application name.
   * For user "andi" and program "myapp" this is for example:
   * <ul>
   * 	<il>under Linux and Solaris: "/home/andi/.myapp/"</il>
   *  <il>under MacOSX: "/Users/andi/Library/Application Support/myapp"</il>
   * 	<il>under Windows: "C:/Users/andi/AppData/Roaming" (under Vista at least)</il>
   * </ul>
   * Of course these settings may differ between different versions of
   * the operating system. This function determines the right place to store
   * the settings. If impossible to find the perfectly right folder, the user's
   * home folder extended by "/myapp" is returned.
   */
  public static File getUserAppDataDirectory(String program)
  {
  	OS os = OSTools.getOS();
  	if (os == OS.Linux || os == OS.Solaris)
  	{
  		//Linux, Solaris: <user-home>/.<program>
  		return new File(System.getProperty("user.home") + "/." + program);
  	}
  	else if (os == OS.MacOSX)
  	{
  		//Mac OS X: <user-home>/Library/Application Support/<program>
  		String path = System.getProperty("user.home") +
  			"/Library/Application Support/";
  		if (path != null && new File(path).exists())
  		{
  			return new File(path + "/" + program);
  		}
  	}
  	else if (os == OS.Windows)
  	{
  		//Windows: use APPDATA environment variable.
  		String path = System.getenv("APPDATA");
  		if (path != null && new File(path).exists())
  		{
  			return new File(path + "/" + program);
  		}
  	}
  	//otherwise: <user-home>/<program>
  	return new File(System.getProperty("user.home") + "/" + program);
  }

  
}
