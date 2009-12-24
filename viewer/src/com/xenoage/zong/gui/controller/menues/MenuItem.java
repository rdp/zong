package com.xenoage.zong.gui.controller.menues;

import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import com.xenoage.util.language.text.TextItem;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.gui.event.CommandListener;


/**
 * A menu item.
 * 
 * @author Andreas Wenger
 */
public class MenuItem
	implements MenuElement
{
  
  TextItem text;
  Icon icon;
  
  String commandID; //TIDY: create command-interface with subclasses Command and CommandByString
  Command command;
  
  
  /**
   * Creates a new {@link MenuItem}.
   * @param text       menu caption (optional)
   * @param icon       menu icon (optional)
   * @param commandID  ID of the command, that is performed when the item is clicked (optional)
   */
  public MenuItem(TextItem text, Icon icon, String commandID)
  {
  	this.text = text;
  	this.icon = icon;
    this.commandID = commandID;
    this.command = null;
  }
  
  
  /**
   * Creates a new {@link MenuItem}.
   * @param text       menu caption (optional)
   * @param icon       menu icon (optional)
   * @param commandI   command, that is performed when the item is clicked (optional)
   */
  public MenuItem(TextItem text, Icon icon, Command command)
  {
  	this.text = text;
  	this.icon = icon;
    this.commandID = null;
    this.command = command;
  }
  
  
  /**
   * Returns one JMenuItem.
   */
	@Override public List<JMenuItem> createSwingComponents()
	{
		List<JMenuItem> ret = new LinkedList<JMenuItem>();
		JMenuItem menu = new JMenuItem(text.getText(), icon);
		if (command != null)
			menu.addActionListener(new CommandListener(command));
		else if (commandID != null)
			menu.addActionListener(new CommandListener(commandID));
		ret.add(menu);
		return ret;
	}

}
