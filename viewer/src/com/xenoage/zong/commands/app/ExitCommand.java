package com.xenoage.zong.commands.app;

import com.xenoage.zong.app.DesktopApp;
import com.xenoage.zong.commands.Command;


/**
 * Requests to close the application.
 *
 * @author Andreas Wenger
 */
public class ExitCommand
  extends Command
{

  /**
   * Execute or redo the command.
   */
  @Override public void execute()
  {
    if (DesktopApp.getInstance() != null)
    {
    	DesktopApp.getInstance().close();
    }
  }
  
  
}
