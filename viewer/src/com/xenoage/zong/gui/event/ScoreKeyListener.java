package com.xenoage.zong.gui.event;

import java.util.EventListener;


/**
 * The listener interface for receiving key events
 * on a score.
 * 
 * @author Andreas Wenger
 */
public interface ScoreKeyListener
  extends EventListener
{
  
  
  /**
   * Invoked when a key has been pressed.
   * Returns true, when the event has been consumed.
   */
  public boolean keyPressed(ScoreKeyEvent e);
  
  
  /**
   * Invoked when a key has been released.
   * Returns true, when the event has been consumed.
   */
  public boolean keyReleased(ScoreKeyEvent e);
  
  
  /**
   * Invoked when a key has been typed.
   * Returns true, when the event has been consumed.
   */
  public boolean keyTyped(ScoreKeyEvent e);
  

}
