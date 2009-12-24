package com.xenoage.util.language;


/**
 * Vocabulary IDs for commands.
 * 
 * @author Andreas Wenger
 */
public class VocByString
	implements VocabularyID
{
	
	private final String id;
	
	
	/**
	 * Creates a new {@link VocabularyID} using the given vocabulary id
	 * like "Error_Unknown".
	 */
	public VocByString(String vocID)
	{
		this.id = vocID;
	}
	
	
	/**
	 * Creates a new {@link VocabularyID} using the given package name
	 * and vocabulary name, like "Error" and "Unknown".
	 */
	public VocByString(String packageName, String vocName)
	{
		this.id = packageName + "_" + vocName;
	}

	
	/**
	 * Gets the ID of the vocabulary as a String.
	 */
	public String getID()
	{
		return id;
	}
	
	
	@Override public String toString()
	{
		return id;
	}


}
