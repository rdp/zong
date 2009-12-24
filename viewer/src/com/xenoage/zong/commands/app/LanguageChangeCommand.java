package com.xenoage.zong.commands.app;

import com.xenoage.util.language.Lang;
import com.xenoage.zong.commands.Command;
import com.xenoage.util.logging.Log;


/**
 * This command changes the current language.
 * It is undoable and redoable.
 *
 * @author Andreas Wenger
 */
public class LanguageChangeCommand
  extends Command
{
  
  private String newLangID;
  
  
  /**
   * Creates a new LanguageChangeCommand.
   * @param  langID  ID of the new language
   */
  public LanguageChangeCommand(String langID)
  {
    this.newLangID = langID;
  }
  

  /**
   * Execute or redo the command.
   */
  @Override public void execute()
  {
    Log.log(Log.COMMAND, "LanguageChangeCommand is executed...");
    Lang.loadLanguage(newLangID);
  }
  
  
}
