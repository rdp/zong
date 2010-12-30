package com.xenoage.zong.view;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.gui.controller.panels.ScorePanelController;
import com.xenoage.zong.layout.LayoutPosition;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.FrameHandle;
import com.xenoage.util.Units;


/**
 * Abstract base class for all views of score documents.
 * 
 * @author Andreas Wenger
 */
public abstract class ScoreView
  extends View
{
	
	protected ScrollTool scrollTool = null;
  
  
  /**
   * Gets the score document this view shows.
   */
  @Override public abstract ScoreDocument getDocument();
  
  
  /**
	 * Computes and returns the adjustment handle below the given coordinates in px,
	 * or null, if none was found.
	 */
	public abstract FrameHandle computeFrameHandleAt(Point2i pPx);
	
	
	/**
   * Returns the display position in mm of the position in px.
   */
  public abstract Point2f computeDisplayPosition(Point2i pPx);
  
  
  /**
   * Computes the page index and the page coordinates at the
   * given position in px.
   * If there is no page at the given position, null is returned.
   * If the view doesn't show pages, the same coordinates with
   * page set to -1 are returned.
   */
  public LayoutPosition computeLayoutPosition(Point2i pPx)
  {
  	//default case: view shows no pages
  	Point2f pMm = computeDisplayPosition(pPx);
  	return new LayoutPosition(-1, pMm.x, pMm.y);
  }
  
  
  /**
   * Transforms the given screen coordinates in px to
   * layout coordinates, using the given page.
   * If the view doesn't show pages, the same coordinates with
   * page set to -1 are returned.
   */
  public LayoutPosition computeLayoutPosition(Point2i pPx, Page page)
  {
  	//default case: view shows no pages
  	Point2f pMm = computeDisplayPosition(pPx);
  	return new LayoutPosition(-1, pMm.x, pMm.y);
  }
  
  
  /**
   * Gets the scroll position in px.
   */
  public Point2i getScrollPositionPx()
  {
    return Units.mmToPx(getScrollPosition(), currentZoom);
  }
  
  
  /**
	 * Gets the ScorePanelController assigned to this view.
	 */
	@Override public abstract ScorePanelController getPanelController();
	
	
	/**
	 * Ends the scroll tool.
	 */
	public void stopScrollTool()
	{
		this.scrollTool = null;
	}
  

}
