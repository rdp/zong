package com.xenoage.zong.data.format;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.math.Size2f;
import com.xenoage.zong.data.format.LayoutFormat.Side;


/**
 * Test cases for a PageLayoutFormat.
 *
 * @author Andreas Wenger
 */
public class PageLayoutFormatTest
{

  @Test public void createScoreFormat()
  {
    //must have default page format
    LayoutFormat format = new LayoutFormat();
    assertNotNull(format.getPageFormat(Side.Left));
    assertNotNull(format.getPageFormat(Side.Right));
  }

  
  @Test public void setPageFormat()
  {
    LayoutFormat format = new LayoutFormat();
    PageFormat pf1 = new PageFormat(new Size2f(100, 200), new PageMargins(30, 30, 30, 30));
    PageFormat pf2 = new PageFormat(new Size2f(100, 100), new PageMargins(10, 10, 10, 10));
    PageFormat pf3 = new PageFormat(new Size2f(50, 100), new PageMargins(15, 15, 15, 15));
    //set equal format for left and right page
    format.setPageFormat(Side.Both, pf1);
    assertEquals(format.getPageFormat(Side.Left), pf1);
    assertEquals(format.getPageFormat(Side.Right), pf1);
    //change left page, right must still be the old one
    format.setPageFormat(Side.Left, pf2);
    assertEquals(format.getPageFormat(Side.Left), pf2);
    assertEquals(format.getPageFormat(Side.Right), pf1);
    //change right page, left one must be the old one
    format.setPageFormat(Side.Right, pf3);
    assertEquals(format.getPageFormat(Side.Left), pf2);
    assertEquals(format.getPageFormat(Side.Right), pf3);
  }
  
  
  @Test(expected = IllegalArgumentException.class)
  public void setPageFormatError()
  {
    LayoutFormat format = new LayoutFormat();
    format.setPageFormat(Side.Both, null);
  }
  
  
  @Test(expected = IllegalArgumentException.class)
  public void getPageFormatError()
  {
    LayoutFormat format = new LayoutFormat();
    format.getPageFormat(Side.Both);
  }
  
}
