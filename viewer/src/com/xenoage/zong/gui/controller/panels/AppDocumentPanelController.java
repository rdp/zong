package com.xenoage.zong.gui.controller.panels;

import java.awt.Component;

import com.xenoage.util.math.Size2i;
import com.xenoage.zong.view.View;


/**
 * Interface for a panel that displays an AppDocument.
 *
 * @author Andreas Wenger
 */
public interface AppDocumentPanelController
{
  
  /**
   * Gets the current view of this panel.
   */
  public View getView();
  
  
  /**
   * Gets the panel.
   */
  public Component getPanel();
  
  
  /**
   * Gets the size of the panel in px.
   */
  public Size2i getSize();
  
  
  /**
   * Repaints the panel.
   */
  public void repaint();
  

}
