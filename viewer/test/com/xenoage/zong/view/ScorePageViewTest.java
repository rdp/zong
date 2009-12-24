package com.xenoage.zong.view;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.xenoage.util.Delta;
import com.xenoage.util.language.Lang;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2f;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.data.format.PageFormat;
import com.xenoage.zong.data.format.PageMargins;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.gui.controller.panels.ScorePanelControllerMock;
import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.layout.LayoutPosition;
import com.xenoage.util.Units;
import com.xenoage.zong.view.ScorePageView.PageDisplayAlignment;


/**
 * Test cases for a {@link ScorePageView}.
 *
 * @author Andreas Wenger
 */
public class ScorePageViewTest
{
  
  private ScorePageView view;
  private ScorePanelControllerMock ctrl;
  
  
  @Before public void setUp()
  {
    Layout layout = new ScoreDocument().getDefaultLayout();
    //3 pages:
    // ----          --------
    // |1 | -2------ |3     |
    // |  | -------- |      |
    // ----          --------
    layout.addPage(new PageFormat(new Size2f(100, 200), new PageMargins(0, 0, 0, 0)));
    layout.addPage(new PageFormat(new Size2f(200, 100), new PageMargins(0, 0, 0, 0)));
    layout.addPage(new PageFormat(new Size2f(200, 200), new PageMargins(0, 0, 0, 0)));
    //create controller
    ctrl = new ScorePanelControllerMock(new Size2i(800, 600));
    //create view
    Lang.loadLanguage("en"); //TIDY
    view = new ScorePageView(null, layout, ctrl);
    view.setPageDisplayAlignment(PageDisplayAlignment.Horizontal);
    ctrl.setView(view);
  }
  
  
  @Test public void getPageDisplayOffset()
  {
    float distance = view.getPageDisplayDistance();
    float d = Delta.DELTA_FLOAT;
    //page 1
    Point2f p = view.getPageDisplayOffset(0);
    assertEquals(-100 / 2, p.x, d);
    assertEquals(-200 / 2, p.y, d);
    //page 2
    p = view.getPageDisplayOffset(1);
    assertEquals(100 / 2 + distance, p.x, d);
    assertEquals(-100 / 2, p.y, d);
    //page 3
    p = view.getPageDisplayOffset(2);
    assertEquals(100 / 2 + distance + 200 + distance, p.x, d);
    assertEquals(-200 / 2, p.y, d);
  }
  
  
  @Test public void getPageIndex()
  {
    float distance = view.getPageDisplayDistance();
    //point -100/0 belongs to page 0
    assertEquals(0, view.getNearestPageIndex(new Point2f(-100, 0)));
    //point 50/0 still belongs to page 0
    assertEquals(0, view.getNearestPageIndex(new Point2f(50, 0)));
    //point 50+distance+10/0 belongs to page 1
    assertEquals(1, view.getNearestPageIndex(new Point2f(50 + distance + 10, 0)));
    //point 1000/0 belongs to page 2
    assertEquals(2, view.getNearestPageIndex(new Point2f(1000, 0)));
  }
  
  
  @Test public void computeLayoutPosition()
  {
    resetZoomAndScroll();
    //default zoom and scroll: point 400/300 must
    //have center position of the page (50/100)
    Point2f pos = view.computeLayoutPosition(new Point2i(400, 300)).getPosition();
    assertEquals(pos.x, 50, Delta.DELTA_FLOAT);
    assertEquals(pos.y, 100, Delta.DELTA_FLOAT);
    //set zoom to 200%, must still be 50/100
    float zoom = 2;
    view.setZoom(zoom);
    pos = view.computeLayoutPosition(new Point2i(400, 300)).getPosition();
    assertEquals(pos.x, 50, Delta.DELTA_FLOAT);
    assertEquals(pos.y, 100, Delta.DELTA_FLOAT);
    //scroll to -5/10
    //screen point 400/300 must be 45/110
    view.setScrollPosition(new Point2f(-5, 10));
    pos = view.computeLayoutPosition(new Point2i(400, 300)).getPosition();
    assertEquals(pos.x, 45, Delta.DELTA_FLOAT);
    assertEquals(pos.y, 110, Delta.DELTA_FLOAT);
    //screen point 0/0 must be
    //50+(-5-400*pxToMm/zoom);100+(10-300*pxToMm/zoom)
    //compute relative to page 0, since coordinates are outside
    pos = view.computeLayoutPosition(new Point2i(0, 0), 0).getPosition();
    assertEquals(pos.x, 50 + -5 - Units.pxToMm(400, zoom), Delta.DELTA_FLOAT);
    assertEquals(pos.y, 100 + 10 - Units.pxToMm(300, zoom), Delta.DELTA_FLOAT);
    //when not computed relative to a given page, it must return null
    assertNull(view.computeLayoutPosition(new Point2i(0, 0)));
    //reset zoom/scroll, then screen point 800/300
    //must be on page 1
    resetZoomAndScroll();
    int pageIndex = view.computeLayoutPosition(new Point2i(800, 300)).getPageIndex();
    assertEquals(1, pageIndex);
  }
  

  @Test public void computeScreenPosition()
  {
    resetZoomAndScroll();
    //center point of the page must have screen coordinates 400/300
    Point2i pos = view.computeScreenPosition(new LayoutPosition(0, 50, 100));
    assertEquals(pos.x, 400);
    assertEquals(pos.y, 300);
    //set zoom to 200%, must still be 400/300
    float zoom = 2;
    view.setZoom(zoom);
    pos = view.computeScreenPosition(new LayoutPosition(0, 50, 100));
    assertEquals(pos.x, 400);
    assertEquals(pos.y, 300);
    //scroll to -5/10
    //page point 45/110 must be 400/300
    view.setScrollPosition(new Point2f(-5, 10));
    pos = view.computeScreenPosition(new LayoutPosition(0, 45, 110));
    assertEquals(pos.x, 400);
    assertEquals(pos.y, 300);
    //page center point must be
    //400-(-5*MmToPx*zoom); 300-(10*MmToPx*zoom)
    pos = view.computeScreenPosition(new LayoutPosition(0, 50, 100));
    assertEquals(pos.x, 400 - Units.mmToPx(-5, zoom));
    assertEquals(pos.y, 300 - Units.mmToPx(10, zoom)); 
  }
  
  
  private void resetZoomAndScroll()
  {
    view.setScrollPosition(new Point2f(0, 0));
    view.setZoom(1f);
  }
  

}
