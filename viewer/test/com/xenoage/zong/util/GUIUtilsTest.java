package com.xenoage.zong.util;

import static org.junit.Assert.*;

import javax.swing.*;

import org.junit.Test;


/**
 * Test case for GUIUtils.
 *
 * @author Andreas Wenger
 */
public class GUIUtilsTest
{
  
  @Test public void isComponentInFrame()
  {
    JFrame frame = new JFrame();
    JComponent comp1 = new JLabel();
    JComponent comp2 = new JButton();
    JComponent comp3 = new JLabel();
    //add two components and find them
    frame.add(comp1);
    frame.add(comp2);
    assertTrue(GUIUtils.isComponentInFrame(frame, comp1));
    assertTrue(GUIUtils.isComponentInFrame(frame, comp2));
    //do not find the third component
    assertFalse(GUIUtils.isComponentInFrame(frame, comp3));
    //do not find a null component
    assertFalse(GUIUtils.isComponentInFrame(frame, null));
  }

}
