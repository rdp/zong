package com.xenoage.util.language;

import static com.xenoage.util.language.LanguageTest.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageInfo;
import com.xenoage.util.language.LanguageComponent;


/**
 * Test cases for the {@link Lang} class.
 * 
 * TODO: try to move these tests to the util project.
 *
 * @author Andreas Wenger
 */
public class LangTest
{
  
	
  @Test public void testCreateUnknownLanguage()
  {
  	/*
  	 * TODO: throws exception now, if no error processing is given
		Lang.unregisterAllComponents();
    Lang.loadLanguage("thiswillbeunknown");
    assertEquals("Unkown language: Must load English!", "en", Lang.getCurrentLanguageID());
    */
  }
  
  
  /**
   * Tests if at least one language pack can be found.
   */
  @Test public void testLanguagePacks()
  	throws Exception
  {
    List<LanguageInfo> langs = LanguageInfo.getAvailableLanguages(Lang.defaultLangPath);
    assertTrue("There must be at least one language pack!", langs.size() > 0);
    for (int i = 0; i < langs.size(); i++)
    {
      Lang.loadLanguage(langs.get(i).getID());
      assertNotNull("Null result in language " + Lang.getCurrentLanguageID(),
        Lang.get(TestVocabulary.Anything));
    }
  }

  
  /**
   * Tests the get(String)-method.
   */
  @Test public void get1()
  {
    Lang.loadLanguage("data/test/lang", "testlang");
    assertEquals("This is a test vocabulary.", Lang.get(TestVocabulary.TestVocabulary_TestVoc));
    assertNull(Lang.getWithNull(TestVocabulary.NotExisting));
  }
  
  
  /**
   * Tests the get(String, String[])-method.
   */
  @Test public void get2()
  {
    Lang.loadLanguage("data/test/lang", "testlang");
    String[] tokens = new String[]{"stupid", "- haha", "crazy"};
    assertEquals("This (stupid) text has some crazy tokens in it - haha.",
      Lang.get(TestVocabulary.TestVocabulary_TestVoc2, tokens));
  }
  
  
  /**
   * Test the registering and implicit unregistering of {@link LanguageComponent}s.
   */
  @Test public void testLanguageComponents()
  {
  	//create and add components
  	Lang.unregisterAllComponents();
  	LanguageComponentMock c1 = new LanguageComponentMock();
  	LanguageComponentMock c2 = new LanguageComponentMock();
  	Lang.registerComponent(c1);
  	Lang.registerComponent(c2);
  	assertEquals(2, Lang.getLanguageComponentsCount());
  	//update 1
  	Lang.updateLanguageComponents();
  	assertEquals(1, c1.languageChangedCounter);
  	assertEquals(1, c1.languageChangedCounter);
  	//update 2
  	Lang.updateLanguageComponents();
  	assertEquals(2, c1.languageChangedCounter);
  	assertEquals(2, c1.languageChangedCounter);
  	//delete component 1
  	c1 = null;
  	System.gc(); //ok, this no guarantee... can we test it in another way?
  	assertEquals(1, Lang.getLanguageComponentsCount());
  	//update 3
  	Lang.updateLanguageComponents();
  	assertEquals(3, c2.languageChangedCounter);
  	//delete other component
  	Lang.unregisterAllComponents();
  	assertEquals(0, Lang.getLanguageComponentsCount());
  }
  
  
}
