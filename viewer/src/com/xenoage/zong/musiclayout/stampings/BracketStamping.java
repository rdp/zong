package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.core.music.bracket.BracketGroupStyle;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.*;


/**
 * Class for a bracket stamping.
 * 
 * This element groups one or more adjacent
 * staves with a brace or a square bracket
 * at the very beginning of the system.
 *
 * @author Andreas Wenger
 */
public class BracketStamping
  extends Stamping
{

  private StaffStamping firstStaff;
  private StaffStamping lastStaff;
  private float positionX;
  private BracketGroupStyle style;
  
  
  /**
   * Creates a new bracket stamping between the given two staves
   * (they may also be the same one) with the given style.
   * @param firstStaff      the topmost staff of the group
   * @param lastStaff       the lowest staff of the group
   * @param positionX       the horizontal position in mm
   * @param style           the style of the bracket
   */
  public BracketStamping(StaffStamping firstStaff,
  	StaffStamping lastStaff,
    float positionX, BracketGroupStyle style)
  {
    super(firstStaff, LEVEL_MUSIC, null);
    this.firstStaff = firstStaff;
    this.lastStaff = lastStaff;
    this.positionX = positionX;
    this.style = style;
    
    //TODO: bounding shape
  }
  
  
  /**
   * Gets the first staff stamping this
   * bracket embraces.
   */
  public StaffStamping getFirstStaff()
  {
  	return firstStaff;
  }
  
  
  /**
   * Gets the last staff stamping this
   * bracket embraces.
   */
  public StaffStamping getLastStaff()
  {
  	return lastStaff;
  }
  
  
  /**
   * Gets the horizontal position of the bracket in mm.
   */
  public float getPositionX()
  {
    return positionX;
  }
  
  
  /**
   * Gets the style of the bracket.
   */
  public BracketGroupStyle getStyle()
  {
  	return style;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    BracketStampingRenderer.paint(this, params);
  }
  
}
