package com.xenoage.zong.commands;

import java.util.ArrayList;

import javax.swing.Icon;

import com.xenoage.util.logging.Log;


/**
 * A CommandList is a list of commands that
 * are combined to a single command.
 * 
 * They should be executed first, then added,
 * and then undone and redone together;
 * or they should be added first, then executed
 * together, undone and then redone.
 * But this is not controlled by this class,
 * so be careful what you do.
 *
 * @author Andreas Wenger
 */
public class CommandList
  extends Command
{
  
  private String name;
  private boolean history;
  private ArrayList<Command> commands = new ArrayList<Command>();
  
  
  /**
   * Creates a new command list with the given name.
   * TODO: doc
   */
  public CommandList(String name, boolean history)
  {
    this.name = name;
    this.history = history;
  }
  
  
  /**
   * Adds the given command to the end of the command list
   * without executing it now.
   */
  public void addCommand(Command command)
  {
    this.commands.add(command);
  }
  
  
  /**
   * Executes or redos all commands in this
   * list in forward direction.
   */
  @Override public void execute()
  {
    Log.log(Log.MESSAGE, this,
      "Executing CommandList \"" + name + "\" with " + commands.size() + " commands...");
    for (Command command : commands)
    {
      command.execute();
    }
  }
  
  
  /**
   * Returns true, if it is possible to undo
   * this CommandList. This is only true, if all commands in this list
   * are undoable.
   */
  @Override public boolean isUndoable()
  {
    for (Command command : commands)
    {
      if (!command.isUndoable())
        return false;
    }
    return true;
  }
  
  
  /**
   * If possible, undo this command.
   */
  @Override public void undo()
  {
    if (isUndoable())
    {
      Log.log(Log.MESSAGE, this,
        "Undoing CommandList \"" + name + "\" with " + commands.size() + " commands...");
      for (int i = commands.size() - 1; i >= 0; i--)
      {
        commands.get(i).undo();
      }
    }
    else
    {
      Log.log(Log.WARNING, this,
        "Can not undo CommandList \"" + name + "\" with " + commands.size() +
        " commands, since at least one command is not undoable.");
    }
  }
  
  
  /**
   * Returns true, if this command should be put
   * into the command history (for undo).
   * Do not include commands like save or print,
   * because they are not undoable and break the chain
   * of undoable commands.
   * Default: false
   */
  @Override public boolean isInHistory()
  {
    return history;
  }
  
  
  /**
   * Gets the name of the CommandList.
   */
  @Override public String getName()
  {
    return name;
  }
  
  
  /**
   * Gets the icon assigned to this command, or null.
   * TODO: allow icon
   */
  @Override public Icon getIcon()
  {
    return null;
  }
  
  
}
