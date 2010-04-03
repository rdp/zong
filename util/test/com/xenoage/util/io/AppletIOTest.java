package com.xenoage.util.io;


import static org.junit.Assert.*;

import com.xenoage.util.FileTools;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


/**
 * Test cases for the AppletIO class.
 *
 * @author Andreas Wenger
 */
public class AppletIOTest
{
  
  AppletIO io;
  HashSet<String> dataFiles;
  
  
  @Before public void setUp()
  	throws IOException
  {
    dataFiles = new HashSet<String>();
    dataFiles.add("hallo.txt");
    dataFiles.add("dir1/hallo.voc");
    dataFiles.add("dir1/ordner/");
    dataFiles.add("dir1/buffer.png");
    dataFiles.add("dir 1/noch ne datei");
    dataFiles.add("dir1/dir2/hallo.txt");
    io = new AppletIO(new URL("http://localhost"), dataFiles);
  }
  
  
  @Test public void existsDataFile()
  {
    for (String dataFile : dataFiles)
    {
      assertTrue(io.existsDataFile(dataFile));
    }
    assertFalse(io.existsDataFile("dir1/hallo.Txt"));
    assertFalse(io.existsDataFile(""));
    assertFalse(io.existsDataFile("dir 1/noch ne datei.png"));
    assertFalse(io.existsDataFile("dir1/dir2/oje"));
  }
  
  
  @Test public void listDataFiles()
  {
    try
    {
      //empty folder
      Set<String> files = io.listDataFiles("dir1/ordner/");
      assertEquals(0, files.size());
      //folder with 2 files
      files = io.listDataFiles("dir1");
      assertEquals(2, files.size());
      assertTrue(files.contains("hallo.voc"));
      assertTrue(files.contains("buffer.png"));
      //folder with 2 files, filtered
      files = io.listDataFiles("dir1/", FileTools.getVocFilter());
      assertEquals(1, files.size());
      assertTrue(files.contains("hallo.voc"));
    }
    catch (Exception ex)
    {
      fail();
    }
  }

}
