package com.xenoage.util.language.text;

import com.xenoage.util.language.Lang;
import com.xenoage.util.language.VocByString;
import com.xenoage.util.language.VocabularyID;


/**
 * One piece of text identified by a vocabulary.
 * 
 * @author Andreas Wenger
 */
public class VocTextItem
	implements TextItem
{

	private final VocabularyID vocID;
	
	
	public VocTextItem(VocabularyID vocID)
	{
		this.vocID = vocID;
	}
	
	
	public VocTextItem(String vocID)
	{
		this.vocID = new VocByString(vocID);
	}

  
  @Override public String getText()
  {
  	return Lang.get(vocID);
  }
	
	
}
