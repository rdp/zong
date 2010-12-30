package com.xenoage.zong.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.awt.Color;

import org.junit.Test;

import com.xenoage.util.Delta;
import com.xenoage.util.MathTools;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.PageFormat;
import com.xenoage.zong.core.format.PageMargins;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.FramePosition;
import com.xenoage.zong.layout.frames.GroupFrame;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.layout.frames.background.ColorBackground;


/**
 * Test cases for the Page class.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class PageTest
{
  
  private static Frame frm1, frm2a, frm2b, frm3;
  private static GroupFrame frm2;
  
  
  
  /**
   * Tests the computeFramePosition-method
   * without rotation.
   */
	@Test public void computeFramePosition1()
  {
    Layout layout = new ScoreDocument().getDefaultLayout();
    Page page = createPageWithUnrotatedFrames(layout);
    
    //Test 1
		Point2f p = new Point2f(113,122);
		FramePosition fp = page.computeFramePosition(p);
    assertNotNull(fp);
    assertEquals(frm2, fp.getFrame());
		assertEquals(23, fp.getPosition().x, Delta.DELTA_FLOAT);
		assertEquals(12, fp.getPosition().y, Delta.DELTA_FLOAT);
    
    //Test 2
    p = new Point2f(122,108);
    fp = page.computeFramePosition(p);
    assertNotNull(fp);
    assertEquals(frm2a, fp.getFrame());
    assertEquals(2, fp.getPosition().x, Delta.DELTA_FLOAT);
    assertEquals(-2, fp.getPosition().y, Delta.DELTA_FLOAT);
    
    //Test 3
    p = new Point2f(86,88);
    fp = page.computeFramePosition(p);
    assertNull(fp);
	}
  
  
  /**
   * Tests the computeFramePosition-method
   * with rotation.
   */
  @Test public void computeFramePosition2()
  {
    Layout layout = new ScoreDocument().getDefaultLayout();
    Page page = createPageWithRotatedFrames(layout);
    
    //Test 1
    Point2f p = new Point2f(85,88);
    FramePosition fp = page.computeFramePosition(p);
    assertNotNull(fp);
    assertEquals(frm2b, fp.getFrame());
    Point2f pExpected = MathTools.rotate(new Point2f(5, -2), -70);
    assertEquals(pExpected.x, fp.getPosition().x, Delta.DELTA_FLOAT_2);
    assertEquals(pExpected.y, fp.getPosition().y, Delta.DELTA_FLOAT_2);
    
    //Test 2
    p = new Point2f(86, 89);
    fp = page.computeFramePosition(p);
    assertNotNull(fp);
    assertEquals(frm2, fp.getFrame());
    pExpected = MathTools.rotate(new Point2f(-4, -21), -40);
    assertEquals(pExpected.x, fp.getPosition().x, Delta.DELTA_FLOAT_2);
    assertEquals(pExpected.y, fp.getPosition().y, Delta.DELTA_FLOAT_2);
    
    //Test 3
    p = new Point2f(85,86);
    fp = page.computeFramePosition(p);
    assertNull(fp);
  }
  
  
  /**
   * Creates a page with some unrotated frames for testing.
   */
  public static Page createPageWithUnrotatedFrames(Layout parentLayout)
  {
    PageFormat pf = new PageFormat(new Size2f(200, 200), new PageMargins(10,10,10,10));
    Page page = parentLayout.addPage(pf);
    
    //Frame 1
    Point2f position1 = new Point2f(120,120);
    Size2f size1 = new Size2f(60,80);
    frm1 = new ScoreFrame(position1, size1, createScore());
    frm1.setBackground(new ColorBackground(Color.blue));
    page.addFrame(frm1);

    //Frame 2
    Point2f position2 = new Point2f(90,110);
    Size2f size2 = new Size2f(60,40);
    frm2 = new GroupFrame(position2, size2);
    frm2.setBackground(new ColorBackground(Color.green));
    page.addFrame(frm2);
    
    //Childframe 2.a
    Point2f position2a = new Point2f(120,110);
    Size2f size2a = new Size2f(10,10);
    frm2a = new ScoreFrame(position2a, size2a, createScore());
    frm2a.setBackground(new ColorBackground(Color.yellow));
    frm2.getChildren().add(frm2a);
    
    //Childframe 2.b
    Point2f position2b = new Point2f(80,90);
    Size2f size2b = new Size2f(10,10);
    frm2b = new ScoreFrame(position2b, size2b, createScore());
    frm2b.setBackground(new ColorBackground(Color.lightGray));
    frm2.getChildren().add(frm2b);
    
    //Frame 3
    Point2f position3 = new Point2f(30,80);
    Size2f size3 = new Size2f(30,60);
    frm3 = new ScoreFrame(position3, size3, createScore());
    frm3.setBackground(new ColorBackground(Color.red));
    page.addFrame(frm3);
    
    return page;
  }
  
  
  /**
   * Creates a page with some rotated frames for testing.
   */
  public static Page createPageWithRotatedFrames(Layout parentPageLayout)
  {
    PageFormat pf = new PageFormat(new Size2f(200,200), new PageMargins(10,10,10,10));
    Page page = parentPageLayout.addPage(pf);
    
    //Frame 1
    Point2f position1 = new Point2f(120,120);
    Size2f size1 = new Size2f(60,80);
    frm1 = new ScoreFrame(position1, size1, createScore());
    frm1.setRelativeRotation(-30);
    frm1.setBackground(new ColorBackground(Color.blue));
    page.addFrame(frm1);

    //Frame 2
    Point2f position2 = new Point2f(90,110);
    Size2f size2 = new Size2f(60,40);
    frm2 = new GroupFrame(position2, size2);
    frm2.setRelativeRotation(40);
    frm2.setBackground(new ColorBackground(Color.green));
    page.addFrame(frm2);
    
    //Childframe 2.a
    Point2f position2a = new Point2f(120,110);
    Size2f size2a = new Size2f(10,10);
    frm2a = new ScoreFrame(position2a, size2a, createScore());
    frm2a.setRelativeRotation(0);
    frm2a.setBackground(new ColorBackground(Color.yellow));
    frm2.getChildren().add(frm2a);
    
    //Childframe 2.b
    Point2f position2b = new Point2f(80,90);
    Size2f size2b = new Size2f(10,10);
    frm2b = new ScoreFrame(position2b, size2b, createScore());
    frm2b.setRelativeRotation(70);
    frm2b.setBackground(new ColorBackground(Color.lightGray));
    frm2.getChildren().add(frm2b);
    
    //Frame 3
    Point2f position3 = new Point2f(30,80);
    Size2f size3 = new Size2f(30,60);
    frm3 = new ScoreFrame(position3, size3, createScore());
    frm3.setRelativeRotation(210);
    frm3.setBackground(new ColorBackground(Color.red));
    page.addFrame(frm3);
    
    //TEST
    Frame f = new ScoreFrame(new Point2f(86,89),new Size2f(1,1), createScore());
    f.setBackground(new ColorBackground(Color.black));
    //page.getFrames().add(f);
    
    return page;
  }
  
  
  private static Score createScore()
  {
    return Score.empty();
  }
  
  
}
