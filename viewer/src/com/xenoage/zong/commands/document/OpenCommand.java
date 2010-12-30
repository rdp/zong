package com.xenoage.zong.commands.document;

import java.io.File;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.DesktopApp;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.documents.ViewerDocument;
import com.xenoage.util.RecentFiles;
import com.xenoage.util.logging.Log;


/**
 * Command for opening a given file.
 * Undoable.
 * 
 * The file is added to the list of recent files.
 *
 * @author Andreas Wenger
 */
public class OpenCommand
  extends Command
{
	
	private final String filepath;
	
	
	public OpenCommand(String filepath)
	{
		this.filepath = filepath;
	}
	

  @Override public void execute()
  {
  	File file = new File(filepath);
    App.getInstance().openFile(file); //TODO: what if fails?
    RecentFiles.addRecentFile(file);
    if (DesktopApp.getInstance() != null)
    {
    	DesktopApp.getInstance().getMainFrameController().updateMainMenuBar();
    }
  }
  
}
