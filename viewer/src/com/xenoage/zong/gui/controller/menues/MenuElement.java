package com.xenoage.zong.gui.controller.menues;

import java.util.List;

import javax.swing.JMenuItem;


/**
 * Interface for an element in the menu.
 * 
 * There must be a method that creates one or more
 * {@link JMenuItem} (or subclass) instances of this element.
 * Special case (needed because of Swing's design):
 * <code>null</code> means a separator.
 * 
 * @author Andreas Wenger
 */
public interface MenuElement
{
	
	public List<JMenuItem> createSwingComponents();
  
}
