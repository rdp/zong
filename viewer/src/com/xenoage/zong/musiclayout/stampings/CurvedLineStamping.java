package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.data.music.CurvedLine;
import com.xenoage.zong.data.music.format.SP;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.*;


/**
 * Class for a curved line stamping, that represents a slur
 * or a tie (both have the same shape).
 * 
 * A curved line has a horizontal start and end position in mm,
 * and a vertical start and end position as a line position.
 * 
 * Additionally, there are two control points to form
 * the slur/tie as a bezier curve, each for the
 * start and the end point. These are given as a horizontal
 * offset in mm and a vertical offset in line position relative
 * to the start and end point respectively.
 *
 * @author Andreas Wenger
 */
public final class CurvedLineStamping
  extends Stamping
{
  
  private final SP p1, p2;
  private final SP c1, c2;
  
  
  /**
   * Creates a new {@link CurvedLineStamping} belonging to the given staff.
   * @param parentStaff  the staff stamping this element belongs to
   * @param curvedLine   the musical element (a {@link Tie} or a {@link Slur})
   * @param p1           the start position (left)
   * @param p2           the end position (right)
   * @param c1           the offset of the first control point relative to the start point
   * @param c2           the offset of the second control point relative to the end point
   */
  public CurvedLineStamping(StaffStamping parentStaff, CurvedLine curvedLine, SP p1, SP p2, SP c1, SP c2)
  {
    super(parentStaff, LEVEL_MUSIC, curvedLine);
    this.p1 = p1;
    this.p2 = p2;
    this.c1 = c1;
    this.c2 = c2;
    //TODO: bounding shape
  }

  
  /**
   * Gets the start position (left).
   */
  public SP getPoint1()
  {
  	return p1;
  }
  
  
  /**
   * Gets the end position (left).
   */
  public SP getPoint2()
  {
  	return p2;
  }
  
  
  /**
   * Gets the offset of the first control point relative to the start point.
   */
  public SP getControl1()
  {
  	return c1;
  }
  
  
  /**
   * Gets the offset of the second control point relative to the end point.
   */
  public SP getControl2()
  {
  	return c2;
  }
  
  
  /**
   * Paints this stamping using the given rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    CurvedLineStampingRenderer.paint(this, params);
  }
  
  
}
