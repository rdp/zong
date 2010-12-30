package com.xenoage.zong.renderer.screen.page;

import static org.junit.Assert.*;

import com.xenoage.util.math.Rectangle2i;
import com.xenoage.util.math.Size2i;

import org.junit.Test;


/**
 * Test cases for a GLPageRenderer.
 *
 * @author Andreas Wenger
 */
public class GLPageRendererTest
{
  
  @Test public void testIsRectVisible()
  {
    GLPageRenderer renderer = GLPageRenderer.getInstance();
    Size2i windowSize = new Size2i(800, 600);
    //center
    assertTrue(renderer.isRectVisible(windowSize,
      new Rectangle2i(100, 100, 100, 100)));
    
    //visible
    //top left
    assertTrue(renderer.isRectVisible(windowSize,
      new Rectangle2i(-99, -99, 100, 100)));
    //top
    assertTrue(renderer.isRectVisible(windowSize,
      new Rectangle2i(300, -99, 100, 100)));
    //top right
    assertTrue(renderer.isRectVisible(windowSize,
      new Rectangle2i(799, -99, 100, 100)));
    //right
    assertTrue(renderer.isRectVisible(windowSize,
      new Rectangle2i(799, 300, 100, 100)));
    //bottom right
    assertTrue(renderer.isRectVisible(windowSize,
      new Rectangle2i(799, 599, 100, 100)));
    //bottom
    assertTrue(renderer.isRectVisible(windowSize,
      new Rectangle2i(300, 599, 100, 100)));
    //bottom left
    assertTrue(renderer.isRectVisible(windowSize,
      new Rectangle2i(-99, 599, 100, 100)));
    //left
    assertTrue(renderer.isRectVisible(windowSize,
      new Rectangle2i(-99, 300, 100, 100)));
    
    //not visible
    //top left
    assertFalse(renderer.isRectVisible(windowSize,
      new Rectangle2i(-101, -101, 100, 100)));
    //top
    assertFalse(renderer.isRectVisible(windowSize,
      new Rectangle2i(300, -101, 100, 100)));
    //top right
    assertFalse(renderer.isRectVisible(windowSize,
      new Rectangle2i(801, -101, 100, 100)));
    //right
    assertFalse(renderer.isRectVisible(windowSize,
      new Rectangle2i(801, 300, 100, 100)));
    //bottom right
    assertFalse(renderer.isRectVisible(windowSize,
      new Rectangle2i(801, 601, 100, 100)));
    //bottom
    assertFalse(renderer.isRectVisible(windowSize,
      new Rectangle2i(300, 601, 100, 100)));
    //bottom left
    assertFalse(renderer.isRectVisible(windowSize,
      new Rectangle2i(-101, 601, 100, 100)));
    //left
    assertFalse(renderer.isRectVisible(windowSize,
      new Rectangle2i(-101, 300, 100, 100)));
  }

}
