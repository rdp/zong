package com.xenoage.zong.layout.frames;


/**
 * Adjustment handle of a frame.
 * 
 * @author Andreas Wenger
 */
public class FrameHandle
{
  
  public static int SIZE_IN_PX = 20;
  public static int DISTANCE_ROTATIONHANDLE_IN_PX = 40;
  
  
  /**
   * Position of the handle.
   */
  public enum HandlePosition
  {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW,
    Rotation,
    Move;
  }
  
  
  private Frame frame;
  private HandlePosition position;
  
  
  public FrameHandle(Frame frame, HandlePosition position)
  {
    this.frame = frame;
    this.position = position;
  }


  public Frame getFrame()
  {
    return frame;
  }

  
  public HandlePosition getPosition()
  {
    return position;
  }
  
  
  /**
   * Returns true, if the given object is a frame handle that belongs
   * to the same frame and handle position as this frame handle,
   * otherwise false.
   */
  @Override public boolean equals(Object o)
  {
    if (o instanceof FrameHandle)
    {
      FrameHandle h = (FrameHandle) o;
      return (this.frame == h.frame && this.position == h.position);
    }
    else
    {
      return false;
    }
  }

  
}
