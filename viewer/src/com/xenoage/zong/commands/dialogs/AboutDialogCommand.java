package com.xenoage.zong.commands.dialogs;

import com.xenoage.zong.Zong;
import com.xenoage.zong.app.App;
import com.xenoage.zong.commands.Command;


/**
 * Shows an about-dialog.
 *
 * @author Andreas Wenger
 */
public class AboutDialogCommand
  extends Command
{

  @Override public void execute()
  {
		App.getInstance().showMessageDialog(App.getInstance().getNameAndVersion() +
			" (" + Zong.PROJECT_ITERATION_NAME + ")\n\n" + Zong.COPYRIGHT);
  }
	
}
