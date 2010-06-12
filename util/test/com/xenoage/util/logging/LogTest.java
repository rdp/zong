package com.xenoage.util.logging;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xenoage.util.FileTools;
import com.xenoage.util.io.IO;


/**
 * Test cases for a Log.
 *
 * @author Andreas Wenger
 */
public class LogTest
{
  
  private String logFilename;
  
  
  @Before public void setUp()
  {
  	IO.initTest();
    logFilename = "data/test/temp.log";
  }
  

  @Test public void testLogging()
  {
    IO.deleteDataFile(logFilename, true);
    
    //create log file
    Log.initApplicationLog(logFilename, "test");
    String message = "This is a message";
    Log.log(Log.MESSAGE, message);
    for (int i = 0; i < 100; i++)
      Log.log(Log.MESSAGE, "Another row (" + i + ")");
    String warning = "This is a message";
    Log.log(Log.WARNING, warning);
    
    //check logfile
    assertTrue(IO.existsDataFile(logFilename));
    String logText = FileTools.readFile(logFilename);
    assertNotNull(logText);
    assertTrue(logText.contains(message));
    assertTrue(logText.contains(warning));
  }
  
  
  @After public void cleanUp()
  {
    Log.close();
    IO.deleteDataFile(logFilename, true);
  }
  
}
