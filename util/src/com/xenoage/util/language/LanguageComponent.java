package com.xenoage.util.language;


/**
 * Interface for any component that contains
 * language pack dependent texts or content.
 * 
 * This program can switch languages without
 * the need of restarting, so each component
 * contains a method to update the content
 * to the current language.
 * 
 * The method is called each time when the
 * Lang class has loaded a new language, but
 * the component must register there before.
 * 
 * Language components need not to be unregistered.
 * This is done automatically by the {@link Lang}
 * class when there are no more references.
 *
 * @author Andreas Wenger
 */
public interface LanguageComponent
{

  /**
   * This method must be called when the language
   * was changed.
   */
  public void languageChanged();
  
  
}
