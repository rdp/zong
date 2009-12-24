package com.xenoage.zong.musiclayout;

import com.xenoage.util.math.Point2f;


/**
 * This class contains a position within
 * a score layout, not in musical sense but
 * in metric coordinates: frame index
 * and coordinates relative to the
 * upper left corner in mm.
 * 
 * @author Andreas Wenger
 */
public class ScoreLayoutPosition
{
  
  private int frameIndex;
  private float x;
  private float y;
  
  
  public ScoreLayoutPosition(int frameIndex, float x, float y)
  {
    this.frameIndex = frameIndex;
    this.x = x;
    this.y = y;
  }

  
  public int getFrameIndex()
  {
    return frameIndex;
  }
  
  
  public Point2f getPosition()
  {
    return new Point2f(x, y);
  }

  
  public float getX()
  {
    return x;
  }

  
  public float getY()
  {
    return y;
  }
  

}
