package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.SystemCursorStampingRenderer;


/**
 * Class for a cursor stamping that belongs to a consecutive
 * range of staves (often the whole system).
 *
 * @author Andreas Wenger
 */
public final class SystemCursorStamping
  extends Stamping
{

	private final StaffStamping topStaff;
	private final StaffStamping bottomStaff;
  private final float positionX;
  
  
  /**
   * Creates a new {@link SystemCursorStamping} between the given staves.
   * @param topStaff      the top staff stamping, where the cursor begins
   * @param bottomStaff   the bottom staff stamping, where the cursor ends
   * @param positionX     the horizontal position of the cursor, relative to the left side of the top staff
   */
  public SystemCursorStamping(StaffStamping topStaff, StaffStamping bottomStaff, float positionX)
  {
    super(topStaff, Level.EmptySpace, null, null);
    this.topStaff = topStaff;
    this.bottomStaff = bottomStaff;
    this.positionX = positionX;
  }
  
  
  /**
   * Gets the horizontal position of the cursor, relative to the left side of the staff.
   */
  public float getPositionX()
  {
  	return positionX;
  }
  
  
  public StaffStamping getTopStaff()
  {
  	return topStaff;
  }
  
  
  public StaffStamping getBottomStaff()
  {
  	return bottomStaff;
  }
  
  
  /**
   * Paints this stamping using the given rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    SystemCursorStampingRenderer.paint(this, params);
  }
  
}
