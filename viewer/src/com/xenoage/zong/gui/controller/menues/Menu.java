package com.xenoage.zong.gui.controller.menues;

import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.xenoage.util.language.text.TextItem;


/**
 * A menu.
 * 
 * @author Andreas Wenger
 */
public class Menu
  extends MenuItem
{
  
  private LinkedList<MenuElement> children = new LinkedList<MenuElement>();
  
  
  /**
   * Creates a new {@link Menu}.
   * @param text       menu caption (optional)
   * @param icon       menu icon (optional)
   */
  public Menu(TextItem text, Icon icon)
  {
    super(text, icon, (String) null);
  }

  
  /**
   * Adds the given child menu item.
   */
  public void addChild(MenuElement child)
  {
  	children.add(child);
  }
  
  
  /**
   * Adds the given child menu items.
   */
  public void addChildren(List<MenuElement> children)
  {
  	this.children.addAll(children);
  }
  
  
  /**
   * Removes the given child menu item.
   */
  public void removeChild(MenuElement child)
  {
  	children.remove(child);
  }
  
  
  /**
   * Returns one JMenu containing all child JMenus/JMenuItems.
   */
	@Override public List<JMenuItem> createSwingComponents()
	{
		List<JMenuItem> ret = new LinkedList<JMenuItem>();
		JMenu menu = new JMenu(text.getText());
		menu.setIcon(icon);
		//children
		for (MenuElement child : children)
		{
			List<JMenuItem> childrenAsSwing = child.createSwingComponents();
			if (childrenAsSwing == null)
			{
				menu.addSeparator();
			}
			else
			{
				for (JMenuItem childAsSwing : childrenAsSwing)
				{
					if (childAsSwing != null)
						menu.add(childAsSwing);
					else
						menu.addSeparator();
				}
			}
		}
		ret.add(menu);
		return ret;
	}

  

}
