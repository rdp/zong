package com.xenoage.zong.data.format;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.math.Size2f;


/**
 * Test cases for a PageFormat.
 *
 * @author Andreas Wenger
 */
public class PageFormatTest
{

  @Test public void createPageFormat()
  {
    //create a default page format. width and height must be
    //greater than 0, margins must be not null.
    PageFormat format = new PageFormat();
    assertTrue(format.getSize().width > 0);
    assertTrue(format.getSize().height > 0);
    PageMargins margins = format.getMargins();
    assertNotNull(margins);
    //create a custom page format. check values.
    format = new PageFormat(new Size2f(100, 200), new PageMargins(30, 31, 32, 33));
    assertTrue(format.getSize().width == 100);
    assertTrue(format.getSize().height == 200);
    margins = format.getMargins();
    assertNotNull(margins);
    assertTrue(margins.left == 30);
    assertTrue(margins.right == 31);
    assertTrue(margins.top == 32);
    assertTrue(margins.bottom == 33);
  }
  
}
