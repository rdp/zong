package com.xenoage.zong.util;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JFrame;


/**
 * This class contains some useful functions
 * for GUI testing.
 *
 * @author Andreas Wenger
 */
public class GUIUtils
{
  
  /**
   * Returns true, if the given component is
   * a direct child of the given frame.
   */
  public static boolean isComponentInFrame(JFrame frame, JComponent component)
  {
    for (Component c : frame.getContentPane().getComponents())
    {
      if (c == component)
        return true;
    }
    return false;
  }

}
