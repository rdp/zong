package com.xenoage.util.language;

import com.xenoage.util.language.LanguageComponent;


/**
 * An simple {@link LanguageComponent} for testing.
 * 
 * @author Andreas Wenger
 */
public class LanguageComponentMock
	implements LanguageComponent
{
	
	public int languageChangedCounter = 0;
	

	public void languageChanged()
	{
		languageChangedCounter++;
	}

}
