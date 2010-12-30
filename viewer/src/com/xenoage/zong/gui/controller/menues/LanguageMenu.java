package com.xenoage.zong.gui.controller.menues;

import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.LanguageInfo;
import com.xenoage.util.language.text.VocTextItem;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.gui.event.LanguageChangeListener;


/**
 * Menu with all languages as child menu items.
 * 
 * @author Andreas Wenger
 */
public class LanguageMenu
	extends Menu
{
	
	
	public LanguageMenu()
	{
		super(new VocTextItem(Voc.Menu_Language), null);
	}
	

	/**
	 * Returns one JMenu with each language as a JMenuItem child.
	 */
	@Override public List<JMenuItem> createSwingComponents()
  {
		LinkedList<JMenuItem> ret = new LinkedList<JMenuItem>();
  	try
  	{
  		JMenu menu = new JMenu(text.getText());
	    //list languages
	    List<LanguageInfo> langs = LanguageInfo.getAvailableLanguages(Lang.defaultLangPath);
	    ButtonGroup grpLangs = new ButtonGroup();
	    for (LanguageInfo lang : langs)
	    {
	      JRadioButtonMenuItem mnuLang = new JRadioButtonMenuItem();
	      menu.add(mnuLang);
	      grpLangs.add(mnuLang);
	      //name
	      if (lang.getLocalName() != null)
	        mnuLang.setText(lang.getLocalName() + " (" +
	          lang.getInternationalName() + ")");
	      else
	        mnuLang.setText(lang.getInternationalName());
	      //flag
	      if (lang.getFlag16() != null)
	        mnuLang.setIcon(lang.getFlag16());
	      //activated?
	      if (Lang.getCurrentLanguageID().equals(lang.getID()))
	        mnuLang.setSelected(true);
	      //action listener
	      mnuLang.addActionListener(new LanguageChangeListener(lang.getID()));
	    }
	    ret.add(menu);
  	}
  	catch (Exception ex)
  	{
  		App.err().report(ErrorLevel.Fatal, "Could not read languages", ex); //TODO
  	}
  	return ret;
  }

}
