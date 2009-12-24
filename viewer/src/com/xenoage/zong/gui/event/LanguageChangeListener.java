package com.xenoage.zong.gui.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.xenoage.zong.app.App;
import com.xenoage.zong.commands.app.LanguageChangeCommand;
import com.xenoage.zong.documents.ScoreDocument;


/**
 * ActionListener for a language menu item.
 * 
 * @author Andreas Wenger
 */
public class LanguageChangeListener
  implements ActionListener
{
  
  private String langID;
  
  
  public LanguageChangeListener(String langID)
  {
    this.langID = langID;
  }
  

  public void actionPerformed(ActionEvent arg0)
  {
    LanguageChangeCommand cmd = new LanguageChangeCommand(langID);
    ScoreDocument doc = App.getInstance().getScoreDocument();
    doc.getCommandPerformer().execute(cmd);
  }

}
