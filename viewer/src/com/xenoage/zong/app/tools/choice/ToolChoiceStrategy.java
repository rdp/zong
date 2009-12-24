package com.xenoage.zong.app.tools.choice;

import com.xenoage.zong.app.tools.Tool;
import com.xenoage.zong.gui.event.ScoreMouseEvent;
import com.xenoage.zong.view.ScoreView;


/**
 * This is the interface for all strategies that
 * decide which {@link Tool} to select when the user
 * generates a mouse event on a {@link ScoreView}.
 * 
 * @author Andreas Wenger
 */
public interface ToolChoiceStrategy
{
	
	
	/**
   * Call this method when the mouse cursor has been pressed over
   * a {@link ScoreView} but no tool is active at this moment
   * or if it did not consume this event.
   * 
   * @return the {@link Tool} this strategy recommends to activate.
   * It it not activated by this method!
   */
  public Tool mousePressed(ScoreMouseEvent e);
	
}
