package com.xenoage.util.language;

import com.xenoage.util.io.IO;
import com.xenoage.util.xml.XMLReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Contains the most important information
 * about a language pack.
 *
 * @author Andreas Wenger
 */
public class LanguageInfo
{
  
	private String path;
  private String id;
  private String localName;
  private String internationalName;
  private ImageIcon flag16;
  
  
  /**
   * Loads information from the given language pack information file.
   * @param path  path to the language pack directory (without trailing slash)
   * @param id    id of the language pack
   */
  public LanguageInfo(String path, String id) throws Exception
  {
  	this.path = path;
  	this.id = id;
    Document doc = XMLReader.readFile(IO.openInputStream(path + "/" + id + "/id.xml"));
    Element root = XMLReader.root(doc);
    Element intName = XMLReader.element(root, "intname");
    Element localName = XMLReader.element(root, "localname");
    if (intName != null)
      this.internationalName = XMLReader.text(intName);
    else
      throw new Exception("intname must be set!");
    if (localName != null)
      this.localName = XMLReader.text(localName);
    else
      this.localName = null;
    loadFlagImage();
  }


  /**
   * Gets the ID of this language pack.
   */
  public String getID()
  {
    return id;
  }


  /**
   * Gets the international name of this language file,
   * e.g. "German" or "French".
   */
  public String getInternationalName()
  {
    return internationalName;
  }


  /**
   * Gets the local name of this language file,
   * e.g. "Deutsch" or "Français". If not set, null
   * is returned. This may be the case if the local
   * name and the international name is the same
   * (like "English").
   */
  public String getLocalName()
  {
    return localName;
  }
  
  
  /**
   * Gets the 16px flag icon of this language, or null.
   */
  public Icon getFlag16()
  {
    return flag16;
  }
  
  
  /**
   * Gets a list of the available languages.
   * @param path  path to the language pack directory (without trailing slash)
   */
  public static List<LanguageInfo> getAvailableLanguages(String path)
  	throws Exception
  {
    ArrayList<LanguageInfo> ret = new ArrayList<LanguageInfo>();
    Set<String> langs = IO.listDataDirectories(path);
    if (langs == null)
    {
      throw new Exception("Languages directory does not exist!");
    }
    else if (langs.size() < 1)
    {
    	throw new Exception("No language pack installed!");
    }
    else
    {
      for (String lang : langs)
      {
        try
        {
          ret.add(new LanguageInfo(path, lang));
        }
        catch (Exception ex)
        {
        	throw new Exception("Language pack \"" + lang + "\" could not be loaded!");
        }
      }
    }
    return ret;
  }
  
  
  /**
   * Gets the ID of the default language, e.g. "de" if German is the
   * user's default language. If it doesn't exist, "en" is returned.
   */
  public static String getDefaultID(List<LanguageInfo> availableLanguages)
  {
  	Locale locale = Locale.getDefault();
  	String id = locale.getLanguage();
  	//language available?
  	boolean available = false;
  	for (LanguageInfo info : availableLanguages)
  	{
  		if (info.getID().equals(id))
  		{
  			available = true;
  			break;
  		}
  	}
  	return (available ? id : "en");
  }
  
  
  /**
   * Loads the 16px flag, if there.
   */
  private void loadFlagImage()
  {
    File flag16File = new File(path + "/" + id + "/flag16.png");
    if (flag16File.exists())
      flag16 = new ImageIcon(flag16File.getAbsolutePath());
    else
      flag16 = null;
  }
  
  
}
