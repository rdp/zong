package com.xenoage.zong.renderer.screen;

import static org.junit.Assert.*;

import com.xenoage.util.Delta;
import com.xenoage.util.Units;

import org.junit.Test;


/**
 * Test cases for a ScreenStaff.
 *
 * @author Andreas Wenger
 */
public class ScreenStaffTest
{

  @Test public void testScreenStaff8Pixel1()
  {
    //1mm -> 1px, 5 lines, interline: 2 mm. Displayed staff must be 8px high.
    float scaling = Units.pxToMm(1, 1);
    ScreenStaff ss = new ScreenStaff(5, 2, 0.1f, scaling);
    assertEquals(8, ss.getHeightPx());
    assertEquals(0, ss.getYOffsetPx());
    assertEquals(1, ss.getHeightScaling(), Delta.DELTA_FLOAT);
    assertEquals(2, ss.getInterlineSpacePx(), Delta.DELTA_FLOAT);
  }
  
  
  @Test public void testScreenStaff16Pixel()
  {
    //1mm -> 2px, 5 lines, interline: 1.8 mm. Displayed staff must be 16px high.
    float scaling = Units.pxToMm(2, 1);
    ScreenStaff ss = new ScreenStaff(5, 1.8f, 0.1f, scaling);
    assertEquals(16, ss.getHeightPx());
    assertEquals(-1, ss.getYOffsetPx());
    assertEquals(16 / (2 * 4 * 1.8f), ss.getHeightScaling(), Delta.DELTA_FLOAT);
    assertEquals(4, ss.getInterlineSpacePx(), Delta.DELTA_FLOAT);
  }
  
  
  @Test public void testScreenStaff12Pixel()
  {
    //1mm -> 1px, 5 lines, interline: 3.4 mm. Displayed staff must be 12px high.
    float scaling = Units.pxToMm(1, 1);
    ScreenStaff ss = new ScreenStaff(5, 3.4f, 0.1f, scaling);
    assertEquals(12, ss.getHeightPx());
    assertEquals(1, ss.getYOffsetPx());
    assertEquals(12 / (4 * 3.4f), ss.getHeightScaling(), Delta.DELTA_FLOAT);
    assertEquals(3, ss.getInterlineSpacePx(), Delta.DELTA_FLOAT);
  }
  
  
  @Test public void testScreenStaff13Pixel()
  {
    //1mm -> 1px, 10 lines, interline: 1.4 mm. Displayed staff must be 13px high.
    float scaling = Units.pxToMm(1, 1);
    ScreenStaff ss = new ScreenStaff(10, 1.4f, 0.1f, scaling);
    assertEquals(13, ss.getHeightPx());
    assertEquals(0, ss.getYOffsetPx());
    assertEquals(13 / (9 * 1.4f), ss.getHeightScaling(), Delta.DELTA_FLOAT);
    assertTrue(ss.getInterlineSpacePx() < 2); //simplified staff
  }
  
  
  @Test public void testScreenStaff2Pixel()
  {
    //1mm -> 1px, 5 lines, interline: 0.55 mm. Displayed staff must be 2px high.
    float scaling = Units.pxToMm(1, 1);
    ScreenStaff ss = new ScreenStaff(5, 0.55f, 0.1f, scaling);
    assertEquals(2, ss.getHeightPx());
    assertEquals(0, ss.getYOffsetPx());
    assertEquals(2 / (4 * 0.55f), ss.getHeightScaling(), Delta.DELTA_FLOAT);
    assertTrue(ss.getInterlineSpacePx() < 2); //simplified staff
  }
  
}