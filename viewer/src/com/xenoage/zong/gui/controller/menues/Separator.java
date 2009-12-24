package com.xenoage.zong.gui.controller.menues;

import java.util.List;

import javax.swing.JMenuItem;


/**
 * A menu seperator.
 * 
 * @author Andreas Wenger
 */
public class Separator
	implements MenuElement
{
  
  /**
   * Returns <code>null</code> by {@link MenuElement} convention.
   */
	@Override public List<JMenuItem> createSwingComponents()
	{
		return null;
	}

}
