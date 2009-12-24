package com.xenoage.zong.gui.controller.menues;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;

import com.xenoage.util.RecentFiles;
import com.xenoage.util.iterators.It;
import com.xenoage.zong.commands.document.OpenCommand;
import com.xenoage.zong.gui.event.CommandListener;


/**
 * Menu item for the recent files.
 * From this item, a list of Swing {@link JMenuItem}s and
 * a separator is generated.
 * 
 * @author Andreas Wenger
 */
public class RecentFilesMenuItem
	extends MenuItem
{
	
	
	public RecentFilesMenuItem()
	{
		super(null, null, (String) null);
	}
	

	/**
	 * Returns one JMenu with each language as a JMenuItem child.
	 */
	@Override public List<JMenuItem> createSwingComponents()
  {
		LinkedList<JMenuItem> ret = new LinkedList<JMenuItem>();
		List<File> recentFiles = RecentFiles.getRecentFiles();
  	if (recentFiles.size() > 0)
  	{
  		It<File> it = new It<File>(recentFiles);
	  	for (File recentFile : it)
	  	{
	  		JMenuItem item = new JMenuItem((it.getIndex() + 1) + ".  " + recentFile.getName());
	  		item.addActionListener(new CommandListener(new OpenCommand(recentFile.getAbsolutePath())));
	  		ret.add(item);
	  	}
	  	ret.add(null); //null is separator by convention (see MenuElement)
	  }
  	return ret;
  }
	

}
