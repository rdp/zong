package com.xenoage.zong.commands;

import javax.swing.Icon;

import com.xenoage.zong.Zong;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.VocByString;
import com.xenoage.zong.util.exceptions.PropertyAlreadySetException;


/**
 * Base class for all commands in Xenoage Score, e.g.
 * open a document, print a document, insert or
 * delete notes, edit text, and so on
 * (command design pattern).
 * 
 * Commands can be executed or undone by calling
 * the <code>execute</code> and <code>undo</code> methods
 * of the class <code>CommandPerformer</code>. If called
 * directly, they are not added to the command history!
 * 
 * Also an icon can be assigned to a command.
 *
 * @author Andreas Wenger
 */
public abstract class Command
{
  
  
  /**
   * Execute or redo the command.
   */
  public abstract void execute()
    throws PropertyAlreadySetException;
  
  
  /**
   * Returns true, if it is possible to undo
   * this command.
   * Default: false.
   */
  public boolean isUndoable()
  {
    return false;
  }
  
  
  /**
   * If possible, undo this command.
   */
  public void undo()
    throws PropertyAlreadySetException
  {
  }
  
  
  /**
   * Returns true, if this command should be put
   * into the command history (for undo).
   * Do not include commands like save or print,
   * because they are not undoable and break the chain
   * of undoable commands.
   * Default: false
   */
  public boolean isInHistory()
  {
    return false;
  }
  
  
  /**
   * Gets the name of the command, e.g.
   * "Print document". Start with an uppercase character.
   * 
   * In the default implementation the vocabulary with id
   * <code>"command:"</code> + class name (relative to
   * this package) is used, e.g.
   * <code>"command:document.PrintCommand"</code>.
   */
  public String getName()
  {
  	return getName(this.getClass());
  }
  
  
  /**
   * Gets the localized name of the given command class, e.g.
   * "Print document".
   * This is an alternative for <code>getName()</code>,
   * which requires an instance of the command.
   */
  @SuppressWarnings("unchecked")
  public static String getName(Class commandClass)
  {
    String className = commandClass.getName();
    String packageBase = Zong.PACKAGE + ".commands.";
    if (className.startsWith(packageBase))
      className = className.substring(packageBase.length());
    return Lang.get(new VocByString("Command", className));
  }
  
  
  /**
   * Gets the icon assigned to this command, or null.
   */
  public Icon getIcon()
  {
    return null;
  }
  
  
}
