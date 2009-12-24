package com.xenoage.zong.commands.document;

import com.xenoage.zong.app.App;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.documents.ViewerDocument;
import com.xenoage.util.logging.Log;


/**
 * This class encapsulates
 * a print request.
 *
 * @author Andreas Wenger
 */
public class PrintCommand
  extends Command
{

  /**
   * Execute or redo the command.
   */
  @Override public void execute()
  {
    Log.log(Log.COMMAND, "PrintCommand is executed...");
    ViewerDocument doc = App.getInstance().getActiveDocument();
    if (doc != null)
      doc.requestPrint();
  }
  
}
