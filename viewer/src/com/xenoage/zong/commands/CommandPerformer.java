package com.xenoage.zong.commands;

import javax.swing.JMenuItem;

import com.xenoage.util.error.ErrorLevel;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.util.exceptions.PropertyAlreadySetException;
import com.xenoage.util.language.Lang;
import com.xenoage.util.logging.Log;


/**
 * This class executes or undoes commands and
 * saves a history of them, for exactly one document.
 *
 * @author Andreas Wenger
 */
public class CommandPerformer
{
	
	private ScoreDocument document;
  private DocumentHistory history;
  
  private JMenuItem mnuUndo = null, mnuRedo = null;
  
  
  /**
   * Creates a new {@link CommandPerformer} for the given {@link ScoreDocument}.
   */
  public CommandPerformer(ScoreDocument document)
  {
  	this.document = document;
  	this.history = new DocumentHistory(document);
  }
  
  
  /**
   * Performs the given Command.
   * If possible, the command is added to
   * the history, and the following commands
   * in the history are deleted.
   */
  public void execute(Command command)
  {
  	Log.log(Log.COMMAND, command.getClass().getName() + " is executed...");
  	try
  	{
	    command.execute();
	    if (command.isInHistory())
	    {
	    	history.removeFollowingCommands();
	    	history.addCommand(command);
	    	history.forward();
	      updateMenuItems();
	      document.repaint();
	    }
  	}
  	catch (PropertyAlreadySetException ex)
  	{
  		//no problem. we just ignore that exception.
  	}
  }
  
  
  /**
   * Performs the Command given by its ID.
   * If possible, the command is added to the history.
   */
  public void execute(String commandID)
  {
    Log.log(Log.MESSAGE, this, "Trying to instantiate command \"" + commandID + "\"...");
    try
    {
      @SuppressWarnings("unchecked") Class cmdClass =
      	Class.forName("com.xenoage.zong.commands." + commandID);
      Command cmd = (Command) cmdClass.newInstance();
      Log.log(Log.MESSAGE, this, "OK. Command created.");
      execute(cmd);
    }
    catch (Exception ex)
    {
      App.err().report(ErrorLevel.Warning, Voc.Error_CommandFailed, ex);
    }
  }
  
  
  /**
   * Undoes the last command in the history, if possible.
   */
  public void undo()
  {
    Command cmd = history.getLastCommand();
    if (cmd != null && cmd.isUndoable())
    {
    	try
    	{
    		Log.log(Log.COMMAND, cmd.getClass().getName() + " is undone...");
	      cmd.undo();
	      history.back();
	      updateMenuItems();
	      document.repaint();
	    }
	  	catch (PropertyAlreadySetException ex)
	  	{
	  		//no problem. we just ignore that exception.
	  	}
    }
  }
  

  /**
   * Redoes the next command in the history, if possible.
   */
  public void redo()
  {
    Command cmd = history.getLastUndoneCommand();
    if (cmd != null)
    {
      cmd.execute();
      history.forward();
      updateMenuItems();
      document.repaint();
    }
  }
  
  
  /**
   * Register menu item for undo.
   */
  public void registerMenuItemUndo(JMenuItem mnuUndo)
  {
    this.mnuUndo = mnuUndo;
  }
  
  
  /**
   * Register menu item for redo.
   */
  public void registerMenuItemRedo(JMenuItem mnuRedo)
  {
    this.mnuRedo = mnuRedo;
  }
  
  
  /**
   * Update the menu items for undo and redo.
   */
  public void updateMenuItems()
  {
  	Command lastCommand = history.getLastCommand();
  	Command lastUndoneCommand = history.getLastUndoneCommand();
  	//undo menu item
		if (mnuUndo != null)
		{
			Command cmd = lastCommand;
			if (cmd != null && cmd.isUndoable())
			{
				mnuUndo.setText(Lang.get(Voc.Menu_Edit_Undo) + ": " + cmd.getName());
				mnuUndo.setEnabled(true);
			}
			else
			{
				mnuUndo.setText(Lang.get(Voc.Menu_Edit_Undo) + ": "
					+ Lang.get(Voc.General_Impossible));
				mnuUndo.setEnabled(false);
			}
		}
		//redo menu item
		if (mnuRedo != null)
		{
			Command cmd = lastUndoneCommand;
			if (cmd != null)
			{
				mnuRedo.setText(Lang.get(Voc.Menu_Edit_Redo) + ": " + cmd.getName());
				mnuRedo.setEnabled(true);
			}
			else
			{
				mnuRedo.setText(Lang.get(Voc.Menu_Edit_Redo) + ": "
					+ Lang.get(Voc.General_Impossible));
				mnuRedo.setEnabled(false);
			}
		}
  }
  

}
