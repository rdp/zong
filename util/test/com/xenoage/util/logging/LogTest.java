package com.xenoage.util.logging;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xenoage.util.FileTools;


/**
 * Test cases for a Log.
 *
 * @author Andreas Wenger
 */
public class LogTest
{
  
  private String logFilename;
  private File logFile;
  
  
  @Before public void setUp()
  {
    logFilename = "data/test/temp.log";
    logFile = new File(logFilename);
  }
  

  @Test public void testLogging()
  {
    logFile.delete();
    
    //create log file
    Log.initApplicationLog(logFilename, "test");
    String message = "This is a message";
    Log.log(Log.MESSAGE, message);
    for (int i = 0; i < 100; i++)
      Log.log(Log.MESSAGE, "Another row (" + i + ")");
    String warning = "This is a message";
    Log.log(Log.WARNING, warning);
    
    //check logfile
    assertTrue(logFile.exists());
    String logText = FileTools.readFile(logFile.getAbsolutePath());
    assertNotNull(logText);
    assertTrue(logText.contains(message));
    assertTrue(logText.contains(warning));
  }
  
  
  @After public void cleanUp()
  {
    Log.close();
    logFile.delete();
  }
  
}
