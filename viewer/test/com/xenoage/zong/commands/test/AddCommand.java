package com.xenoage.zong.commands.test;

import com.xenoage.zong.commands.Command;



/**
 * This command is written for testing only.
 * 
 * It adds a given number to a given
 * Integer instance.
 * 
 * Is is undoable and subtracts the given
 * number when undone.
 *
 * @author Andreas Wenger
 */
public class AddCommand
  extends Command
{
  
  private int[] number;
  private int addNumber;
  
  
  /**
   * Creates a new AddCommand.
   * @param number     one Integer instance that will be changed
   * @param addNumber  the number to add
   */
  public AddCommand(int[] number, int addNumber)
  {
    this.number = number;
    this.addNumber = addNumber;
  }
  

  /**
   * Execute or redo the command.
   */
  @Override public void execute()
  {
    number[0] += addNumber;
  }

  
  /**
   * Returns true, because this command can be undone.
   */
  @Override public boolean isUndoable()
  {
    return true;
  }
  
  
  /**
   * Undo this command.
   */
  @Override public void undo()
  {
    number[0] -= addNumber;
  }
  
  
  /**
   * Returns true, because this command should be put
   * into the command history (for undo).
   */
  @Override public boolean isInHistory()
  {
    return true;
  }
  
  
}
