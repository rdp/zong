package com.xenoage.util.font;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.font.FontReplacements;


/**
 * Test cases for a {@link FontReplacements} class.
 * 
 * TODO: remove test case config file from "data/config", put into "data/test/config".
 *
 * @author Andreas Wenger
 */
public class FontReplacementsTest
{
	
	
	@Test public void testGetReplacement()
	{
		//"Times New Roman" is replaced by "Linux Libertine"
		assertEquals("Linux Libertine", FontReplacements.getInstance().getReplacement("Times New Roman"));
		//"No Way, This is No Font" is not replaced
		assertEquals("No Way, This is No Font", FontReplacements.getInstance().getReplacement("No Way, This is No Font"));
	}

}
