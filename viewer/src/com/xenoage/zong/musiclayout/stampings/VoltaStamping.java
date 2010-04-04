package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.core.music.volta.Volta;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.VoltaStampingRenderer;


/**
 * Class for a volta stamping belonging to a staff.
 * 
 * It has a vertical position (line position of the
 * horizontal volta line), and optionally a left downward
 * hook and a right downward hook. Optionally, there may
 * be a {@link FormattedText} left-aligned under the
 * volta line.
 * 
 * Many values are optional, since voltas can contain
 * line breaks, and e.g. a left hook and text is only used
 * in the first system, but not in the following ones.
 *
 * @author Andreas Wenger
 */
public final class VoltaStamping
	extends Stamping
{

	private final float linePosition;
	private final float x1, x2;
  private final FormattedText text;
  private final boolean leftHook, rightHook;
  
  
  /**
   * Creates a new {@link VoltaStamping} belonging to this staff.
   * @param volta           the volta element this stamping belongs to
   * @param parentStaff     the staff stamping this stamping belongs to
   * @param linePosition    the line position of the horizontal line
   * @param x1              the horizontal start position in mm relative to the beginning of the staff
   * @param x2              the horizontal end position in mm relative to the beginning of the staff
   * @param text            the caption of the volta (or null)
   * @param leftHook        true, if there is a left downward hook, else false
   * @param rightHook       true, if there is a right downward hook, else false
   */
  public VoltaStamping(
  	Volta volta, StaffStamping parentStaff, float linePosition, float x1, float x2,
    FormattedText text, boolean leftHook, boolean rightHook)
  {
    super(parentStaff, Stamping.LEVEL_MUSIC, volta);
    this.linePosition = linePosition;
    this.x1 = x1;
    this.x2 = x2;
    this.text = text;
    this.leftHook = leftHook;
    this.rightHook = rightHook;
    
    updateBoundingShape();
  }
  
  
  /**
   * Gets the line position of the horizontal line.
   */
  public float getLinePosition()
  {
  	return linePosition;
  }
  
  
  /**
   * Gets the horizontal start position of the horizontal line in mm.
   */
  public float getX1()
  {
  	return x1;
  }
  
  
  /**
   * Gets the horizontal end position of the horizontal line in mm.
   */
  public float getX2()
  {
  	return x2;
  }
  
  
  /**
   * Gets the caption of the volta, or null if there is none.
   */
  public FormattedText getText()
  {
  	return text;
  }
  

  /**
   * Updates the bounding rectangle.
   * This method must be called after creating an instance
   * of this class.
   */
  protected void updateBoundingShape()
  {
    //TODO
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    VoltaStampingRenderer.paint(this, params);
  }
  
  
  /**
	 * Returns true, if there is a downward hook on the left side, otherwise false.
	 */
	public boolean isLeftHook()
	{
		return leftHook;
	}
  
  
  /**
	 * Returns true, if there is a downward hook on the right side, otherwise false.
	 */
	public boolean isRightHook()
	{
		return rightHook;
	}
  
}
