package com.xenoage.zong.gui.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.xenoage.zong.app.App;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.documents.ScoreDocument;


/**
 * ActionListener for a menu item connected to a command.
 * 
 * @author Andreas Wenger
 */
public class CommandListener
	implements ActionListener
{
  
	private final Command command;
  private final String commandID;
  
  
  public CommandListener(Command command)
  {
    this.command = command;
    this.commandID = null;
  }
  
  
  public CommandListener(String commandID)
  {
    this.commandID = commandID;
    this.command = null;
  }
  

  public void actionPerformed(ActionEvent arg0)
  {
  	//TODO: command performer for general tasks!!
  	ScoreDocument doc = App.getInstance().getScoreDocument();
  	if (command != null)
  		doc.getCommandPerformer().execute(command);
  	else
  		doc.getCommandPerformer().execute(commandID);
  }

}
