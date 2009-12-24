package com.xenoage.zong.commands.music;

import com.xenoage.zong.commands.Command;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.musiclayout.layouter.verticalframefilling.VerticalFrameFillingStrategy;
import com.xenoage.zong.util.exceptions.PropertyAlreadySetException;


/**
 * Undoable command for changing
 * the {@link VerticalFrameFillingStrategy} of a
 * {@link ScoreFrame}.
 *
 * @author Andreas Wenger
 */
public class ScoreFrameVerticalFrameFillingCommand
  extends Command
{
  
  private ScoreFrame scoreFrame;
  private VerticalFrameFillingStrategy newFillingStrategy;
  private VerticalFrameFillingStrategy oldFillingStrategy;
  
  
  /**
   * Creates a ScoreFrameVerticalFrameFillingCommand for the given score frame
   * with the given strategy.
   */
  public ScoreFrameVerticalFrameFillingCommand(
  	ScoreFrame scoreFrame, VerticalFrameFillingStrategy newFillingStrategy)
  	throws IllegalStateException
  {
    this.scoreFrame = scoreFrame;
    this.oldFillingStrategy = scoreFrame.getVerticalFrameFillingStrategy();
    this.newFillingStrategy = newFillingStrategy;
  }
  

  /**
   * Applies the vertical filling strategy.
   * Throws an PropertyAlreadySetException if the new and the old strategy are
   * the same.
   */
  @Override public void execute()
    throws PropertyAlreadySetException
  {
    if (this.oldFillingStrategy == this.newFillingStrategy)
    {
      throw new PropertyAlreadySetException("New and old strategy are the same.");
    }
    scoreFrame.setVerticalFrameFillingStrategy(newFillingStrategy);
  }
  
  
  /**
   * Returns true, because this command is undoable.
   */
  @Override public boolean isUndoable()
  {
    return true;
  }
  
  
  /**
   * Undo this command.
   * Throws an PropertyAlreadySetException if the new and the old strategy are
   * the same.
   */
  @Override public void undo()
    throws PropertyAlreadySetException
  {
    if (this.oldFillingStrategy == this.newFillingStrategy)
    {
      throw new PropertyAlreadySetException("New and old strategy are the same.");
    }
    scoreFrame.setVerticalFrameFillingStrategy(oldFillingStrategy);
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
