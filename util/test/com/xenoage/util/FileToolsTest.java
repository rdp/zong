package com.xenoage.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.xenoage.util.FileTools;
import com.xenoage.util.lang.Tuple2;


/**
 * Test cases for the {@link FileTools} class.
 *
 * @author Andreas Wenger
 */
public class FileToolsTest
{

  @Test public void getNameWithoutExt()
  {
    //gets the filename without extension of this file
    assertEquals("FileToolsTest",
      FileTools.getNameWithoutExt(new File(
        "test/com.xenoage.zong/util/FileToolsTest.java")));
  }
  
  
  @Test public void splitDirectoryAndFilename()
  {
  	//test "1/2/3.pdf"
  	String s = "1" + File.separator + "2" + File.separator + "3.pdf";
  	Tuple2<String, String> res = FileTools.splitDirectoryAndFilename(s);
  	assertEquals("1" + File.separator + "2", res.get1());
  	assertEquals("3.pdf", res.get2());
  	//test "4.xml"
  	s = "4.xml";
  	res = FileTools.splitDirectoryAndFilename(s);
  	assertEquals("", res.get1());
  	assertEquals("4.xml", res.get2());
  }

  
}