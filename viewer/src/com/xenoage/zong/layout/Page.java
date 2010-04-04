package com.xenoage.zong.layout;

import java.util.ArrayList;

import com.xenoage.util.iterators.It;
import com.xenoage.util.math.Point2f;
import com.xenoage.zong.core.format.PageFormat;
import com.xenoage.zong.data.event.ScoreChangedEvent;
import com.xenoage.zong.layout.frames.FrameHandle;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.FramePosition;
import com.xenoage.zong.layout.frames.ScoreFrame;


/**
 * One page within a page layout.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class Page
{
  
  private PageFormat format;
  private Layout parentLayout;
  private ArrayList<Frame> frames = new ArrayList<Frame>();
  
  
  /**
   * Creates a new empty page.
   * @param pageFormat    the format of the new page
   * @param parentLayout  the layout this page belongs to
   */
  Page(PageFormat format, Layout parentLayout)
  {
    this.parentLayout = parentLayout;
    if (format == null)
      throw new IllegalArgumentException("Page format must be given.");
    this.format = format;
  }
  
  
  /**
   * Gets the format of this page.
   */
  public PageFormat getFormat()
  {
    return format;
  }
  
  
  /**
   * Sets the format of this page.
   */
  public void setFormat(PageFormat format)
  {
    this.format = format;
    //TODO: move all frames, that are outside of the page,
    //into the page?
    
    //tell the parent layout that page format has changed
    this.parentLayout.pageFormatChanged(this);
  }
  
  
  /**
   * Adds the given frame, if it is not already on the page.
   */
  public void addFrame(Frame frame)
  {
    if (!frames.contains(frame))
    {
    	frame.setParentPage(this);
      frames.add(frame);
    }
  }
  
  
  /**
   * Removes the given frame from the page.
   */
  public void removeFrame(Frame frame)
  {
  	if (frames.contains(frame))
  	{
  		frames.remove(frame);
  		frame.setParentPage(null);
  	}
  }
  
  
  /**
   * Gets the frames on this page.
   */
  public It<Frame> getFrames()
  {
    return new It<Frame>(frames);
  }
  
  
  /**
   * Transforms the given coordinates in page space to
   * a frame position.
   * If there is no frame, null is returned.
   */
  public FramePosition computeFramePosition(Point2f p)
  {
    //since frames are painted in forward direction,
    //the last one is the highest one. so we have to
    //check for clicks in reverse order.
    for (int i = frames.size() - 1; i >= 0; i--)
    {
      FramePosition fp = frames.get(i).computeFramePosition(p);
      if (fp != null)
      {
        return fp;
      }
    }
    return null;
  }
  
  
  /**
   * This method is called when the given score has been changed.
   */
  public void scoreChanged(ScoreChangedEvent event)
  {
    for (Frame frame : frames)
    {
      if (frame instanceof ScoreFrame)
      {
        ScoreFrame scoreFrame = (ScoreFrame) frame;
        if (scoreFrame.isLeading())
        {
        	scoreFrame.updateLayout(event.getMeasures());
        }
      }
    }
  }
  
  
  /**
   * Update the layout of all score frames that have
   * a changed-flag.
   */
  public void updateScoreFrames()
  {
    for (Frame frame : frames)
    {
      if (frame instanceof ScoreFrame)
      {
        ScoreFrame scoreFrame = (ScoreFrame) frame;
        scoreFrame.updateLayout();
      }
    }
  }
  
  
  public Layout getParentLayout()
  {
	  return parentLayout;
  }
  
  
  /**
   * Computes the adjustment handle at the given position
   * and returns it. If there is none, null is returned.
   * @param position    the position where to look for a handle
   * @param scaling     the current scaling factor
   */
  public FrameHandle computeFrameHandleAt(Point2f layoutPosition, float scaling)
  {
    //test each frame, but in reverse order, because the last frame
    //is drawn on tip
    for (int i = frames.size() - 1; i >= 0; i--)
    {
      FrameHandle fh = frames.get(i).computeFrameHandleAt(layoutPosition, scaling);
      if (fh != null)
      {
        return fh;
      }
    }
    return null;
  }
  
  
}
