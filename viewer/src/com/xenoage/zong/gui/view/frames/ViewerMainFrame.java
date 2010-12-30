package com.xenoage.zong.gui.view.frames;

import com.xenoage.zong.gui.controller.frames.ViewerMainFrameController;


/**
 * The main frame of the Zong! Viewer.
 *
 * @author Andreas Wenger
 */
public class ViewerMainFrame
  extends MainFrame
{
  
  
  /**
   * Creates the main frame.
   */
  public ViewerMainFrame(ViewerMainFrameController controller)
  {
    super(controller);
  }
  
  
  /**
   * Gets the controller of this main frame.
   */
  @Override public ViewerMainFrameController getController()
  {
    return (ViewerMainFrameController) controller;
  }
  

}
