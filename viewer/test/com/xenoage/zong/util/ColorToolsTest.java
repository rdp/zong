package com.xenoage.zong.util;

import static com.xenoage.zong.util.ColorTools.toHTMLColor;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;


/**
 * Test cases for a {@link ColorTools} class.
 *
 * @author Andreas Wenger
 */
public class ColorToolsTest
{

  @Test public void toHTMLColorTest()
  {
    assertEquals("#000000", toHTMLColor(Color.BLACK));
    assertEquals("#ffffff", toHTMLColor(Color.WHITE));
    assertEquals("#ffffff", toHTMLColor(new Color(255, 255, 255, 200)));
    assertEquals("#ff0080", toHTMLColor(new Color(255, 0, 128, 100)));
    assertEquals("#010203", toHTMLColor(new Color(1, 2, 3, 100)));
  }
  
}