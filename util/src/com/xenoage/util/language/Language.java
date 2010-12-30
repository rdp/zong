package com.xenoage.util.language;

import static com.xenoage.util.NullTools.notNull;

import com.xenoage.util.FileTools;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.io.IO;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.logging.Log;
import com.xenoage.util.xml.XMLReader;

import java.io.File;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Class for a language pack.
 * 
 * When a language is loaded, all .xml files from the given directory
 * are read and all values are stored.
 * 
 * Then, they can be queried using the <code>get</code> methods.
 * 
 * For non-verbose and quick access to a language pack
 * in the program, use the {@link Lang} class instead of this class.
 *
 * @author Andreas Wenger
 */
public class Language
{
  
	private final String id;
  private HashMap<String, String> entries = new HashMap<String, String>();
  
  
  /**
   * Loads a language pack from the directory <code>langPath + id</code>.
   * All ".voc" files in this directory are loaded.
   * If the method fails, an {@link Exception} is thrown. If single vocabulary files
   * are corrupted, loading proceeds but the errors are reported to the given error handler
   * (if given. otherwise an Exception is thrown).
   * @param path  path to the language pack directory (without trailing slash)
   * @param id    id of the language pack
   * @param err   error processing class
   */
  public Language(String path, String id, ErrorProcessing err)
  	throws Exception
  {
  	this.id = id;
  	
    String currentFile = "";
    Log.log(Log.MESSAGE, "Loading language pack \"" + id + "\"...");
    
    //locate vocabulary files
    Set<String> langFiles = IO.listDataFiles(path + "/" + id, FileTools.getVocFilter());
    
    //load entries
    entries = new HashMap<String, String>();
    int entriesCount = 0;
    int entriesOverwrittenCount = 0;
    for (String langFile : langFiles)
    {
      currentFile = new File(langFile).getName();
      Log.log(Log.MESSAGE, "Reading language file \"" + currentFile + "\"...");
      Document doc = null;
      doc = XMLReader.readFile(IO.openInputStream(path + "/" + id + "/" + currentFile));
      
      //check root element
      Element root = XMLReader.root(doc);
      if (!root.getNodeName().equals("vocabulary"))
      {
      	if (err != null)
      	{
      		err.report(ErrorLevel.Fatal, "Corrupted vocabulary file!",
      			new Exception(".voc file does not start with \"vocabulary\" element"), langFile);
      		continue;
      	}
      	else
      	{
      		throw new Exception("Corrupted vocabulary file: " +
      			".voc file does not start with \"vocabulary\" element: " + langFile);
      	}
      }
      
      //read package
      String packageName = "";
      if (root.getAttribute("package").length() > 0)
      {
      	packageName = root.getAttribute("package") + "_";
      }
      
      //read vocabulary data
      List<Element> eEntries = XMLReader.elements(root, "voc");
      for (int i = 0; i < eEntries.size(); i++)
      {
        Element e = (Element) eEntries.get(i);
        String eID = packageName + XMLReader.attributeNotNull(e, "key");
        String eValue = XMLReader.attributeNotNull(e, "value");
        eValue = eValue.replaceAll("\\\\n", "\n");
        String oldValue = entries.put(eID, eValue);
        if (oldValue == null)
          entriesCount++;
        else
        {
          Log.log(Log.WARNING, "Overwritten entry: " + eID);
          entriesOverwrittenCount++;
        }
      }
      Log.log(Log.MESSAGE,
        "Language pack loaded. Entries: " + entriesCount + 
          ". Overwritten entries: " + entriesOverwrittenCount);
    }
    
    //replace all tokens
    for (String key : entries.keySet())
    {
    	String value = entries.get(key);
    	if (value.indexOf("{") > -1)
    	{
    		entries.put(key, replaceTokens(value));
    	}
    }
  }
  
  
  /**
   * Gets the ID of this language.
   */
  public String getID()
  {
  	return id;
  }
  
  
  /**
   * Gets an entry from the language pack.
   * If no value is found, null is returned.
   */
  public String getWithNull(VocabularyID id)
  {
    return entries.get(id.getID());
  }
  
  
  /**
   * Gets an entry from the language pack.
   * If no value is found, the id is given back as a String
   * (because it's more useful for the user
   * than an empty string).
   */
  public String get(VocabularyID id)
  {
    return notNull(getWithNull(id), id.toString());
  }
  
  
  /**
   * Gets an entry from the language pack.
   * If no value is found, null is returned.
   */
  public String getWithNull(String id)
  {
    return entries.get(id);
  }
  
  
  /**
   * Gets an entry from the language pack.
   * If no value is found, the id is given back as a String
   * (because it's more useful for the user
   * than an empty string).
   */
  private String get(String id)
  {
    return notNull(entries.get(id), id);
  }
  
  
  /**
   * Gets an entry from the language pack.
   * If no value is found, the id is given back as a String
   * (because it's more useful for the user
   * than an empty string).
   * The tokens {1}, {2}, ... {n} are replaced
   * by the given Strings.
   */
  public String get(VocabularyID id, String... replacements)
  {
    String ret = get(id);
    //search for {n}-tokens and replace them
    for (int i = 0; i < replacements.length; i++)
    {
      ret = ret.replaceAll("\\{" + (i + 1) + "\\}", replacements[i]);
    }
    return ret;
  }
  
  
  /**
   * Replaces the following tokens in the given String and returns the result:
   * <ul>
   * 	<li>All registered tokens from the {@link Lang} class</li>
   * 	<li>{voc:xyz}: Inserts vocabulary with ID "xyz"</li>
   * </ul>
   */
  private String replaceTokens(String s)
  {
  	String ret = s;
  	for (Tuple2<String, String> t : Lang.getTokens())
  	{
  		ret = ret.replace(t.get1(), t.get2());
  	}
  	int pos;
  	while ((pos = ret.indexOf("{voc:")) > -1)
  	{
  		int pos2 = ret.indexOf("}", pos + 1);
  		if (pos2 > -1)
  		{
  			String id = ret.substring(pos + 5, pos2);
  			ret = ret.replaceFirst("\\{voc:" + id + "\\}", get(id));
  		}
  		else
  		{
  			ret = ret.replaceFirst("\\{voc:", "");
  		}
  	}
  	return ret;
  }
  
  
  /**
   * Gets all vocabulary keys.
   */
  public Set<String> getAllKeys()
  {
  	return entries.keySet();
  }
  
  
}
