package com.xenoage.zong.commands.music;

import com.xenoage.zong.commands.Command;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.musiclayout.layouter.horizontalsystemfilling.HorizontalSystemFillingStrategy;
import com.xenoage.zong.util.exceptions.PropertyAlreadySetException;


/**
 * Undoable command for changing
 * the {@link HorizontalSystemFillingStrategy} of a
 * {@link ScoreFrame}.
 *
 * @author Andreas Wenger
 */
public class ScoreFrameHorizontalSystemFillingCommand
  extends Command
{
  
  private ScoreFrame scoreFrame;
  private HorizontalSystemFillingStrategy newFillingStrategy;
  private HorizontalSystemFillingStrategy oldFillingStrategy;
  
  
  /**
   * Creates a ScoreFrameHorizontalAlignmentCommand for the given score frame
   * with the given strategy.
   */
  public ScoreFrameHorizontalSystemFillingCommand(
  	ScoreFrame scoreFrame, HorizontalSystemFillingStrategy newFillingStrategy)
  	throws IllegalStateException
  {
    this.scoreFrame = scoreFrame;
    this.oldFillingStrategy = scoreFrame.getHorizontalSystemFillingStrategy();
    this.newFillingStrategy = newFillingStrategy;
  }
  

  /**
   * Applies the horizontal filling strategy.
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
    scoreFrame.setHorizontalSystemFillingStrategy(newFillingStrategy);
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
    scoreFrame.setHorizontalSystemFillingStrategy(oldFillingStrategy);
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
