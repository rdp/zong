package com.xenoage.zong.view;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.xenoage.util.MathTools;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.documents.ViewerDocument;
import com.xenoage.zong.gui.controller.panels.AppDocumentPanelController;
import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.renderer.GraphicsContext;
import com.xenoage.util.Units;


/**
 * Interface for a view on a document,
 * including zoom and scrolling position.
 * 
 * A view can be observed from other objects
 * so they can react at once if the view changes.
 *
 * @author Andreas Wenger
 */
public abstract class View
{
  
  //zooming and scrolling
  protected float targetZoom = 1;
  protected float currentZoom = targetZoom; //needed for smooth scrolling
  protected float zoomSteps[] = {0.125f, 0.25f, 0.5f, 0.75f, 1, 1.5f, 2, 3, 4, 6, 8, 12, 16, 32, 64};
  protected int zoomStepIndex = 4;
  
  private Point2f scrollPosition = new Point2f(0, 0);
  private Point2f targetScrollPosition = null; //for smooth scrolling
  
  //minimal and maximal zoom
  private static final float minZoom = 0.1f;
  private static final float maxZoom = 80f;
  
  //smooth zooming thread
  private ViewZoomThread zoomThread;
  
  //repaint thread
  private ViewRepaintThread repaintThread;
  
  
  protected View()
  {
  	this.repaintThread = new ViewRepaintThread(this);
  	this.repaintThread.start();
  	this.zoomThread = new ViewZoomThread(this);
  	this.zoomThread.start();
  }
  
  
  /**
   * Gets the document this view shows.
   */
  public abstract ViewerDocument getDocument();
  
  
  /**
   * Gets the layout this view shows.
   */
  public abstract Layout getLayout();
  
  
  /**
   * Gets the target zoom factor.
   */
  public float getTargetZoom()
  {
    return targetZoom;
  }
  
  
  /**
   * Sets the zoom factor immediately (not smooth) and repaints the view.
   * @param zoom  zoom factor. e.g. 2 means double size
   */
  public void setZoom(float zoom)
  {
    //clamp to minimum and maximum zoom 
    this.targetZoom = MathTools.clamp(zoom, minZoom, maxZoom);
    this.currentZoom = this.targetZoom;
    repaint();
  }
  
  
  /**
   * Sets the temporary zoom factor (needed for smooth zooming)
   * and repaints the view.
   * @param zoom  zoom factor. e.g. 2 means double size
   */
  public synchronized void setCurrentZoom(float zoom)
  {
    //clamp to minimum and maximum zoom 
    this.currentZoom = MathTools.clamp(zoom, minZoom, maxZoom);
    //System.out.println(zoomTemporary);
    repaint();
  }
  
  
  /**
   * Gets the target scaling factor (zoom, may be multiplied with other factors).
   */
  public float getTargetScaling()
  {
    return targetZoom;
  }
  
  
  /**
   * Gets the current scaling factor (zoom, may be multiplied with other factors).
   */
  public float getCurrentScaling()
  {
    return currentZoom;
  }
  
  
  /**
   * Gets the scroll position.
   */
  public Point2f getScrollPosition()
  {
    return scrollPosition;
  }
  
  
  /**
   * Gets the target scroll position for smooth scrolling,
   * or null if there is none..
   */
  public Point2f getTargetScrollPosition()
  {
    return targetScrollPosition;
  }
  
  
  /**
   * Sets the scroll position immediately (interrupting
   * any smooth scrolling).
   */
  void setScrollPosition(Point2f scrollPosition)
  {
  	if (scrollPosition == null)
  		throw new IllegalArgumentException();
    this.scrollPosition = scrollPosition;
    this.targetScrollPosition = null;
    repaint();
  }
  
  
  /**
   * Sets the temporary scroll position factor (needed for smooth scrolling)
   * and repaints the view.
   */
  public synchronized void setCurrentScrollPosition(Point2f scrollPosition)
  {
  	if (scrollPosition == null)
  		throw new IllegalArgumentException();
    this.scrollPosition = scrollPosition;
    repaint();
  }
  
  
  /**
   * Gets the size of the panel.
   */
  public Size2i getSize()
  {
  	return getPanelController().getSize();
  }

  
  /**
   * Gets the AppDocumentPanelController assigned to this view.
   */
  public abstract AppDocumentPanelController getPanelController();
  
  
  /**
   * Paints the layout in this view on the given
   * graphics context.
   */
  public abstract void paint(GraphicsContext g);
  
  
  /**
   * Does the actual repainting, called by the repaint thread.
   */
  protected synchronized void doRepaint()
  {
    AppDocumentPanelController panelController = getPanelController();
    if (panelController != null)
    {
    	//TEST float testStartZoom = currentZoom;
    	panelController.repaint();
    	//TEST
    	//if (currentZoom != testStartZoom)
    	//	System.out.println("WARNING: Zoom was changed during rendering!");
			//System.out.println(getCurrentScaling());
    }
  }
  
  
  /**
   * Call this method to repaint this view.
   */
  public void repaint()
  {
  	repaintThread.requestRepaint();
  }
  
  
  /**
	 * Requests continuous repaints for the given time in ms.  
	 */
	public void requestRepaintsForTime(int ms)
	{
		repaintThread.requestRepaintsForTime(ms);
	}
  
  
  /**
   * Zooms one step in.
   */
  public synchronized void zoomIn()
  {
  	//find target step index
  	int targetStepIndex = 0;
  	while (targetStepIndex < zoomSteps.length - 1 && zoomSteps[targetStepIndex] <= targetZoom)
  	{
  		targetStepIndex++;
  	}
  	//apply zoom smoothly
	  targetZoom = zoomSteps[targetStepIndex];
  }
  
  
  /**
   * Zooms one step out.
   */
  public synchronized void zoomOut()
  {
  	//find target step index
  	int targetStepIndex = zoomSteps.length - 1;
  	while (targetStepIndex > 0 && zoomSteps[targetStepIndex] >= targetZoom)
  	{
  		targetStepIndex--;
  	}
  	//apply zoom smoothly
	  targetZoom = zoomSteps[targetStepIndex];
  }
  
  
  /**
   * Zooms one step out.
   */
  public synchronized void zoomTo(float targetZoom)
  {
  	this.targetZoom = targetZoom;
  }
  
  
  /**
   * Scrolls the given amount of px immediately.
   */
  public void scrollInPx(Point2i amountPx)
  {
    Point2f newScroll = scrollPosition;
    newScroll = newScroll.sub(Units.pxToMm(amountPx.x, targetZoom),
      Units.pxToMm(amountPx.y, targetZoom));
    setScrollPosition(newScroll);
  }
  
  
  /**
   * Scrolls the to the given position in mm smoothly.
   */
  public void scrollToSmooth(Point2f pMm)
  {
  	targetScrollPosition = pMm;
  }
  
  
  /**
   * Invoked when a key has been pressed on this view.
   */
  public abstract void keyPressed(KeyEvent e);
  
  
  /**
   * Invoked when a mouse button has been pressed on this view.
   */
  public abstract void mouseClicked(MouseEvent e);

  
  /**
   * This method is called when the mouse was pressed over the view.
   */
  public abstract void mousePressed(MouseEvent e);
  
  
  /**
   * This method is called when the mouse was released over the view.
   */
  public abstract void mouseReleased(MouseEvent e);
  
  
  /**
   * This method is called when the mouse was moved on the view
   * with a button pressed.
   */
  public abstract void mouseDragged(MouseEvent e);
  
  
  /**
   * This method is called when the mouse was moved on the view
   * without a button pressed.
   */
  public abstract void mouseMoved(MouseEvent e);

  
  @Override protected void finalize()
  {
  	repaintThread.interrupt(); //close repaint thread
  }
  
  
  /**
   * This method is called by the score panel when it was resized.
   */
  public void resize()
  {
  }

  
}
