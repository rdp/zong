package com.xenoage.zong.musiclayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Rectangle2f;
import com.xenoage.zong.musiclayout.ScoreFrameLayout;
import com.xenoage.zong.musiclayout.continued.ContinuedElement;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.zong.musiclayout.stampings.Stamping;
import com.xenoage.zong.musiclayout.stampings.StampingMock;


/**
 * Test cases for a ScoreFrameLayout.
 *
 * @author Andreas Wenger
 */
public class ScoreFrameLayoutTest
{
  

  @Test public void getLayoutElementAt()
  {
    
    /* 0    5   10   15    20    | 0
     *                             
     *       ***********           2
     *  +----*   [1]   *     
     *  |    *         *~~~~~~~    4
     *  |[2] ***********      |
     *  +--------+       [3]  |    6
     *     |                  |
     *     ~~~~~~~~~~~~~~~~~~~~    8
     */
  	Stamping[] stampings = new Stamping[3];
    
    StampingMock s1 = new StampingMock(3);
    s1.addBoundingShape(new Rectangle2f(6, 2, 10, 3));
    stampings[0] = s1;
    
    StampingMock s2 = new StampingMock(2);
    s2.addBoundingShape(new Rectangle2f(1, 3, 9, 3));
    stampings[1] = s2;
    
    StampingMock s3 = new StampingMock(1);
    s3.addBoundingShape(new Rectangle2f(4, 4, 19, 4));
    stampings[2] = s3;
    
    ScoreFrameLayout layout = new ScoreFrameLayout(null,
    	new LinkedList<StaffStamping>(), Arrays.asList(stampings),
    	new LinkedList<ContinuedElement>());
    
    //no hit (but empty space)
    assertTrue(isNot(layout.getStampingAt(new Point2f(0, 0)), s1, s2, s3));
    assertTrue(isNot(layout.getStampingAt(new Point2f(3, 7)), s1, s2, s3));
    assertTrue(isNot(layout.getStampingAt(new Point2f(17, 3)), s1, s2, s3));
    
    //single hit
    assertEquals(s1, layout.getStampingAt(new Point2f(10, 2)));
    assertEquals(s2, layout.getStampingAt(new Point2f(3, 5)));
    assertEquals(s3, layout.getStampingAt(new Point2f(22, 8)));
    
    //intersection hit
    assertEquals(s1, layout.getStampingAt(new Point2f(15, 4)));
    assertEquals(s2, layout.getStampingAt(new Point2f(5, 5)));
    assertEquals(s1, layout.getStampingAt(new Point2f(8, 4)));
    
  }
  
  
  private boolean isNot(Object object, Object this1, Object this2, Object this3)
  {
    return (object != this1) && (object != this2) && (object != this3);
  }
  
}
