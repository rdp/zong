package com.xenoage.util.language;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.language.Language;


/**
 * Test cases for the {@link Language} class.
 *
 * @author Andreas Wenger
 */
public class LanguageTest
{
	
	public enum TestVocabulary
		implements VocabularyID
	{
		Anything, TestVocabulary_TestVoc, TestVocabulary_TestVoc2, NotExisting;
		
		public String getID()
		{
			return this.toString();
		}
	}

  
  /**
   * Tests the get(String)-method.
   */
  @Test public void get1()
  	throws Exception
  {
    Language l = new Language("data/test/lang", "testlang", null);
    assertEquals("This is a test vocabulary.", l.get(TestVocabulary.TestVocabulary_TestVoc));
    assertNull(l.getWithNull(TestVocabulary.NotExisting));
  }
  
  
  /**
   * Tests the get(String, String[])-method.
   */
  @Test public void get2()
  	throws Exception
  {
  	Language l = new Language("data/test/lang", "testlang", null);
    String[] tokens = new String[]{"stupid", "- haha", "crazy"};
    assertEquals("This (stupid) text has some crazy tokens in it - haha.",
      l.get(TestVocabulary.TestVocabulary_TestVoc2, tokens));
  }
  
  
}
