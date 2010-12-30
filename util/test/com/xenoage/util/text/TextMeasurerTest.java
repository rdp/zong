package com.xenoage.util.text;

import static org.junit.Assert.assertEquals;

import java.awt.Font;

import javax.xml.ws.ServiceMode;

import org.junit.Before;
import org.junit.Test;

import com.xenoage.util.font.FontTools;


/**
 * Test cases for the {@link TextMeasurer} class.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class TextMeasurerTest
{
	
	private boolean windowsFontsAvailable = true;
	
	
	@Before public void setUp()
	{
		//find out if Verdana and Times New Roman are installed.
		//if not, skip the tests.
		windowsFontsAvailable &= FontTools.getInstance().isFontFamilySupported("Verdana");
		windowsFontsAvailable &= FontTools.getInstance().isFontFamilySupported("Times New Roman");
	}
	
	
	@Test public void getWidthTest()
	{
		if (windowsFontsAvailable)
		{
			Font font = new Font("Verdana", Font.PLAIN, 72);
			float fw = new TextMeasurer(font, "Hallo Welt").getWidth();
			assertEquals(127,fw,1.5);
					
			font = new Font("Verdana", Font.PLAIN, 72);
			fw = new TextMeasurer(font, "Hallo WeltHallo WeltHallo WeltHallo Welt").getWidth();
			assertEquals(519,fw,3);
			
			font = new Font("Times New Roman", Font.PLAIN, 120);
			fw = new TextMeasurer(font, "Using Times").getWidth();
			assertEquals(216f,fw,1f);
		}
	}
	
	
	@Test public void getAscentTest()
	{
		if (windowsFontsAvailable)
		{
			Font font = new Font("Verdana", Font.PLAIN, 72);
			float f = new TextMeasurer(font, "Mein Ascent").getAscent();
			assertEquals(25f,f,1f);
			
			font = new Font("Verdana", Font.PLAIN, 12);
			f = new TextMeasurer(font, "Mein Ascent").getAscent();
			assertEquals(4.2f,f,0.1f);
			
			font = new Font("Times New Roman", Font.PLAIN, 120);
			f = new TextMeasurer(font, "Mein Ascent").getAscent();
			assertEquals(38f,f,1f);
		}
	}
	
}
