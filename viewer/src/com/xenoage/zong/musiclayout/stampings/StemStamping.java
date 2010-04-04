package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.*;


/**
 * Class for a stem stamping.
 * 
 * A stem has a notehead position and an end position
 * and is slightly thinner than a staff line.
 * 
 * TODO: bind to note stamping somehow?
 *
 * @author Andreas Wenger
 */
public class StemStamping
  extends Stamping
{
  
  private float positionX;
  private float noteheadLinePosition;
  //float, because it may be a fraction of interline space
  private float endLinePosition;  
  private int direction; //1: note on the top, -1: note on the bottom
  
  
  /**
   * Creates a new stem stamping belonging to the given staff.
   * @param parentStaff       the staff stamping this element belongs to
   * @param chord             the chord this stem belongs to
   * @param positionX         the horizontal position in mm
   * @param noteheadLinePosition  the start line position of the stem
   * @param endLinePosition   the end line position of the stem, e.g.
   *                          0 = bottom line, 3 = between 2nd and 3rd line, ...
   *                          also non-integer values are allowed here.
   * @param direction         1: note on the top, -1: note on the bottom
   *                          this value does not affect the drawing of the
   *                          element, but can be used by other elements
   *                          to find out the stem direction.
   */
  public StemStamping(StaffStamping parentStaff, Chord chord, float positionX,
    float noteheadLinePosition, float endLinePosition, int direction)
  {
    super(parentStaff, LEVEL_MUSIC, chord);
    this.positionX = positionX;
    this.noteheadLinePosition = noteheadLinePosition;
    this.endLinePosition = endLinePosition;
    this.direction = direction;
    
    //TODO: bounding shape: one at the end of the stem
    //(like in sibelius)?
  }
  
  
  /**
   * Gets the start line position of the stem.
   */
  public float getNoteheadLinePosition()
  {
    return noteheadLinePosition;
  }


  /**
   * Gets the vertical end position of the stem as
   * a line position.
   */
  public float getEndLinePosition()
  {
    return endLinePosition;
  }
  
  
  /**
   * Sets the vertical end position of the stem as
   * a line position.
   */
  public void setEndLinePosition(float endLinePosition)
  {
    this.endLinePosition = endLinePosition;
  }


  /**
   * Gets the horizontal position of the stem in mm.
   */
  public float getPositionX()
  {
    return positionX;
  }
  
  
  /**
   * Gets the direction of the stem:
   * 1: note on the top, -1: note on the bottom
   */
  public int getDirection()
  {
    return direction;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    StemStampingRenderer.paint(this, params);
  }
  
}
