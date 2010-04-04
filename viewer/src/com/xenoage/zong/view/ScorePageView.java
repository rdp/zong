package com.xenoage.zong.view;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.xenoage.util.Units;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2f;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.util.math.Size2f;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.tools.Tool;
import com.xenoage.zong.app.tools.ToolPreview;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.gui.controller.panels.ScorePanelController;
import com.xenoage.zong.gui.event.ScoreMouseEvent;
import com.xenoage.zong.gui.score.GUIManager;
import com.xenoage.zong.gui.score.GUIManager.Layer;
import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.layout.LayoutPosition;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.FrameHandle;
import com.xenoage.zong.layout.frames.FramePosition;
import com.xenoage.zong.layout.observer.LayoutObserver;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.GraphicsContext;
import com.xenoage.zong.renderer.screen.page.GLPageRenderer;


/**
 * Page view for a score document, showing a layout with pages.
 * 
 * The scrolling position in mm is relative to the center of the first page.
 * 
 * TODO: TIDY !!!
 * TODO: move as much as possible in EditorScorePageView
 * 
 * @author Andreas Wenger
 */
public class ScorePageView
	extends ScoreView
	implements LayoutObserver
{

	//the document this view belongs to
	protected ScoreDocument document;

	//the layout this view is showing
	protected Layout layout;

	//the score panel controller that belongs to this view
	protected ScorePanelController scorePanelCtrl;

	//special zooms
	public static final int ZOOM_FITPAGE = 0;
	public static final int ZOOM_FITWIDTH = 1;
	public static final int ZOOM_FITHEIGHT = 2;

	//the display distance between two pages in mm
	public enum PageDisplayAlignment
	{
		Horizontal,
		Vertical
	};

	private PageDisplayAlignment pageDisplayAlignment = PageDisplayAlignment.Horizontal;
	
	//GUI manager
	protected final GUIManager guiManager;


	private float pageDisplayDistance = 30;

	//bounding rectangles of the pages
	private ArrayList<Rectangle2f> pageRects;

	//the current adjustment handle with a hover-effect, or null
	protected FrameHandle currentHandleHover = null;
	
	//for generating mouseEntered and mouseExited events:
	//last frames the mouse was over
	private Frame hoverFrame = null;


	/**
	 * Creates the a page view for a score document, assigned to the given
	 * ScorePanelController.
	 */
	public ScorePageView(ScoreDocument document, Layout layout,
		ScorePanelController scorePanelCtrl)
	{
		this.document = document;
		this.layout = layout;
		this.scorePanelCtrl = scorePanelCtrl;
		if (this.scorePanelCtrl != null)
		{
			this.scorePanelCtrl.setView(this);
		}
		
		//GUI manager
		this.guiManager = new GUIManager(this);

		//compute the page bounding rectangles
		this.pageRects = computePageRects();

		//observe the layout (deprecated)
		this.layout.addObserver(this);
	}


	/**
	 * Gets the score document this view shows.
	 */
	@Override
	public ScoreDocument getDocument()
	{
		return document;
	}


	/**
	 * Gets the current layout this view shows.
	 */
	@Override public Layout getLayout()
	{
		return layout;
	}


	/**
	 * Sets a special zoom.
	 * @param zoom ZOOM_FITPAGE, ZOOM_FITWIDTH or ZOOM_FITHEIGHT.
	 */
	public void setSpecialZoom(int specialZoom)
	{
		// TODO
		repaint();
	}


	/**
	 * Gets the ScorePanelController assigned to this view.
	 */
	@Override
	public ScorePanelController getPanelController()
	{
		return scorePanelCtrl;
	}


	public PageDisplayAlignment getPageDisplayAlignment()
	{
		return pageDisplayAlignment;
	}


	public void setPageDisplayAlignment(PageDisplayAlignment pageDisplayAlignment)
	{
		this.pageDisplayAlignment = pageDisplayAlignment;
		this.pageRects = computePageRects();
		repaint();
	}


	/**
	 * Paints the layout (and, if on the screen, the GUI elements)
	 * in this view on the given graphics context.
	 */
	@Override public void paint(GraphicsContext g)
	{
		//draw desktop
		GLPageRenderer.getInstance().paintDesktop(g);
		//GUI's back panel
		guiManager.paint((GLGraphicsContext) g, Layer.Back);
		//draw pages
		GLPageRenderer.getInstance().paintPages(this, g);
		//GUI's front panel
		guiManager.paint((GLGraphicsContext) g, Layer.Front);
	}


	/**
	 * Computes the page index and the page coordinates at the given position in
	 * px. If there is no page at the given position, null is returned.
	 */
	@Override public LayoutPosition computeLayoutPosition(Point2i pPx)
	{
		Point2f pMm = computeDisplayPosition(pPx);
		//TODO: performance? binary search?
		for (int page = pageRects.size() - 1; page >= 0; page--)
		{
			if (pageRects.get(page).contains(pMm))
			{
				pMm = pMm.sub(getPageDisplayOffset(page));
				return new LayoutPosition(page, pMm.x, pMm.y);
			}
		}
		return null;
	}


	/**
	 * Computes the page coordinates at the given position in px, relative to
	 * the upper left corner of the given page.
	 */
	@Override public LayoutPosition computeLayoutPosition(Point2i px, Page page)
	{
		int pageIndex = layout.getPages().indexOf(page);
		return computeLayoutPosition(px, pageIndex);
	}


	/**
	 * Computes the page coordinates at the given position in px, relative to
	 * the upper left corner of the given page.
	 */
	public LayoutPosition computeLayoutPosition(Point2i pPx, int page)
	{
		Point2f pMm = computeDisplayPosition(pPx);
		Point2f pageOffset = pageRects.get(page).position;
		pMm = pMm.sub(pageOffset);
		return new LayoutPosition(page, pMm.x, pMm.y);
	}
	
	
	/**
	 * Computes the FramePosition at the given given position in px, or null,
	 * if there is no frame.
	 */
	public FramePosition computeFramePosition(Point2i pPx)
	{
		LayoutPosition lp = computeLayoutPosition(pPx);
		if (lp != null)
			return layout.computeFramePosition(lp);
		else
			return null;
	}


	/**
	 * Computes the offsets of all pages in mm, relative to the upper left
	 * corner of the first page.
	 */
	private ArrayList<Rectangle2f> computePageRects()
	{
		float offsetX = 0; // horizontal center of the current page
		float offsetY = 0; // vertical center of the current page
		List<Page> pages = layout.getPages();
		ArrayList<Rectangle2f> ret = new ArrayList<Rectangle2f>(pages.size());
		if (pages.size() > 0)
		{
			// compute offsets for all pages
			float firstPageOffsetX = -pages.get(0).getFormat().getSize().width / 2;
			float firstPageOffsetY = -pages.get(0).getFormat().getSize().height / 2;
			for (int i = 0; i < pages.size(); i++)
			{
				Size2f pageSize = pages.get(i).getFormat().getSize();
				Point2f offset = null;
				switch (pageDisplayAlignment)
				{
					case Horizontal:
						offset = new Point2f(offsetX + firstPageOffsetX, -pageSize.height / 2);
						offsetX += pageSize.width + pageDisplayDistance;
						break;
					case Vertical:
						offset = new Point2f(-pageSize.width / 2, offsetY + firstPageOffsetY);
						offsetY += pageSize.height + pageDisplayDistance;
						break;
				}
				ret.add(new Rectangle2f(offset, new Size2f(pageSize)));
			}
		}
		return ret;
	}


	/**
	 * Transforms the given layout coordinates to screen coordinates in px. If
	 * not possible, null is returned.
	 */
	public Point2i computeScreenPosition(LayoutPosition p)
	{
		Point2f pos = getPageDisplayOffset(p.getPageIndex());
		pos = pos.add(p.getPosition());
		pos = pos.sub(getScrollPosition());
		Point2i ret = new Point2i(Units.mmToPx(pos.x, currentZoom), Units.mmToPx(pos.y,
			currentZoom));
		Size2i panelSize = scorePanelCtrl.getSize();
		ret = ret.add(panelSize.width / 2, panelSize.height / 2);
		return ret;
	}
	
	
	/**
	 * Transforms the given layout coordinates to screen coordinates in px. If
	 * not possible, null is returned.
	 */
	public Rectangle2i computeScreenPosition(int page, Rectangle2f rect)
	{
		Point2f nw = rect.position;
		Point2f se = nw.add(rect.size);
		Point2i nwPx = computeScreenPosition(new LayoutPosition(page, nw));
		Point2i sePx = computeScreenPosition(new LayoutPosition(page, se));
		return new Rectangle2i(nwPx, new Size2i(sePx.sub(nwPx)));
	}


	/**
	 * Gets the display position in mm of the page with the given index,
	 * relative to the center of the first page.
	 * @param pageIndex the index of the page, beginning at 0
	 */
	public Point2f getPageDisplayOffset(int pageIndex)
	{
		return pageRects.get(pageIndex).position;
	}


	float getPageDisplayDistance()
	{
		return pageDisplayDistance;
	}


	/**
	 * Invoked when a key has been pressed on this view.
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		// special keys (TODO: move elsewhere)
		if (keyCode == KeyEvent.VK_PLUS || keyCode == KeyEvent.VK_MINUS)
		{
			if (keyCode == KeyEvent.VK_PLUS)
			{
				zoomIn();
			}
			else if (keyCode == KeyEvent.VK_MINUS)
			{
				zoomOut();
			}
		}
	}
	
	
	@Override public void mouseClicked(MouseEvent e)
	{
		//forward to GUI's front panel. if not consumed, send it to scores
		if (!(isGUIUsable() && guiManager.mouseClicked(e, Layer.Front)))
		{
			//forward to scores. if not consumed again, forward to GUI's back panel
			if (!mouseClickedNotGUI(e))
			{
				if (isGUIUsable()) guiManager.mouseClicked(e, Layer.Back);
			}
		}
	}
	
	
	/**
	 * Returns only false, when no page was hit.
	 */
	protected boolean mouseClickedNotGUI(MouseEvent e)
	{
		//forward event to frame
		FramePosition fp = computeFramePosition(new Point2i(e.getPoint()));
		if (fp != null)
		{
			fp.getFrame().mouseClicked(new ScoreMouseEvent(e, this));
			return true;
		}
		return (computeLayoutPosition(new Point2i(e.getPoint())) != null);
	}


	/**
	 * This method is called when a mouse button was pressed over this view and
	 * no tool was active at this moment.
	 */
	@Override public void mousePressed(MouseEvent e)
	{
		//forward to GUI's front panel. if not consumed, send it to scores
		if (!(isGUIUsable() && guiManager.mousePressed(e, Layer.Front)))
		{
			//forward to scores. if not consumed again, forward to GUI's back panel
			if (!mousePressedNotGUI(e))
			{
				if (isGUIUsable()) guiManager.mousePressed(e, Layer.Back);
			}
		}
	}
	
	
	/**
	 * Returns only false, when no page was hit.
	 */
	protected boolean mousePressedNotGUI(MouseEvent e)
	{
		//middle button and right button are reserved for scrolling and context menus.
		if (e.getButton() == MouseEvent.BUTTON2)
		{
			//middle button was pressed
			//begin scroll
			scrollTool = new ScrollTool(this,
				new Point2i(e.getX(), e.getY()), e.getButton());
			return true;
		}
		//forward event to frame
		boolean consumed = false;
		FramePosition fp = computeFramePosition(new Point2i(e.getPoint()));
		if (fp != null)
		{
			consumed = fp.getFrame().mousePressed(new ScoreMouseEvent(e, this));
		}
		//if unconsumed, the cursor is over empty area,
		//which is interpreted as scrolling for button 1
		if (!consumed && e.getButton() == MouseEvent.BUTTON1)
		{
			//begin scroll
			scrollTool = new ScrollTool(this,
				new Point2i(e.getX(), e.getY()), e.getButton());
		}
		return (computeLayoutPosition(new Point2i(e.getPoint())) != null);
	}


	/**
	 * Invoked when a mouse button has been released on this view.
	 */
	@Override public void mouseReleased(MouseEvent e)
	{
		//forward to GUI's front panel. if not consumed, send it to scores
		if (!(isGUIUsable() && guiManager.mouseReleased(e, Layer.Front)))
		{
			//forward to scores. if not consumed again, forward to GUI's back panel
			if (!mouseReleasedNotGUI(e))
			{
				if (isGUIUsable()) guiManager.mouseReleased(e, Layer.Back);
			}
		}
	}
	
	
	/**
	 * Returns only false, when no page was hit.
	 */
	protected boolean mouseReleasedNotGUI(MouseEvent e)
	{
		//scroll
		if (scrollTool != null)
		{
			scrollTool.mouseReleased(new ScoreMouseEvent(e, this));
			//no further use of this event.
			return true;
		}
		//tool
		Tool tool = document.getSelectedTool();
		if (tool != null)
		{
			tool.mouseReleased(new ScoreMouseEvent(e, this));
		}
		//forward event to frame
		FramePosition fp = computeFramePosition(new Point2i(e.getPoint()));
		if (fp != null)
		{
			fp.getFrame().mouseReleased(new ScoreMouseEvent(e, this));
		}
		return (computeLayoutPosition(new Point2i(e.getPoint())) != null);
	}


	/**
	 * This method is called when the mouse was moved on the view with a button
	 * pressed.
	 */
	@Override public void mouseDragged(MouseEvent e)
	{
		//forward to GUI's front panel. if not consumed, send it to scores
		if (!(isGUIUsable() && guiManager.mouseDragged(e, Layer.Front)))
		{
			//forward to scores. if not consumed again, forward to GUI's back panel
			if (!mouseDraggedNotGUI(e))
			{
				if (isGUIUsable()) guiManager.mouseDragged(e, Layer.Back);
			}
		}
	}
	
	
	
	protected boolean mouseDraggedNotGUI(MouseEvent e)
	{
		FramePosition fp = computeFramePosition(new Point2i(e.getPoint()));
		//scroll
		if (scrollTool != null)
		{
			scrollTool.mouseDragged(new ScoreMouseEvent(e, this));
			//no further use of this event.
			return true;
		}
		//events for frames
		generateMouseEnteredAndExitedEvents(fp, e);
		if (fp != null)
		{
			fp.getFrame().mouseDragged(new ScoreMouseEvent(e, this));
		}
		//when a tool is active, send the event to the tool
		Tool tool = document.getSelectedTool();
		if (tool != null)
		{
			tool.mouseDragged(new ScoreMouseEvent(e, this));
		}
		return (computeLayoutPosition(new Point2i(e.getPoint())) != null);
	}


	/**
	 * This method is called when the mouse was moved on the view without a
	 * button pressed.
	 */
	@Override public void mouseMoved(MouseEvent e)
	{
		//forward to GUI's front panel. if not consumed, send it to scores
		if (!(isGUIUsable() && guiManager.mouseMoved(e, Layer.Front)))
		{
			//forward to scores. if not consumed again, forward to GUI's back panel
			if (!mouseMovedNotGUI(e))
			{
				if (isGUIUsable()) guiManager.mouseMoved(e, Layer.Back);
			}
		}
	}
	
	
	/**
	 * Returns only false, when no page was hit.
	 */
	protected boolean mouseMovedNotGUI(MouseEvent e)
	{
		FramePosition fp = computeFramePosition(new Point2i(e.getPoint()));
		//events for frames
		generateMouseEnteredAndExitedEvents(fp, e);
		if (fp != null)
		{
			fp.getFrame().mouseMoved(new ScoreMouseEvent(e, this));
		}
		//tool active?
		boolean consumed = false;
		Tool tool = document.getSelectedTool();
		if (tool != null)
		{
			//send the event to the tool
			consumed = tool.mouseMoved(new ScoreMouseEvent(e, this));
		}
		if (!consumed)
		{
			//show tool preview
			ToolPreview toolPreview = App.getInstance().getToolPreview();
			if (toolPreview != null)
			{
				toolPreview.mouseMoved(this, new Point2i(e.getPoint()));
				return true;
			}
		}
		return (computeLayoutPosition(new Point2i(e.getPoint())) != null);
	}


	/**
	 * This method is called when a new page was added to the layout.
	 */
	public void pageAdded(Page page)
	{
		//recompute the page bounding rects
		pageRects = computePageRects();
	}


	/**
	 * This method is called when the format of the given page within the layout
	 * has been changed.
	 */
	public void pageFormatChanged(Page page)
	{
		//recompute the page bounding rects
		pageRects = computePageRects();
	}


	/**
	 * Computes the index of the page that is behind the given coordinates in
	 * px. If it is before the first page, the first page is returned. If it is
	 * between two pages, the left/top one is returned.
	 * Unlike in <code>getPageIndex</code>, -1 is never returned.
	 */
	int getNearestPageIndex(Point2f pMm)
	{
		// relevant axis: x when using horizontal page alignment, else y
		float p = (pageDisplayAlignment == PageDisplayAlignment.Horizontal ? pMm.x : pMm.y);
		// TODO (when closures are available): use binary search instead to find
		// page very fast
		if (pageRects.size() == 0)
		{
			return 0;
		}
		else
		{
			int ret = 0;
			for (int i = 1; i < pageRects.size(); i++)
			{
				Point2f po = getPageDisplayOffset(i);
				float poc = (pageDisplayAlignment == PageDisplayAlignment.Horizontal ? po.x : po.y);
				if (p > poc)
					ret++;
				else
					break;
			}
			return ret;
		}
	}
	

	/**
	 * Computes and returns the adjustment handle below the given coordinates in
	 * px, or null, if none was found.
	 */
	@Override public FrameHandle computeFrameHandleAt(Point2i pPx)
	{
		// if there are no pages, there are no handles
		if (pageRects.size() == 0)
			return null;
		FrameHandle handle;
		Point2f pMm = computeDisplayPosition(pPx);
		// get the page behind the coordinates
		int pageIndex = getNearestPageIndex(pMm);
		// look on this page first. if a handle is found, return it.
		Point2f pageOffset = getPageDisplayOffset(pageIndex);
		handle = layout.computeFrameHandleAt(new LayoutPosition(pageIndex, pMm.x
			- pageOffset.x, pMm.y - pageOffset.y), currentZoom);
		if (handle != null)
			return handle;
		// if not found, we look one page before
		if (pageIndex > 0)
		{
			pageOffset = getPageDisplayOffset(pageIndex - 1);
			handle = layout.computeFrameHandleAt(new LayoutPosition(pageIndex - 1, pMm.x
				- pageOffset.x, pMm.y - pageOffset.y),
				currentZoom);
			if (handle != null)
				return handle;
		}
		// if still not found, we look one page later
		if (pageIndex < pageRects.size() - 1)
		{
			pageOffset = getPageDisplayOffset(pageIndex + 1);
			handle = layout.computeFrameHandleAt(new LayoutPosition(pageIndex + 1, pMm.x
				- pageOffset.x, pMm.y - pageOffset.y),
				currentZoom);
			if (handle != null)
				return handle;
		}
		// if still nothing found, return null
		return null;
	}


	/**
	 * Returns the display position in mm of the given position in px,
	 * relative to the center point of the first page.
	 * TIDY: method name?!
	 */
	@Override public Point2f computeDisplayPosition(Point2i pPx)
	{
		Size2i panelSize = scorePanelCtrl.getSize();
		Point2f pMm = new Point2f(Units.pxToMm(pPx.x - panelSize.width / 2, currentZoom),
			Units.pxToMm(pPx.y - panelSize.height / 2, currentZoom));
		pMm = pMm.add(getScrollPosition());
		return pMm;
	}


	/**
	 * Shows a hover-effect on the given adjustment handle. All other adjustment
	 * handles lose there hover-effect.
	 * @param handle the handle, or null to remove all hover-effects TODO: move
	 * into page view class?
	 */
	public void showHandleHover(FrameHandle handle)
	{
		if (handle == null)
		{
			// remove effects
			if (currentHandleHover != null)
			{
				currentHandleHover.getFrame().setSelectedHandle(null);
				currentHandleHover = null;
				repaint();
			}
		}
		else if (!handle.equals(currentHandleHover))
		{
			// active handle has changed
			if (currentHandleHover != null)
			{
				currentHandleHover.getFrame().setSelectedHandle(null);
			}
			currentHandleHover = handle;
			currentHandleHover.getFrame().setSelectedHandle(currentHandleHover.getPosition());
			repaint();
		}
	}

	
	/**
	 * Generates <code>mouseEntered</code> and <code>mouseExited</code> events
	 * and sends them to the appropriate frames.
	 * @param framePosition  the current position in reference to a frame, or null
	 * @param mouseEvent     the original mouse event
	 */
	private void generateMouseEnteredAndExitedEvents(FramePosition framePosition, MouseEvent mouseEvent)
	{
		if (framePosition != null)
		{
			if (framePosition.getFrame() != hoverFrame)
			{
				if (hoverFrame != null)
				{
					//old frame lost its hover state
					hoverFrame.mouseExited(new ScoreMouseEvent(mouseEvent, this));
				}
				//new frame gets hover state
				hoverFrame = framePosition.getFrame();
				hoverFrame.mouseEntered(new ScoreMouseEvent(mouseEvent, this));
			}
		}
		else if (hoverFrame != null)
		{
			//frame lost its hover state
			hoverFrame.mouseExited(new ScoreMouseEvent(mouseEvent, this));
			hoverFrame = null;
		}
	}
	
	
	/**
   * Scrolls to the given {@link LayoutPosition}.
   */
  public void scrollTo(LayoutPosition goal)
  {
    setScrollPosition(getPageDisplayOffset(goal.getPageIndex()).add(goal.getPosition()));
  }
  
  
  /**
   * Gets the {@link GUIManager} of this view.
   */
  public GUIManager getGUIManager()
  {
  	return guiManager;
  }
  
  
  /**
   * This method is called by the score panel when it was resized.
   */
  @Override public void resize()
  {
  	guiManager.resizeView();
  }
  
  
  /**
   * Returns true, if no scrolling is done and if
   * either no tool is active or if the active tools allows using the GUI.
   */
  public boolean isGUIUsable()
  {
  	return scrollTool == null &&
  		(document.getSelectedTool() == null || document.getSelectedTool().isGUIActive());
  }
	
	
}
