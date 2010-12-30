package com.xenoage.zong.commands;

import javax.swing.JOptionPane;

import com.xenoage.zong.commands.Command;


/**
 * Very simple command, that just shows a message.
 * 
 * It shows a given text as a message box.
 * 
 * @author Andreas Wenger
 */
public class MessageCommand
  extends Command
{
  
  private String msg;
  
  
  public MessageCommand(String msg)
  {
    this.msg = msg;
  }
  
  
  @Override public String getName()
  {
    return msg;
  }

  
  @Override public void execute()
  {
    JOptionPane.showMessageDialog(null, msg);
  }

}
