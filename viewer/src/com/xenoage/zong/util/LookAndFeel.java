package com.xenoage.zong.util;

import javax.swing.UIManager;


/**
 * This class provides some look&feel functions.
 *
 * @author Andreas Wenger
 */
public class LookAndFeel
{
  
  /**
   * Activates the system look&feel.
   */
  public static void activateSystemLookAndFeel()
  {
    try
    {
      UIManager.setLookAndFeel(
        UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e)
    {
      //not catastrophal if not available.
      //use default look&feel then.
    }
  }
  
  
  private LookAndFeel()
  {
  }

}
