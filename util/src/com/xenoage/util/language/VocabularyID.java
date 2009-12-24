package com.xenoage.util.language;


/**
 * This interface must be implemented by all enums that contain
 * vocabulary IDs.
 * 
 * There must be a method <code>getID</code> that returns the ID
 * as a String, in the format "EnumClassName.EnumValueName",
 * needed to index the vocabulary in XML files.
 * 
 * @author Andreas Wenger
 */
public interface VocabularyID
{
	
	
	/**
	 * Gets the ID of the vocabulary as a String.
	 */
	public String getID();
	

}
