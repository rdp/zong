package com.xenoage.zong.gui.controller.frames;

import com.xenoage.zong.app.ViewerApplication;
import com.xenoage.zong.gui.controller.menues.MainMenuBarController;
import com.xenoage.zong.gui.controller.panels.ScorePanelController;
import com.xenoage.zong.gui.view.frames.ViewerMainFrame;


/**
 * The controller for the main frame of the Zong! Viewer.
 * 
 * Use this class instead of using {@link ViewerMainFrame} directly.
 *
 * @author Andreas Wenger
 */
public class ViewerMainFrameController
  extends MainFrameController
{
	
	
  /**
   * Creates a controller for the main frame of the Zong! Viewer.
   * @param app                   the main class of the application
   * @param scorePanelController  the controller of the score panel
   */
  public ViewerMainFrameController(ViewerApplication app, ScorePanelController scorePanelController)
  {
    super(app);
    initMainFrame(new ViewerMainFrame(this), scorePanelController);
  }
  
  
  /**
   * Gets the main frame.
   */
  @Override public ViewerMainFrame getMainFrame()
  {
    return (ViewerMainFrame) frame;
  }
  

}
