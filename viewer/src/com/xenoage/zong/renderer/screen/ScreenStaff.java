package com.xenoage.zong.renderer.screen;

import com.xenoage.zong.musiclayout.stampings.StaffStamping;
import com.xenoage.util.Units;


/**
 * The ScreenStaff class helps drawing nice
 * staves on the screen.
 * 
 * Because the screen works with integer
 * coordinates, a staff could look very ugly,
 * when it uses for example the interline spaces
 * 2-1-2-1-2. Just view a score in Adobe Reader 7
 * for example and the need of a better display
 * is obvious.
 * 
 * This class computes the best possible
 * display of staves on the screen: It ensures
 * that each interline space has the same size.
 * Unfortunately this also means that the displayed
 * size is not really the actual one, but an
 * approximation. But it's really much better
 * than just rounding to integer interline spaces.
 * 
 * When the staff is too small, a rectangle should be drawn
 * (called a "simplified staff").
 * In this case the interline space if a float value
 * and the height of the staff is just rounded
 * to the next integer value.
 *
 * @author Andreas Wenger
 */
public class ScreenStaff
{
  
	private final boolean simplifiedStaff;
  private final int yOffsetPx;
  private final int heightPx;
  private final float lineHeightPxFloat;
  private final float heightScaling;
  private final float interlineSpacePx;
  
  //cache for performance
  private final int lp0Px; 
  
  
  /**
   * Creates a {@link ScreenStaff} with the given number of lines,
   * interline space, line width (in interline spaces) and the given scaling.
   */
  public ScreenStaff(int lines, float interlineSpace, float lineWidth, float scaling)
  {
    float interlineSpacePxFloat = Units.mmToPxFloat(interlineSpace, scaling);
    float normalHeightPxFloat = (lines - 1) * interlineSpacePxFloat;
    //height of a line (may be smaller than 1)
    this.lineHeightPxFloat = lineWidth * interlineSpacePxFloat;
    //simplified staff?
    this.simplifiedStaff = (interlineSpacePxFloat < 2);
    if (this.simplifiedStaff)
    {
      //no place for lines, the staff is displayed too small
      //just round the height to the next integer value
      //explanation: Lines are only useful, if there is (at least) 1-pixel-line, then
      //a 1-pixel space, and so on (= (lines - 1) * 2
      //if the normalHeightPxFloat (because of the
      //above explanation) is smaller than this value, there can be no
      //useful display with lines, but a rectangle should be drawn.
      //for its height we can use any integer value.
      this.heightPx = Math.round(normalHeightPxFloat);
      //no vertical offset is needed
      this.yOffsetPx = 0;
      //interline space: < 1
      this.interlineSpacePx = interlineSpacePxFloat;
    }
    else
    {
      //ensure equal interline spaces
      this.heightPx = Math.round(interlineSpacePxFloat) * (lines - 1);
      //compute vertical offset. if for example the staff is 4 px "too high",
      //move it 2 px up (-2). Thanks for the idea to Andreas Schmid.
      this.yOffsetPx = Math.round((normalHeightPxFloat - this.heightPx) / 2);
      //interline space
      this.interlineSpacePx = Math.round(interlineSpacePxFloat);
    }
    //height scaling factor
    this.heightScaling = ((float) this.heightPx) / normalHeightPxFloat;
    //cache
    this.lp0Px = yOffsetPx + heightPx;
  }
  
  
  /**
   * Returns true, if a simplified staff (just a filled rectangle)
   * should be drawn.
   */
  public boolean isSimplifiedStaff()
  {
  	return simplifiedStaff;
  }
  
  
  /**
   * Gets the height of the staff in px, that fits best to the screen.
   * The half top line and half bottom line are, as always in a "staff height",
   * not included!
   */
  public int getHeightPx()
  {
    return heightPx;
  }
  
  
  /**
   * Gets the vertical position in pixel, relative to the normal y-position
   * of the {@link StaffStamping}, of line position 0 (bottom line).
   * This is the y-offset plus the height in pixel plus the (rounded up) half
   * line width in px. 
   */
  public int getLP0Px()
  {
    return lp0Px;
  }
  
  
  /**
   * Gets the vertical position in pixel, relative to the normal y-position
   * of the {@link StaffStamping}, of the given line position.
   * This is the y-offset plus the height in pixel plus the (rounded up) half
   * line width in px minus half the given line position times the interline space. 
   */
  public int getLPPx(float lp)
  {
    return Math.round(lp0Px - lp / 2 * interlineSpacePx);
  }
  
  
  /**
   * Gets the corrected vertical position in pixel, relative to the normal y-position
   * of the {@link StaffStamping}, of the given line position, as a float value.
   * 
   * Corrected means: Everything gets drawn 0.5 pixel to low (because if there is a 1 px line,
   * it can not be drawn "between two screen pixels" - the lower one is taken),
   * so correct the bottom line position is 0.5 px lower.
   * 
   * Call this method in favor of <code>getLPPx</code>, if float coordinates are used.
   */
  public float getLPPxCorrected(float lp)
  {
    return getLPPx(lp) + 0.5f;
  }
  
  
  /**
   * Gets the height of a line in px (as a float value).
   * This value may be less than 1. If you need the displayed height
   * of the line, use the {@link ScreenLine} class.
   */
  public float getLineHeightPx()
  {
  	return lineHeightPxFloat;
  }
  
  
  /**
   * Gets the vertical offset of the staff in px,
   * that fits best to the screen.
   */
  public int getYOffsetPx()
  {
    return yOffsetPx;
  }
  
  
  /**
   * Gets the height scaling factor of the staff in px,
   * that fits best to the screen.
   */
  public float getHeightScaling()
  {
    return heightScaling;
  }
  
  
  /**
   * Gets the interline space of the staff in px,
   * that fits best to the screen.
   * Although it is a float, in most cases it will have
   * an integer value. Only when the staff has to be drawn
   * simplified, it will have a float value to allow
   * computations with higher precision.
   */
  public float getInterlineSpacePx()
  {
    return interlineSpacePx;
  }

}
