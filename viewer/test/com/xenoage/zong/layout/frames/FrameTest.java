package com.xenoage.zong.layout.frames;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.Test;

import com.xenoage.util.Delta;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.layout.frames.background.ColorBackground;


/**
 * Test cases for a Frame.
 *
 * @author Andreas Wenger
 */
public class FrameTest
{
  
  
  /**
   * Tests the computation of the absolute position.
   */
  @Test public void getAbsolutePosition()
  {
    double u = Math.PI / 180d;
    GroupFrame frame = createFrameWithChildren();
    //frame
    Point2f p = frame.getAbsolutePosition();
    assertEquals(50, p.x, Delta.DELTA_FLOAT);
    assertEquals(75, p.y, Delta.DELTA_FLOAT);
    //child frame
    GroupFrame child = (GroupFrame) frame.getChildren().get(0);
    p = child.getAbsolutePosition();
    float r = 30;
    double childX = 50 + 20 * Math.cos(r * u) + 10 * Math.sin(r * u);
    double childY = 75 - 20 * Math.sin(r * u) + 10 * Math.cos(r * u);
    assertEquals(childX, p.x, Delta.DELTA_FLOAT_2);
    assertEquals(childY, p.y, Delta.DELTA_FLOAT_2);
    //child frame of child frame
    Frame child2 = child.getChildren().get(0);
    p = child2.getAbsolutePosition();
    r += 30;
    assertEquals(childX + -15 * Math.cos(r * u) + -10 * Math.sin(r * u), p.x, Delta.DELTA_FLOAT_2);
    assertEquals(childY - -15 * Math.sin(r * u) + -10 * Math.cos(r * u), p.y, Delta.DELTA_FLOAT_2);
  }
  
  
  public static GroupFrame createFrameWithChildren()
  {
    //frame
    GroupFrame frame = new GroupFrame(new Point2f(50, 75), new Size2f(80, 50));
    frame.setRelativeRotation(30);
    frame.setBackground(new ColorBackground(Color.RED));
    //child frame
    GroupFrame child = new GroupFrame(new Point2f(20, 10), new Size2f(30, 20));
    child.setBackground(new ColorBackground(Color.green));
    child.setRelativeRotation(30);
    frame.addChildFrame(child);
    //child frame of child frame
    ImageFrame child2 = new ImageFrame(new Point2f(-15, -10), new Size2f(5, 5), "data/test/images/flag6.png");
    child2.setBackground(new ColorBackground(Color.blue));
    child2.setRelativeRotation(45);
    child.addChildFrame(child2);
    return frame;
  }

}