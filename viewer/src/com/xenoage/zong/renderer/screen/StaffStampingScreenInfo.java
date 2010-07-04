package com.xenoage.zong.renderer.screen;

import java.awt.Color;

import com.xenoage.zong.musiclayout.stampings.StaffStamping;



/**
 * This class contains some information that is
 * useful to draw a staff on the screen.
 * 
 * It could be recomputed in each rendering
 * step, but caching this information saves
 * much time.
 * 
 * @author Andreas Wenger
 */
public final class StaffStampingScreenInfo
{
	
	private final StaffStamping parentStaff;
	
	//cache
	private ScreenStaff screenStaff = null;
  private float lastScreenStaffScaling;
  private ScreenLine screenLine = null;
  private float lastScreenLineScaling;
  
  
  /**
   * Creates a cache for screen display information.
   */
  public StaffStampingScreenInfo(StaffStamping parentStaff)
  {
  	this.parentStaff = parentStaff;
  }
  
  
  /**
   * Gets the ScreenStaff instance
   * (for nice rendering) for the given scaling.
   * When the color or width was changed, set screenLine to
   * null before and pass the width and color parameters.
   */
  public ScreenLine getScreenLine(float scaling, float widthMm, Color color)
  {
    if (screenLine == null || scaling != lastScreenLineScaling)
    {
      screenLine = new ScreenLine(widthMm, color, scaling);
      lastScreenLineScaling = scaling;
    }
    return screenLine;
  }
  
  
  /**
   * For screen display: Gets the ScreenLine instance
   * (for nice rendering) for the given scaling.
   */
  public ScreenStaff getScreenStaff(float scaling)
  {
    if (screenStaff == null || scaling != lastScreenStaffScaling)
    {
      screenStaff = new ScreenStaff(parentStaff.getLinesCount(), parentStaff.getInterlineSpace(),
      	parentStaff.getLineWidth() / parentStaff.getInterlineSpace(), scaling);
      lastScreenStaffScaling = scaling;
    }
    return screenStaff;
  }
  

}
