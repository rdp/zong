package com.xenoage.zong.commands.dialogs;

import java.io.File;

import javax.swing.JFileChooser;

import com.xenoage.util.settings.Settings;
import com.xenoage.zong.app.App;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.commands.document.OpenCommand;
import com.xenoage.zong.util.filefilter.MusicXMLFileFilter;
import com.xenoage.util.logging.Log;


/**
 * This command shows a dialog that allows to
 * select a MusicXML file for opening.
 *
 * @author Andreas Wenger
 */
public class OpenDocumentDialogCommand
  extends Command
{ 


	/**
	 * Shows the dialog.
	 */
  @Override public void execute()
  {
    JFileChooser fileChooser = new JFileChooser();
    
    //use last document directory
    String lastDocumentDir = Settings.getInstance().getSetting("lastdocumentdirectory", "paths");
    if (lastDocumentDir != null && new File(lastDocumentDir).exists())
    {
    	fileChooser.setCurrentDirectory(new File(lastDocumentDir));
    }
    else
    {
    	fileChooser.setCurrentDirectory(new File("files"));
    }
    
    //filter .xml and .mxl files
    fileChooser.addChoosableFileFilter(new MusicXMLFileFilter());
    
    //show the dialog
    int dialogResult = fileChooser.showOpenDialog(App.getInstance().getMainFrame());
    if (dialogResult == JFileChooser.APPROVE_OPTION)
    {
      File file = fileChooser.getSelectedFile();
      Log.log(Log.MESSAGE, this, "Dialog closed (OK), opening file \"" + file.getName() + "\"");
      new OpenCommand(file.getAbsolutePath()).execute();
    }
    else
    {
    	Log.log(Log.MESSAGE, this, "Dialog closed (Cancel)");
    }
    
    //save document directory
    Settings.getInstance().saveSetting("lastdocumentdirectory", "paths",
    	fileChooser.getCurrentDirectory().toString());
    
  }


}
