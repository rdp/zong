package com.xenoage.zong.musiclayout.stampings;

import java.util.List;

import com.xenoage.zong.core.music.barline.Barline;
import com.xenoage.zong.core.music.barline.BarlineGroupStyle;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.*;


/**
 * Class for a barline stamping.
 * 
 * The stamping can be placed on a single
 * staff or a list of staves (the barlines are
 * connected).
 * 
 * A special case is a "Mensurstrich" barline,
 * which is placed between the staves, but
 * not on them.
 * 
 * At the moment a single stroke is used
 * for the barline.
 *
 * @author Andreas Wenger
 */
public final class BarlineStamping
  extends Stamping
{

	private final Barline barline;
  private final List<StaffStamping> staves;
  private final float xPosition;
  private final BarlineGroupStyle group;
  
  
  /**
   * Creates a new barline stamping.
   * @param barline    the musical element, including the repeat and line style
   * @param staves     the list of staves this barline is spanning
   * @param xPosition  the horizontal position in mm, relative to the parent frame
   * @param group      the grouping style of the barline
   */
  public BarlineStamping(Barline barline, List<StaffStamping> staves,
    float xPosition, BarlineGroupStyle group)
  {
    super(staves.get(0), LEVEL_MUSIC, barline);
    this.barline = barline;
    this.staves = staves;
    this.xPosition = xPosition;
    this.group = group;
    
    updateDrawingElements();
  }
  
  
  private void updateDrawingElements()
  {
    //TODO: bounding shape
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    BarlineStampingRenderer.paint(this, params);
  }
  
  
  public Barline getBarline()
  {
  	return barline;
  }
  
  
  public BarlineGroupStyle getBarlineGroupStyle()
  {
    return group;
  }
  
  
  public List<StaffStamping> getStaffLayoutElements()
  {
    return staves;
  }
  
  
  public float getPositionX()
  {
    return xPosition;
  }
  
  
}
