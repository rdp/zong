package com.xenoage.zong.commands;

import java.util.ArrayList;

import com.xenoage.zong.documents.ScoreDocument;


/**
 * Command history for a ScoreDocument.
 * 
 * @author Andreas Wenger
 */
public class DocumentHistory
{
  
  private ScoreDocument document;
  private ArrayList<Command> history = new ArrayList<Command>();
  private int historyPosition = -1;
  
  
  public DocumentHistory(ScoreDocument document)
  {
    this.document = document;
  }
  
  
  public ScoreDocument getScoreDocument()
  {
    return document;
  }
  
  
  public void addCommand(Command command)
  {
    history.add(command);
  }
  
  
  public void forward()
  {
    historyPosition++;
  }
  
  
  public void back()
  {
    historyPosition--;
  }

  
  /**
   * Gets the last executed {@link Command}, or null if there is none.
   */
  public Command getLastCommand()
  {
    if (historyPosition > -1)
    {
      return history.get(historyPosition);
    }
    else
    {
      return null;
    }
  }
  
  
  public Command getLastUndoneCommand()
  {
    if (history.size() > historyPosition + 1)
    {
      return history.get(historyPosition + 1);
    }
    else
    {
      return null;
    }
  }
  
  
  /**
   * Delete all commands that follow the
   * current command.
   */
  public void removeFollowingCommands()
  {
    int numberToRemove = history.size() - historyPosition - 1;
    for (int i = 0; i < numberToRemove; i++)
    {
      history.remove(history.size() - 1);
    }
  }
  

}
