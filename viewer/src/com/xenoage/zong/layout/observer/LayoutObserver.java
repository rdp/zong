package com.xenoage.zong.layout.observer;

import com.xenoage.zong.layout.Page;


/**
 * A layout observer gets notified
 * when something on a layout has
 * been changed.
 * 
 * TODO: check if this class is really needed
 * 
 * @author Andreas Wenger
 */
public interface LayoutObserver
{
	
	/**
	 * This method is called when a new page was added
	 * to the layout.
	 */
	public void pageAdded(Page page);
	
	
	/**
	 * This method is called when the format of the given page
	 * within the page layout has been changed.
	 */
	public void pageFormatChanged(Page page);

}
