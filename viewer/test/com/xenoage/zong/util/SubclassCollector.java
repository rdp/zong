package com.xenoage.zong.util;

import java.io.File;
import java.util.ArrayList;




/**
 * This class contains a method
 * that returns all subclasses of
 * a given class.
 * 
 * @author Andreas Wenger
 */
public class SubclassCollector
{
  
  private static final String binDir = "bin";
  
  
  /**
   * Gets all subclasses of the given class.
   */
  @SuppressWarnings("unchecked") public static ArrayList<Class> getSubclasses(Class superClass)
  {
    //get class files
    ArrayList<String> classFiles = new ArrayList<String>();
    getClassFiles(classFiles, "");
    /* TEST
    for (String classFile : classFiles)
    {
      System.out.println(classFile);
    } //*/
    //get classes with given superclass
    ArrayList<Class> ret = new ArrayList<Class>();
    for (String classFile : classFiles)
    {
      String classID = classFile.substring(0,
        classFile.length() - ".class".length());
      if (classID.startsWith("/"))
        classID = classID.substring(1);
      classID = classID.replace('/','.');
      try
      {
        Class cls = Class.forName(classID);
        //TEST
        //System.out.println("Found:     " + cls);
        if (superClass.equals(cls.getSuperclass()))
        {
          ret.add(cls);
        }
      }
      catch (ClassNotFoundException ex)
      {
        //TEST
        //System.out.println("Not found: " + classID);
      }
      catch (ExceptionInInitializerError err)
      {
        //TEST
        //err.printStackTrace();
      }
      catch (NoClassDefFoundError err)
      {
      }
    }
    return ret;
  }
  
  
  /**
   * Gets all subinterfaces of the given interface.
   */
  @SuppressWarnings("unchecked") public static ArrayList<Class> getSubinterfaces(Class superInterface)
  {
    //get class files
    ArrayList<String> classFiles = new ArrayList<String>();
    getClassFiles(classFiles, "");
    //get interfaces with given superinterfaces
    ArrayList<Class> ret = new ArrayList<Class>();
    for (String classFile : classFiles)
    {
      String classID = classFile.substring(0,
        classFile.length() - ".class".length());
      if (classID.startsWith("/"))
        classID = classID.substring(1);
      classID = classID.replace('/','.');
      try
      {
        Class cls = Class.forName(classID);
        if (cls.isInterface() && com.xenoage.util.ArrayTools.contains(
        	cls.getInterfaces(), superInterface))
        {
          ret.add(cls);
        }
      }
      catch (ClassNotFoundException ex)
      {
      }
      catch (ExceptionInInitializerError err)
      {
      }
    }
    return ret;
  }
  
  
  /**
   * Gets all classfiles in the directory
   * given directory (relative to the bin directory),
   * adding them to the given list.
   */
  private static void getClassFiles(
    ArrayList<String> classFiles, String currentDir)
  {
    //list files and directories
    File dir = new File(binDir + "/" + currentDir);
    File[] files = dir.listFiles();
    //add all class files
    for (File file : files)
    {
      if (file.isFile() && file.getName().endsWith(".class"))
      {
        classFiles.add(currentDir + "/" + file.getName());
      }
    }
    //walk through all subdirectories
    //(ignore hidden ones, beginning with ".")
    for (File file : files)
    {
      if (file.isDirectory() && !file.getName().startsWith("."))
      {
        getClassFiles(classFiles, currentDir + "/" + file.getName());
      }
    }
  }

}
