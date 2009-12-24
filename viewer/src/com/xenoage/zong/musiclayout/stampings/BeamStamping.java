package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.data.music.Beam;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.*;


/**
 * Class for a beam stamping.
 * 
 * A beam has a start and end position in mm and two positions for the
 * end points. The two end points can be on different staves or on
 * the same staff.
 *
 * @author Andreas Wenger
 */
public final class BeamStamping
  extends Stamping
{
  
  //TODO: move into other class
  public static final float beamHeight = 0.5f; //height of beam in interline spaces
  public static final float beamGap = 0.3f; //gap between lines in interline spaces
  
  private final StaffStamping staff1, staff2;
  private final float x1, x2;
  private final float lp1, lp2;
  
  
  /**
   * Creates a new beam stamping belonging to the given staff.
   * @param beam         the musical element
   * @param staff1       the staff stamping of the start position
   * @param staff2       the staff stamping of the end position
   * @param x1           the horizontal start position (left) in mm
   * @param x2           the horizontal end position (right) in mm
   * @param lp1          the end line position at the start position (centered)
   * @param lp2          the end line position at the end position (centered)
   */
  public BeamStamping(Beam beam, StaffStamping staff1, StaffStamping staff2, 
    float x1, float x2, float lp1, float lp2)
  {
    super(staff1, LEVEL_MUSIC, beam);
    this.staff1 = staff1;
    this.staff2 = staff2;
    this.x1 = x1;
    this.x2 = x2;
    this.lp1 = lp1;
    this.lp2 = lp2;
    //TODO: bounding shape: (only for the outermost stem?)
  }
  
  
  /**
   * Gets the {@link StaffStamping} of the start position.
   */
  public StaffStamping getStaffStamping1()
  {
  	return staff1;
  }
  
  
  /**
   * Gets the {@link StaffStamping} of the end position.
   */
  public StaffStamping getStaffStamping2()
  {
  	return staff2;
  }
  
  
  /**
   * Gets the horizontal start coordinate of the beam in mm.
   */
  public float getX1()
  {
  	return x1;
  }
  
  
  /**
   * Gets the horizontal end coordinate of the beam in mm.
   */
  public float getX2()
  {
  	return x2;
  }
  
  
  /**
   * Gets the end line position at the start position.
   */
  public float getLP1()
  {
  	return lp1;
  }
  
  
  /**
   * Gets the end line position at the end position.
   */
  public float getLP2()
  {
  	return lp2;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    BeamStampingRenderer.paint(this, params);
  }
  
  
}
