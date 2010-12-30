package com.xenoage.build.entities;

import java.util.ArrayList;


/**
 * List of dependent {@link CodeItem}s.
 * 
 * @author Andreas Wenger
 */
public class Dependencies
{
	
	private ArrayList<CodeItem> codeItems;
	
	
	public static Dependencies depends(CodeItem... codeItems)
	{
		return new Dependencies(codeItems);
	}
	
	
	private Dependencies(CodeItem... codeItems)
	{
		this.codeItems = new ArrayList<CodeItem>(codeItems.length);
		for (int i = 0; i < codeItems.length; i++)
			this.codeItems.add(codeItems[i]);
	}
	
	
	public ArrayList<CodeItem> getCodeItems()
	{
		return codeItems;
	}
	
	
	public void addAll(Dependencies dependencies)
	{
		for (CodeItem item : dependencies.getCodeItems())
		{
			if (!this.codeItems.contains(item))
				this.codeItems.add(item);
		}
	}
	
	
	public int size()
	{
		return codeItems.size();
	}
	

}
