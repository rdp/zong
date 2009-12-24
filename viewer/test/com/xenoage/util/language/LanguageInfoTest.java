package com.xenoage.util.language;

import static org.junit.Assert.*;

import com.xenoage.util.FileTools;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageInfo;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * Test cases for a {@link LanguageInfo}.
 *
 * @author Andreas Wenger
 */
public class LanguageInfoTest
{
  
  private List<LanguageInfo> langs;
  
  
  @Before public void setUp()
  	throws Exception
  {
    langs = LanguageInfo.getAvailableLanguages(Lang.defaultLangPath);
    assertTrue("There must be at least one language pack!", langs.size() > 0);
  }
  

  @Test public void getAvailableLanguages()
  {
    //all folders from language pack folder must be returned
    File[] folders = new File(Lang.defaultLangPath).listFiles(
      FileTools.getDirectoriesFilter());
    assertEquals(folders.length, langs.size());
    for (File folder : folders)
    {
      boolean found = false;
      for (LanguageInfo lang : langs)
      {
        if (lang.getID().equals(folder.getName()))
        {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }
  
  
  @Test public void getLangInformation()
  {
    String errorMsg = "international name must be set";
    for (LanguageInfo lang : langs)
    {
      assertNotNull(errorMsg, lang.getInternationalName());
    }
  }
  
  
  @Test public void getFlag16()
  {
    String errorMsg = "flag16 must be set, because there is a flag16.png";
    for (LanguageInfo lang : langs)
    {
      if (new File(Lang.defaultLangPath + "/" + lang.getID() + "/flag16.png").exists())
        assertNotNull(errorMsg, lang.getFlag16());
    }
  }
  
}
