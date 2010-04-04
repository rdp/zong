package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.core.music.direction.Wedge;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.WedgeStampingRenderer;


/**
 * Class for a wedge (crescendo or decrescendo) stamping
 * belonging to a staff.
 * 
 * It has a vertical position (line position of the
 * center baseline of the wedge), a horizontal start
 * and end position in mm and the vertical distance at these
 * points in interline spaces.
 *
 * @author Andreas Wenger
 */
public final class WedgeStamping
	extends Stamping
{

	private final float yLp;
	private final float x1Mm, x2Mm;
  private final float d1Is, d2Is;
  
  
  /**
   * Creates a new {@link WedgeStamping} belonging to the given staff.
   * @param wedge        the wedge element this stamping belongs to
   * @param yLp          the line position of the (centered) baseline
   * @param x1Mm         the horizontal start position in mm relative to the beginning of the staff
   * @param x2Mm         the horizontal end position in mm relative to the beginning of the staff
   * @param d1Is         the vertical distance of the lines at the start position in IS
   * @param d2Is         the vertical distance of the lines at the end position in IS
   * @param parentStaff  the staff stamping this stamping belongs to
   */
  public WedgeStamping(Wedge wedge, float yLp, float x1Mm, float x2Mm, float d1Is, float d2Is,
  	StaffStamping parentStaff)
  {
    super(parentStaff, Stamping.LEVEL_MUSIC, wedge);
    this.yLp = yLp;
    this.x1Mm = x1Mm;
    this.x2Mm = x2Mm;
    this.d1Is = d1Is;
    this.d2Is = d2Is;
    //TODO: updateBoundingShape();
  }
  
  
	public float getYLp()
	{
		return yLp;
	}

	
	public float getX1Mm()
	{
		return x1Mm;
	}

	
	public float getX2Mm()
	{
		return x2Mm;
	}

	
	public float getD1Is()
	{
		return d1Is;
	}

	
	public float getD2Is()
	{
		return d2Is;
	}


	/**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    WedgeStampingRenderer.paint(this, params);
  }
  
  
}
