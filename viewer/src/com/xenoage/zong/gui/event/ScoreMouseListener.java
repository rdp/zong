package com.xenoage.zong.gui.event;

import java.util.EventListener;


/**
 * The listener interface for receiving mouse events
 * on a score.
 * 
 * @author Andreas Wenger
 */
public interface ScoreMouseListener
  extends EventListener
{
  
  
  /**
   * Invoked when the mouse button has been clicked
   * (pressed and released) on a score document.
   * Returns true, when the event has been consumed.
   */
  public boolean mouseClicked(ScoreMouseEvent e);
  
  
  /**
   * Invoked when a mouse button has been pressed on a
   * score document.
   * Returns true, when the event has been consumed.
   */
  public boolean mousePressed(ScoreMouseEvent e);
  
  
  /**
   * Invoked when a mouse button has been released on a
   * score document.
   * Returns true, when the event has been consumed.
   */
  public boolean mouseReleased(ScoreMouseEvent e);
  
  
  /**
   * Invoked when the mouse cursor has been moved onto a
   * score document but no buttons have been pushed.
   * Returns true, when the event has been consumed.
   */
  public boolean mouseMoved(ScoreMouseEvent e);
  
  
  /**
   * Invoked when a mouse button is pressed on a
   * score document and then dragged.
   * Returns true, when the event has been consumed.
   */
  public boolean mouseDragged(ScoreMouseEvent e);
  

}
