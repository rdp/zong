package com.xenoage.util.language;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.iterators.It;
import com.xenoage.util.lang.Tuple2;

import java.lang.ref.WeakReference;
import java.util.*;


/**
 * This class manages a single language pack for
 * non-verbose and quick access.
 *
 * @author Andreas Wenger
 */
public class Lang
{
  
  public static final String defaultLangPath = "data/lang";
  
  private static Language currentLanguage = null;
  
  private static ErrorProcessing err = null;
  
  private static LinkedList<WeakReference<LanguageComponent>> languageComponents =
    new LinkedList<WeakReference<LanguageComponent>>();
  
  private static ArrayList<Tuple2<String, String>> tokens =
  	new ArrayList<Tuple2<String,String>>();
  
  
  /**
   * Sets the error processing class. If unset,
   * an exception will be thrown in any method where an error occurs.
   */
  public static void setErrorProcessing(ErrorProcessing err)
  {
  	Lang.err = err;
  }
  
  
  /**
   * Loads the given language pack from the default directory.
   * If this fails, it is tried to load the English language pack.
   * If this fails too, a fatal error is thrown.
   * @param id    id of the language pack
   */
  public static void loadLanguage(String id)
  {
  	loadLanguage(defaultLangPath, id);
  }
  
  
  /**
   * Loads the given language pack from the given directory.
   * If this fails, it is tried to load the English language pack.
   * If this fails too, a fatal error is thrown.
   * @param path  path to the language pack directory (without trailing slash)
   * @param id    id of the language pack
   */
  public static void loadLanguage(String path, String id)
  {
    try
    {
    	currentLanguage = new Language(path, id, err);
    }
    catch (Exception ex)
    {
    	//loading language pack failed
    	if (err != null)
	    	err.report(ErrorLevel.Warning, "The language \"" + id +
	    		"\" could not be loaded. Loading English pack instead...", ex);
    	else
    		throw new RuntimeException(ex);
    	//try to load English one
    	try
      {
      	currentLanguage = new Language(path, "en", err);
      }
      catch (Exception ex2)
      {
      	if (err != null)
      		err.report(ErrorLevel.Fatal, "The language \"" + id + "\" could not be loaded.", ex2);
      	else
      		throw new RuntimeException(ex2);
      }
    }
    updateLanguageComponents();
  }
  
  
  /**
   * Ensure that a language pack was loaded. If not,
   * throw a fatal error.
   */
  private static void ensureLanguageLoaded()
  {
  	if (currentLanguage == null)
  	{
  		if (err != null)
  			err.report(ErrorLevel.Fatal, "No language loaded!");
  		else
    		throw new RuntimeException("No language loaded!");
  	}
  }
  
  
  /**
   * Gets an entry from the language pack.
   * If no value is found, null is returned.
   */
  public static String getWithNull(VocabularyID id)
  {
  	ensureLanguageLoaded();
    return currentLanguage.getWithNull(id);
  }
  
  
  /**
   * Gets an entry from the language pack.
   * If no value is found, the id is given back as a String
   * (because it's more useful for the user
   * than an empty string).
   */
  public static String get(VocabularyID id)
  {
  	ensureLanguageLoaded();
    return currentLanguage.get(id);
  }
  
  
  /**
   * Gets an entry from the language pack.
   * If no value is found, the id is given back as a String
   * (because it's more useful for the user
   * than an empty string).
   * The tokens {1}, {2}, ... {n} are replaced
   * by the given Strings.
   */
  public static String get(VocabularyID id, String... replacements)
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
   * Gets the ID of the current language.
   */
  public static String getCurrentLanguageID()
  {
    return currentLanguage.getID();
  }
  
  
  /**
   * Register the given {@link LanguageComponent}.
   * Every time the language is changed, the <code>languageChanged()</code>
   * method of all registered components is called.
   * 
   * Unregistering is not necessary. This class stores only weak
   * references of the components, so they can be removed by the
   * garbage collector when they are not used any more.
   */
  public static void registerComponent(LanguageComponent component)
  {
  	removeObsoleteLanguageComponents();
    if (component != null)
      languageComponents.add(new WeakReference<LanguageComponent>(component));
  }
  
  
  /**
   * Unregister the all LanguageComponents.
   */
  static void unregisterAllComponents()
  {
    languageComponents.clear();
  }
  
  
  /**
   * Update all registered language components.
   */
  static void updateLanguageComponents()
  {
  	removeObsoleteLanguageComponents();
    for (WeakReference<LanguageComponent> component : languageComponents)
    {
      component.get().languageChanged();
    }
  }
  
  
  /**
   * Removes all obsolete language components.
   */
  static void removeObsoleteLanguageComponents()
  {
  	for (WeakReference<LanguageComponent> component : languageComponents)
    {
      if (component == null || component.get() == null)
        languageComponents.remove(component);
    }
  }
  
  
  /**
   * Gets the number of registered language components.
   */
  static int getLanguageComponentsCount()
  {
  	removeObsoleteLanguageComponents();
  	return languageComponents.size();
  }
  
  
  /**
   * Register a token for replacement, e.g. "{app.name}" by "Xenoage WhatEver".
   */
  public static void registerToken(String symbol, String value)
  {
  	tokens.add(new Tuple2<String, String>(symbol, value));
  }
  
  
  /**
   * Gets an iterator over the tokens for replacement.
   */
  public static It<Tuple2<String, String>> getTokens()
  {
  	return new It<Tuple2<String, String>>(tokens);
  }
  
  
}
