package com.xenoage.util.zip;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xenoage.util.FileTools;


/**
 * Test cases for the {@link ZipTools} class.
 * 
 * @author Andreas Wenger
 */
public class ZipToolsTest
{
	
	File dir = new File(FileTools.getTempFolder(), getClass().getName() + ".extractAllTest");
	
	
	@Before public void setUp()
	{
		FileTools.deleteDirectory(dir);
		dir.mkdir();
	}
	
	
	@Test public void extractAllTest()
		throws IOException
	{
		ZipTools.extractAll(new FileInputStream("data/test/zip/album.zip"), dir);
		assertTrue(new File(dir, "META-INF/container.xml").exists());
		assertTrue(new File(dir, "BeetAnGeSample.xml").exists());
		assertTrue(new File(dir, "BrahWiMeSample.mxl").exists());
		assertTrue(new File(dir, "SchbAvMaSample.xml").exists());
	}
	
	
	@After public void cleanUp()
	{
		FileTools.deleteDirectory(dir);
	}

}
