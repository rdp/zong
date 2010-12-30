package com.xenoage.zong.renderer.screen;

import static org.junit.Assert.*;

import com.xenoage.util.Units;

import java.awt.*;

import org.junit.Test;


/**
 * Test cases for a ScreenLine.
 *
 * @author Andreas Wenger
 */
public class ScreenLineTest
{

  @Test public void testScreenLine1Pixel()
  {
    //1mm -> 1px, line: 1 mm, opaque. Should result in 1 px, opaque.
    float scaling = Units.pxToMm(1, 1);
    Color color = Color.black;
    ScreenLine sl = new ScreenLine(1, color, scaling);
    assertEquals(1, sl.getWidthPx());
    assertEquals(255, sl.getColor().getAlpha());
  }
  
  
  @Test public void testScreenLineHalfPixel()
  {
    //2mm -> 1px, line: 1 mm, opaque. Should result in 1 px with transparency.
    float scaling = Units.pxToMm(1, 2);
    Color color = Color.black;
    ScreenLine sl = new ScreenLine(1, color, scaling);
    assertEquals(1, sl.getWidthPx());
    int alpha = sl.getColor().getAlpha();
    assertTrue(Math.abs(alpha - 128) < 5);
  }
  
  
  @Test public void testScreenLine3Pixel()
  {
    //1mm -> 1px, line: 3 mm, opaque. Should result in 3 px, opaque.
    float scaling = Units.pxToMm(1, 1);
    Color color = Color.black;
    ScreenLine sl = new ScreenLine(3, color, scaling);
    assertEquals(3, sl.getWidthPx());
    assertEquals(255, sl.getColor().getAlpha());
  }
  
}
