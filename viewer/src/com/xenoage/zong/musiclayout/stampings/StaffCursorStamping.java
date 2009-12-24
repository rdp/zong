package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.StaffCursorStampingRenderer;


/**
 * Class for a cursor stamping
 * that belongs to one staff.
 *
 * @author Andreas Wenger
 */
public class StaffCursorStamping
  extends Stamping
{

  private float positionX;
  private float offsetSpaces;
  
  
  /**
   * Creates a new {@link StaffCursorStamping} belonging to the given staff.
   * @param parentStaff   the staff drawing element, where the cursor is placed
   * @param positionX     the horizontal position of the cursor, relative to the left side of the staff
   * @param offsetSpaces  an additional offset for the cursor in interline spaces
   */
  public StaffCursorStamping(StaffStamping parentStaff, float positionX, float offsetSpaces)
  {
    super(parentStaff, LEVEL_EMPTYSPACE, null);
    this.positionX = positionX;
    this.offsetSpaces = offsetSpaces;
  }
  
  
  /**
   * Gets the horizontal position of the cursor, relative to the left side of the staff.
   */
  public float getPositionX()
  {
  	return positionX;
  }
  
  
  /**
   * Gets the additional offset for the cursor in interline spaces.
   */
  public float getOffsetSpaces()
  {
  	return offsetSpaces;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    StaffCursorStampingRenderer.paint(this, params);
  }
  
}
