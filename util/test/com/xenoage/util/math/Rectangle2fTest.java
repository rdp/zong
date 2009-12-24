package com.xenoage.util.math;

import static org.junit.Assert.*;

import com.xenoage.util.Delta;
import com.xenoage.util.math.Rectangle2f;

import org.junit.Test;


/**
 * Test cases for the Rectangle2f class.
 *
 * @author Andreas Wenger
 */
public class Rectangle2fTest
{
  
  @Test public void extend()
  {
    //test 1
    Rectangle2f r1 = new Rectangle2f(2, 4, 6, 8);
    Rectangle2f r2 = new Rectangle2f(1, -4, 12, 20);
    r1 = r1.extend(r2);
    assertEquals(r1.position.x, 1, Delta.DELTA_FLOAT);
    assertEquals(r1.position.y, -4, Delta.DELTA_FLOAT);
    assertEquals(r1.size.width, 12, Delta.DELTA_FLOAT);
    assertEquals(r1.size.height, 20, Delta.DELTA_FLOAT);
    //test 2
    r1 = new Rectangle2f(2, 4, 6, 8);
    r2 = new Rectangle2f(3, 8, 4, 1);
    r1 = r1.extend(r2);
    assertEquals(r1.position.x, 2, Delta.DELTA_FLOAT);
    assertEquals(r1.position.y, 4, Delta.DELTA_FLOAT);
    assertEquals(r1.size.width, 6, Delta.DELTA_FLOAT);
    assertEquals(r1.size.height, 8, Delta.DELTA_FLOAT);
    //test 3
    r1 = new Rectangle2f(2, 2, 4, 4);
    r2 = new Rectangle2f(0, 0, 4, 4);
    r1 = r1.extend(r2);
    assertEquals(r1.position.x, 0, Delta.DELTA_FLOAT);
    assertEquals(r1.position.y, 0, Delta.DELTA_FLOAT);
    assertEquals(r1.size.width, 6, Delta.DELTA_FLOAT);
    assertEquals(r1.size.height, 6, Delta.DELTA_FLOAT);
  }

}
