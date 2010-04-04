package com.xenoage.zong.io.musicxml.in;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.xenoage.util.FileTools;
import com.xenoage.zong.core.Score;


/**
 * Tests for the {@link MxlScoreDocumentFileInput} class.
 * 
 * @author Andreas Wenger
 */
public class MxlScoreDocumentFileInputTest
{
	
	private static final String dir11 = "../shared/data/test/musicxml11/";
	private static final String dir20 = "../shared/data/test/musicxml20/";
	
	
	/**
   * It's too hard to check the contents of a MusicXML file
   * automatically. We just try to load the score data and
   * check if they could be loaded without problems.
   * 
   * We check all official MusicXML 1.1 and 2.0 sample files.
   */
  @Test public void testSampleFiles()
  {
    for (String file : getSampleFiles())
    {
    	try
    	{
	      new MxlScoreDocumentFileInput().read(file);
    	}
      catch (Exception ex)
      {
        ex.printStackTrace();
        fail("Failed to load file: " + file);
      }
    }
  }
  
  
  /**
   * It's too hard to check the contents of a MusicXML file
   * automatically. We just try to load the score data and
   * check if they could be loaded without problems.
   * 
   * We check all official MusicXML 1.1 sample files.
   */
  public static List<String> getSampleFiles()
  {
  	List<String> ret = new LinkedList<String>();
    String[] files = new File(dir11).list(FileTools.getXMLFilter());
    for (String file : files)
    {
    	ret.add(dir11 + file);
    }
    files = new File(dir20).list(FileTools.getXMLFilter());
    for (String file : files)
    {
    	ret.add(dir20 + file);
    }
    return ret;
  }
  
  
  /**
   * Loads the given file from "data/test/scores/" as MusicXML 1.1
   * and returns the score.
   */
  public static Score loadXMLTestScore(String filename)
  {
  	try
  	{
  		String filepath = "data/test/scores/" + filename;
  		return new MxlScoreDocumentFileInput().read(filepath).getScore(0);
  	}
  	catch (Exception ex)
  	{
  		ex.printStackTrace();
  		fail("Could not load file: " + filename);
  		return null;
  	}
  }
  

}
