package com.xenoage.util.settings;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;



/**
 * Test cases for the {@link Setting} class.
 * 
 * The test data is read from the files
 * data/test/settings/test1.settings and
 * data/test/settings/test2.settings.
 * 
 * @author Andreas Wenger
 */
public class SettingsTest
{

	private Settings settings = null;
	private String directory = "data/test/settings";
	
	
	/**
	 * Loads the settings.
	 */
	@Before public void setUp()
	{
		settings = new Settings(directory);
	}
	
	
	/**
	 * Tests the reload method.
	 */
	@Test public void reload()
	{
		//changes a value
		settings.setSetting("changed", "test1", "my.first.setting");
		//reload the file
		settings.reload("test1");
		//must contain original value
		assertEquals("my first value", settings.getSetting("my.first.setting", "test1"));
	}
	
	
	/**
	 * Tests the getSetting method.
	 */
	@Test public void getSetting()
	{
		//reads 3 existing values
		settings.reload("test1");
		assertEquals("my first value", settings.getSetting("my.first.setting", "test1"));
		assertEquals("my second value", settings.getSetting("my.second.setting", "test1"));
		assertEquals("and yet another value", settings.getSetting("another.key", "test2"));
		//read nonexisting value
		assertNull(settings.getSetting("not.find", "test1"));
		assertNull(settings.getSetting("my.first.setting", "notfind"));
	}
	
	
	/**
	 * Tests the setSetting method.
	 */
	@Test public void setSetting()
	{
		//changes a value
		settings.setSetting("my.first.setting", "test1", "value changed");
		assertEquals("value changed", settings.getSetting("my.first.setting", "test1"));
		//adds a value
		settings.setSetting("a.new.key", "test2", "a newly created value");
		assertEquals("a newly created value", settings.getSetting("a.new.key", "test2"));
	}
	
	
	/**
	 * Tests the saveSetting method.
	 */
	@Test public void saveSetting()
	{
		//save to temp.settings
		settings.saveSetting("a.new.key", "temp", "a newly created value");
		settings.reload("test3");
		assertEquals("a newly created value", settings.getSetting("a.new.key", "temp"));
		//delete the temp file
		new File(directory + "/temp.settings").delete();
	}
	
	
	/**
	 * Tests the save method.
	 */
	@Test public void save()
	{
		File file = new File(directory + "/temp.settings");
		file.delete();
		settings.setSetting("a.new.key", "temp", "a newly created value");
		//file may not exist yet
		assertFalse(file.exists());
		settings.setSetting("a.new.key.2", "temp", "a second value");
		//file may still not exist
		assertFalse(file.exists());
		settings.save("temp");
		//file must exist now
		assertTrue(file.exists());
		//delete the temp file
		new File(directory + "/temp.settings").delete();
	}
	
	
}
