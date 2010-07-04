package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.LegerLineStampingRenderer;


/**
 * Class for a leger line stamping.
 * 
 * Leger lines belong to a staff. They have
 * a horizontal position around which they
 * are centered. They are 2 spaces long.
 *
 * @author Andreas Wenger
 */
public final class LegerLineStamping
  extends Stamping
{

  private final float positionX;
  private final int linePosition;
  
  
  /**
   * Creates a new leger line stamping belonging to the given staff.
   * @param parentStaff     the staff stamping this element belongs to
   * @param chord           the chord this leger line belongs to
   * @param positionX       the horizontal position in mm
   * @param linePosition    the line the leger line sits on. 0 = bottom line,
   *                        3 = between 2nd and 3rd line, ..
   */
  public LegerLineStamping(StaffStamping parentStaff, Chord chord,
    float positionX, int linePosition)
  {
    super(parentStaff, Level.Music, chord, null);
    this.positionX = positionX;
    this.linePosition = linePosition;
  }
  
  
  /**
   * Gets the line the leger line sits on. 0 = bottom line,
   * 3 = between 2nd and 3rd line, ..
   */
  public float getLinePosition()
  {
  	return linePosition;
  }
  
  
  /**
   * Gets the horizontal position of this leger line in mm.
   */
  public float getPositionX()
  {
  	return positionX;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    LegerLineStampingRenderer.paint(this, params);
  }
  
}
