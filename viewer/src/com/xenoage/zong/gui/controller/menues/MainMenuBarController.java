package com.xenoage.zong.gui.controller.menues;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xenoage.util.RecentFiles;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.xml.XMLReader;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.commands.document.OpenCommand;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageComponent;
import com.xenoage.util.language.LanguageInfo;
import com.xenoage.util.language.text.VocTextItem;
import com.xenoage.zong.gui.event.CommandListener;
import com.xenoage.zong.gui.event.LanguageChangeListener;
import com.xenoage.util.io.IO;
import com.xenoage.util.iterators.It;
import com.xenoage.util.logging.Log;


/**
 * Controller for the menu bar of the main frame.
 * 
 * The menu bar data is loaded from the file
 * data/gui/menu.xml.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class MainMenuBarController
{
  
  LinkedList<Menu> menus;
  JMenuBar menuSwing;
  
  
  /**
   * Creates a controller for the menu bar of
   * the main frame.
   */
  public MainMenuBarController()
  {
  	menus = new LinkedList<Menu>();
    
    //load the menu from a XML file
    Document menuDoc = null;
    try
    {
      menuDoc = XMLReader.readFile(IO.openInputStream("data/gui/menu.xml"));
	    //read root element
	    Element eMenuBar = XMLReader.element(menuDoc, "menubar");
	    if (eMenuBar == null)
	    {
	      throw new IOException("Invalid main menu file!");
	    }
	    //read menus
	    List<Element> listMenu = XMLReader.elements(eMenuBar, "menu");
	    for (Element eMenu : listMenu)
	    {
	    	menus.add(readMenu(eMenu));
	    }
    }
    catch (Exception ex)
    {
    	App.err().report(ErrorLevel.Fatal, Voc.Error_InvalidGUIFile, ex);
    }
    
    menuSwing = new JMenuBar();
    updateJMenuBar();
  }
  
  
  /**
   * Reads the menu and the child menus and menu items from
   * the given menu element.
   */
  private Menu readMenu(Element eMenu)
  {
    //read menu data
    String voc = "Menu_" + XMLReader.attribute(eMenu, "voc");
    String icon = XMLReader.attribute(eMenu, "icon");
    String id = XMLReader.attribute(eMenu, "id");
    Icon iconImage = (icon != null && icon.length() > 0 ? readIcon(icon) : null);
    //special menu?
    if (id != null)
    {
      //languages
      if (id.equals("language"))
      {
      	return new LanguageMenu();
      }
    }
    //normal menu
    Menu menu = new Menu(new VocTextItem(voc), iconImage);
  	//add children
    for (Element eChild : XMLReader.elements(eMenu))
    {
      if (eChild.getNodeName().equals("menu"))
      {
      	menu.addChild(readMenu(eChild));
      }
      else if (eChild.getNodeName().equals("menuitem"))
      {
      	menu.addChild(readMenuItem(eChild));
      }
      else if (eChild.getNodeName().equals("separator"))
      {
      	menu.addChild(new Separator());
      }
    }
    return menu;
  }
  
  
  /**
   * Reads the menu item from the given
   * menuitem element and adds it to the given parentMenu.
   */
  private MenuItem readMenuItem(Element eMenu)
  {
    //read menu item data
    String voc = "Menu_" + XMLReader.attribute(eMenu, "voc");
    String icon = XMLReader.attribute(eMenu, "icon");
    String command = XMLReader.attribute(eMenu, "command");
    String id = XMLReader.attribute(eMenu, "id");
    Icon iconImage = (icon != null && icon.length() > 0 ? readIcon(icon) : null);
    //special menu?
    if (id != null)
    {
    	MenuItem specialMenu = readSpecialMenuItem(id, voc, iconImage);
    	if (specialMenu != null)
    		return specialMenu;
    }
    //normal menu
    MenuItem menuItem = new MenuItem(new VocTextItem(voc), iconImage, command);
    return menuItem;
  }
  
  
  /**
   * Creates a MenuItem for the given ID, or returns null if unknown.
   */
  MenuItem readSpecialMenuItem(String id, String voc, Icon icon)
  {
  	//start playback
    if (id.equals("recentfiles"))
    {
    	return new RecentFilesMenuItem();
    }
    return null;
  }
  
  
  /**
   * Returns the icon with the given ID.
   * If it can not be read, null is returned and a warning is logged.
   */
  private Icon readIcon(String iconID)
  {
    try
    {
      Image img = ImageIO.read(IO.openInputStream("data/img/menu/16/" + iconID));
      return new ImageIcon(img);
    }
    catch (IOException ex)
    {
    	App.err().report(ErrorLevel.Remark, "Could not read icon", ex);
      return null;
    }
  }
  
  
  public JMenuBar getJMenuBar()
  {
    return menuSwing;
  }
  
  
  public void updateJMenuBar()
  {
  	menuSwing.removeAll();
    for (Menu menu : menus)
    {
    	for (JMenuItem item : menu.createSwingComponents())
    	{
    		menuSwing.add((JMenu) item);
    	}
    }
  }

  
}
