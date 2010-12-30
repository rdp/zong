package com.xenoage.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.MathTools;
import com.xenoage.util.math.Point2f;


/**
 * Test cases for MathTools.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class MathToolsTest
{

  @Test public void modMin()
  {
    assertEquals(2, MathTools.modMin(5, 3, 0));
    assertEquals(-8, MathTools.modMin(1, 3, -10));
    assertEquals(2, MathTools.modMin(-1, 3, 0));
    assertEquals(5, MathTools.modMin(5, 3, 4));
    assertEquals(20, MathTools.modMin(5, 3, 20));
    assertEquals(30, MathTools.modMin(8, 1, 30));
    assertEquals(0, MathTools.modMin(4, 4, 0));
    assertEquals(-6, MathTools.modMin(-2, 4, -6));
  }
  
  
  /**
   * Tests the rotate-method.
   */
  @Test public void rotate()
  {
    Point2f p = new Point2f(10, 5);
    Point2f res;
    //angle 0
    res = MathTools.rotate(p, 0);
    assertEquals(10, res.x, Delta.DELTA_FLOAT_2);
    assertEquals(5, res.y, Delta.DELTA_FLOAT_2);
    //angle 90
    res = MathTools.rotate(p, 90);
    assertEquals(5, res.x, Delta.DELTA_FLOAT_2);
    assertEquals(-10, res.y, Delta.DELTA_FLOAT_2);
    //angle 122
    res = MathTools.rotate(p, 122);
    double cos = Math.cos(122 * Math.PI / 180f);
    double sin = Math.sin(122 * Math.PI / 180f);
    assertEquals(10 * cos + 5 * sin, res.x, Delta.DELTA_FLOAT_2);
    assertEquals(10 * -sin + 5 * cos, res.y, Delta.DELTA_FLOAT_2);
  }
  
  
  @Test public void lowestPrimeNumberTest()
  {
	  assertEquals(2,MathTools.lowestPrimeNumber(8));
	  assertEquals(3,MathTools.lowestPrimeNumber(21));
	  assertEquals(5,MathTools.lowestPrimeNumber(65));
	  assertEquals(11,MathTools.lowestPrimeNumber(209));
  }
  
  
  @Test public void interpolateLinearTest()
  {
  	assertEquals(5, MathTools.interpolateLinear(5, 10, 100, 200, 100), Delta.DELTA_FLOAT);
  	assertEquals(10, MathTools.interpolateLinear(5, 10, 100, 200, 200), Delta.DELTA_FLOAT);
  	assertEquals(6, MathTools.interpolateLinear(5, 10, 100, 200, 120), Delta.DELTA_FLOAT);
  }
  
  
}
