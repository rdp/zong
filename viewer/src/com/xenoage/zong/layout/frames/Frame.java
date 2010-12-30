package com.xenoage.zong.layout.frames;

import com.xenoage.util.MathTools;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.gui.event.ScoreMouseEvent;
import com.xenoage.zong.gui.event.ScoreMouseListener;
import com.xenoage.zong.layout.LayoutPosition;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.FrameHandle.HandlePosition;
import com.xenoage.zong.layout.frames.background.Background;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.zong.renderer.frames.GLFrameHandlesRenderer;
import com.xenoage.util.Units;


/**
 * A frame is the abstract base class for
 * object in a layout of a score document.
 * 
 * It is basically a rectangle with a position and size
 * and rotation and an optional background.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public abstract class Frame
	implements ScoreMouseListener
{
  
  protected Point2f position; //center point of the frame in mm
  protected Size2f size; //size of the frame in mm
  protected float rotation; //ccw. rotation of the frame in degrees
  protected Background background; //background of the frame, or null
  
  protected boolean selected = false; //selection state of the frame
  protected HandlePosition selectedHandle = null; //the currently selected handle, or null
  
  protected Frame parentFrame = null; //the parent frame, or null
  protected Page parentPage = null; //the parent page, or null, if there is a parent frame
  
  
  
  /**
   * Creates a new frame.
   * @param position  center position of the frame in mm.
   * @param size      size of the frame in mm.
   */
  public Frame(Point2f position, Size2f size)
  {
    super();
    this.position = position;
    this.size = size;
    this.rotation = 0;
    this.background = null;
    
    //TEST
    //this.selected = true;
  }


  /**
   * Gets the center position of the frame in mm,
   * relative the position of the parent frame, or if
   * not available, to the page.
   */
  public Point2f getRelativePosition()
  {
    return position;
  }
  
  
  /**
   * Gets a copy of the center position of the frame in mm,
   * relative to the page.
   */
  public Point2f getAbsolutePosition()
  {
    Point2f ret = getRelativePosition();
    if (parentFrame != null)
    {
      ret = MathTools.rotate(ret, parentFrame.getAbsoluteRotation());
      ret = ret.add(parentFrame.getAbsolutePosition());
    }
    return ret;
  }

  
  /**
   * Gets the LayoutPosition of the center position of the frame.
   * If the LayoutPosition is undefined (e.g. Frame is not placed yet) null is returned.
   */
  public LayoutPosition getLayoutPosition()
  {
	  Point2f pos = getAbsolutePosition();
	  int pageIndex = getParentPage().getParentLayout().getPages().indexOf(getParentPage());
	  return new LayoutPosition(pageIndex,pos);
  }

  /**
   * Sets the center position of the frame in mm,
   * relative the position of the parent frame, or if
   * not available, to the page.
   */
  public void setRelativePosition(Point2f position)
  {
    this.position = position;
  }
  
  
  /**
   * Gets a copy of the size of the frame in mm.
   */
  public Size2f getSize()
  {
    return size;
  }


  /**
   * Sets the size of the frame in mm.
   */
  public void setSize(Size2f size)
  {
    this.size = size;
  }


  /**
   * Gets the counter clockwise rotation of the frame in degrees,
   * relative the rotation of the parent frame, if available.
   */
  public float getRelativeRotation()
  {
    return rotation;
  }
  
  
  /**
   * Gets the counter clockwise rotation of the frame in degrees.
   */
  public float getAbsoluteRotation()
  {
    float ret = getRelativeRotation();
    if (parentFrame != null)
    {
      ret += parentFrame.getAbsoluteRotation();
    }
    return ret;
  }


  /**
   * Sets the counter clockwise rotation of the frame in degrees,
   * relative the rotation of the parent frame, if available.
   */
  public void setRelativeRotation(float rotation)
  {
    this.rotation = rotation;
  }


  /**
   * Gets the background of this frame, or null.
   */
  public void setBackground(Background background)
  {
    this.background = background;
  }


  /**
   * Sets the background of this frame, or null.
   */
  public Background getBackground()
  {
    return background;
  }
  
  
  /**
   * Gets the parent frame of this frame, or null.
   */
  public Frame getParentFrame()
  {
    return parentFrame;
  }


  /**
   * Sets the parent frame of this frame, or null.
   */
  public void setParentFrame(Frame parentFrame)
  {
    this.parentFrame = parentFrame;
    //the parent page of this frame is now the parent
    //page of the parent's frame
    this.parentPage = null;
  }
  
  
  /**
   * Paints this frame with the given OpenGL context.
   */
  public abstract void paint(GLGraphicsContext context);
  
  
  /**
   * Paints the adjustment handles of this frame
   * with the given OpenGL context, if this frame is
   * selected.
   */
  public void paintHandles(GLGraphicsContext context)
  {
    if (this.selected)
    {
      GLFrameHandlesRenderer.getInstance().paint(this, context);
    }
  }
  
  
  /**
   * Paints this frame with the given Swing context.
   */
  public abstract void paint(SwingGraphicsContext context);
  
  
  /**
   * Transforms the given coordinates in page space to
   * a frame position.
   * If the given coordinates are not within this
   * frame, null is returned.
   */
  public FramePosition computeFramePosition(Point2f p)
  {
    return computeFramePosition(p, false);
  }
  
  
  /**
   * Transforms the given coordinates in page space to
   * a frame position.
   * If the given coordinates are not within this
   * frame and force is false, null is returned. Otherwise
   * the computed coordinates are returned, even if
   * they are outside the frame.
   */
  public FramePosition computeFramePosition(Point2f p, boolean force)
  {
    Point2f pos = getAbsolutePosition();
    float rot = getAbsoluteRotation();
    float hw = size.width / 2;
    float hh = size.height / 2;
    //two cases: no rotation or rotation
    if (rot == 0f)
    {
      //no rotation. this is easy to compute.
      if (force || (p.x >= pos.x - hw && p.x <= pos.x + hw &&
        p.y >= pos.y - hh && p.y <= pos.y + hh))
      {
        return new FramePosition(this, new Point2f(p.x - pos.x, p.y - pos.y));
      }
      else
      {
        return null;
      }
    }
    else
    {
      //rotated frame. this is more complicated.
      //first fast check: point within circle around center point?
      //radius is half width + half height (easy to compute)
      float radius = (size.width + size.height) / 2f;
      Point2f pRel = new Point2f(p.x - pos.x, p.y - pos.y);
      float distanceSq = pRel.x * pRel.x + pRel.y * pRel.y;
      if (force || (distanceSq <= radius * radius)) //TEST: does this really work?
      {
        //the given point could be within the frame. rotate the
        //point and check again.
        pRel = MathTools.rotate(pRel, -rot);
        if (force || (pRel.x >= -hw && pRel.x <= +hw &&
          pRel.y >= -hh && pRel.y <= +hh))
        {
          return new FramePosition(this, new Point2f(pRel.x, pRel.y));
        }
        else
        {
          return null;
        }
      }
      else
      {
        return null;
      }
    }
  }
  
  
  /**
   * Transforms the given coordinates in frame space to
   * a position in page space.
   * 
   * TODO: untested for higher levels
   */
  public Point2f computePagePosition(Point2f p)
  {
  	Point2f ret = p;
  	Frame frame = this;
  	//convert level after level, until page level is reached
  	while (frame != null)
  	{
  		if (frame.rotation != 0f)
  			ret = MathTools.rotate(ret, frame.rotation);
  		ret = ret.add(frame.position);
  		frame = frame.getParentFrame();
  	}
    return ret;
  }


  /**
   * Gets the selection state of this frame.
   */
  public boolean isSelected()
  {
    return selected;
  }


  /**
   * Sets the selection state of this frame.
   */
  public void setSelected(boolean selected)
  {
    this.selected = selected;
  }
  
  
  /**
   * Sets the selection state of this frame
   * and all child frames.
   */
  public void setSelectedRecursive(boolean selected)
  {
    setSelected(selected);
  }
  
  
  /**
   * Computes the adjustment handle at the given position
   * and returns it. If there is none, null is returned.
   * @param position    the position where to look for a handle
   * @param scaling     the current scaling factor
   * TODO: performance
   */
  public FrameHandle computeFrameHandleAt(Point2f layoutPosition, float scaling)
  {
  	if (!selected)
  		return null;
  	
    //translates the given point into frame space coordinates (still without rotation)
    Point2f pos = layoutPosition;
    pos = pos.sub(getAbsolutePosition());
    //rotate the point to get real frame space coordinates
    float rot = getAbsoluteRotation();
    if (rot != 0f)
    {
    	pos = MathTools.rotate(pos, -rot);
    }
  	
    //hit a handle?
    float s = Units.pxToMm(FrameHandle.SIZE_IN_PX, scaling) / 2; //half handle size in mm
    float x1 = -size.width / 2;
    float x2 = -x1;
    float y1 = -size.height / 2;
    float y2 = -y1;
    float rotY = Units.pxToMm(
      FrameHandle.DISTANCE_ROTATIONHANDLE_IN_PX, scaling); //distance of rotation handle in mm
    //N, NE, E, SE, S, SW, W or NW handle?
    if (pos.x >= x1 - s && pos.x <= x2 + s && pos.y >= y1 - s && pos.y <= y2 + s)
    {
      //left side: NW, W, SW
      if (pos.x <= x1 + s)
      {
        if (pos.y <= y1 + s)
        {
          //NW
          return new FrameHandle(this, HandlePosition.NW);
        }
        else if (pos.y >= y2 - s)
        {
          //SW
          return new FrameHandle(this, HandlePosition.SW);
        }
        else if (pos.y <= s && pos.y >= -s)
        {
          //W
          return new FrameHandle(this, HandlePosition.W);
        }
      }
      //left side: NE, E, SE
      if (pos.x >= x2 - s)
      {
        if (pos.y <= y1 + s)
        {
          //NE
          return new FrameHandle(this, HandlePosition.NE);
        }
        else if (pos.y >= y2 - s)
        {
          //SE
          return new FrameHandle(this, HandlePosition.SE);
        }
        else if (pos.y <= s && pos.y >= -s)
        {
          //E
          return new FrameHandle(this, HandlePosition.E);
        }
      }
      //middle side: N, S
      if (pos.x >= -s && pos.x <= s)
      {
        if (pos.y <= y1 + s)
        {
          //N
          return new FrameHandle(this, HandlePosition.N);
        }
        else if (pos.y >= y2 - s)
        {
          //S
          return new FrameHandle(this, HandlePosition.S);
        }
      }
      //frame border: hover distance = s / 2
      float fs = s / 2;
      if (
        (pos.x >= x1 - fs && pos.x <= x1 + fs) || //left border
        (pos.x >= x2 - fs && pos.x <= x2 + fs) || //right border
        (pos.y >= y1 - fs && pos.y <= y1 + fs) || //top border
        (pos.y >= y2 - fs && pos.y <= y2 + fs))   //bottom border
      {
        return new FrameHandle(this, HandlePosition.Move);
      }
    }
    //rotation handle?
    else if (pos.x >= -s && pos.x <= s && pos.y >= y1 - rotY - s && pos.y <= y1 - rotY + s)
    {
      return new FrameHandle(this, HandlePosition.Rotation);
    }
    return null;
  }


  /**
   * Gets the currently selected handle, or null.
   */
  public HandlePosition getSelectedHandle()
  {
    return selectedHandle;
  }


  /**
   * Sets the currently selected handle, or null.
   */
  public void setSelectedHandle(HandlePosition selectedHandle)
  {
    this.selectedHandle = selectedHandle;
  }
  
  
  /**
   * Sets the parent page of this frame.
   * If this frame is a child frame, an IllegalStateException
   * is thrown.
   */
  public void setParentPage(Page page)
  {
  	if (parentFrame != null)
  		throw new IllegalStateException("Child frames can not be moved on another page");
  	else
  		parentPage = page;
  }
  
  
  /**
   * Gets the parent page of this frame.
   * If this frame is a child frame, the parent page
   * of the parent's frame is returned.
   * If the parent page is unknown, null is returned.
   */
  public Page getParentPage()
  {
  	if (parentPage != null)
  		return parentPage;
  	else if (parentFrame != null)
  		return parentFrame.getParentPage();
  	else
  		return null;
  }
  
  
  public boolean mouseClicked(ScoreMouseEvent e)
	{
  	//TEST
		//App.getInstance().showMessageDialog("clicked: " + this);
		return false;
	}
	
	
	public boolean mousePressed(ScoreMouseEvent e)
	{
		//TEST
		//App.getInstance().showMessageDialog("pressed: " + this);
		return false;
	}


	public boolean mouseReleased(ScoreMouseEvent e)
	{
		//TEST
		//App.getInstance().showMessageDialog("released: " + this);
		return false;
	}
	
	
	public boolean mouseMoved(ScoreMouseEvent e)
	{
		//TEST
		//App.getInstance().showMessageDialog("moved: " + this);
		return false;
	}


	public boolean mouseDragged(ScoreMouseEvent e)
	{
		//TEST
		//App.getInstance().showMessageDialog("dragged: " + this);
		return false;
	}
  
  
  /**
   * Invoked when the mouse enters this frame.
   */
  public void mouseEntered(ScoreMouseEvent e)
  {
  	//TEST
  	//App.getInstance().showMessageDialog("entered: " + this);
  }
  
  
  /**
   * Invoked when the mouse exits this frame.
   */
  public void mouseExited(ScoreMouseEvent e)
  {
  	//TEST
  	//App.getInstance().showMessageDialog("exited: " + this);
  }
  

}
