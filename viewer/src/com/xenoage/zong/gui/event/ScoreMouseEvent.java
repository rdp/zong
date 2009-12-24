package com.xenoage.zong.gui.event;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.layout.Layout;
import com.xenoage.zong.layout.LayoutPosition;
import com.xenoage.zong.layout.frames.FramePosition;
import com.xenoage.zong.view.ScoreView;


/**
 * An event which indicates that a mouse
 * action occurred on a score document.
 * 
 * It contains the coordinates (in screen space,
 * layout space and frame space, if available) and the
 * corresponding layout and view.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class ScoreMouseEvent
{
  
  private ScoreDocument document;
  
  private int button;
  
  //layout information
  private Layout layout;
  private LayoutPosition layoutPosition;
  
  //frame information
  private FramePosition framePosition;
  
  //view information
  private ScoreView view;
	private Point2f viewPositionMm;
	private Point2i viewPositionPx;
	private Point2i screenPositionPx;
	
	//pressed buttons
	private boolean keyShiftDown;
	private boolean keyCtrlDown;
	private boolean keyAltDown;
  
  
  /**
   * Creates a new mouse event that occurred on a score document.
	 * @param event		the original AWT mouse event
   * @param view		the ScoreView in which the event happened
   */
  public ScoreMouseEvent(MouseEvent event, ScoreView view)
  {
    this.document = view.getDocument();
    this.layout = view.getLayout();
    this.button = event.getButton();
    this.viewPositionPx = new Point2i(event.getX(), event.getY());

    if (event.getComponent() != null)
    {
    	Point p = event.getComponent().getLocationOnScreen();
    	this.screenPositionPx = new Point2i(this.viewPositionPx.x + p.x, this.viewPositionPx.y + p.y);
    }
    else
    {
    	this.screenPositionPx = this.viewPositionPx;
    }

    this.layoutPosition = view.computeLayoutPosition(this.viewPositionPx);
    this.framePosition = this.layout.computeFramePosition(this.layoutPosition);
    this.view = view;
    this.viewPositionMm = this.view.computeDisplayPosition(this.viewPositionPx);
    
    //keys
    this.keyShiftDown = ((event.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) > 0);
    this.keyCtrlDown = ((event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) > 0);
    this.keyAltDown = ((event.getModifiersEx() & InputEvent.ALT_DOWN_MASK) > 0);
    	
  }
  
  
  /**
   * Gets the score document that was hit by this mouse event.
   */
  public ScoreDocument getDocument()
  {
    return document;
  }
  
  
  /**
   * Gets the button of the mouse event.
   * This is one of the MouseEvent.BUTTON-constants
   */
  public int getButton()
  {
    return button;
  }
  
  
  /**
   * Gets the layout that was hit by this mouse event.
   */
  public Layout getLayout()
  {
    return layout;
  }

  
  /**
   * Gets the layout-dependent position of this mouse event,
   * or null, if not available.
   */
  public LayoutPosition getLayoutPosition()
  {
    return layoutPosition;
  }

  
  /**
   * Gets the hit frame and position in frame space,
   * or null, if no frame was hit.
   */
  public FramePosition getFramePosition()
  {
    return framePosition;
  }


	/**
	 * Gets the {@link ScoreView} in which the event happened.
	 */
	public ScoreView getView()
	{
		return view;
	}


	/**
	 * Gets the position of this mouse event within the view in mm.
	 */
	public Point2f getViewPositionMm()
	{
		return viewPositionMm;
	}
	
	
	/**
	 * Gets the position of this mouse event within the view in px.
	 */
	public Point2i getViewPositionPx()
	{
		return viewPositionPx;
	}
	
	
	/**
	 * Gets the position of this mouse event on the screen in px.
	 */
	public Point2i getScreenPositionPx()
	{
		return screenPositionPx;
	}


	/**
	 * Returns true, when the shift key was pressed when
	 * this mouse event happened.
	 */
	public boolean isKeyShiftDown()
	{
		return keyShiftDown;
	}


	/**
	 * Returns true, when the ctrl key was pressed when
	 * this mouse event happened.
	 */
	public boolean isKeyCtrlDown()
	{
		return keyCtrlDown;
	}


	/**
	 * Returns true, when the alt key was pressed when
	 * this mouse event happened.
	 */
	public boolean isKeyAltDown()
	{
		return keyAltDown;
	}
  
  
}
