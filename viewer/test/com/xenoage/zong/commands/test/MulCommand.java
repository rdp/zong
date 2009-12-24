package com.xenoage.zong.commands.test;

import com.xenoage.zong.commands.Command;



/**
 * This command is written for testing only.
 * 
 * It multiplies a given number to a
 * given Integer instance.
 * 
 * Is is undoable and divides through the
 * given number when undone.
 *
 * @author Andreas Wenger
 */
public class MulCommand
  extends Command
{
  
  private int[] number;
  private int mulNumber;
  
  
  /**
   * Creates a new AddCommand.
   * @param number     one Integer instance that will be changed
   * @param mulNumber  the number to multiply
   */
  public MulCommand(int[] number, int mulNumber)
  {
    this.number = number;
    this.mulNumber = mulNumber;
  }
  

  /**
   * Execute or redo the command.
   */
  @Override public void execute()
  {
    number[0] *= mulNumber;
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
    number[0] /= mulNumber;
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
