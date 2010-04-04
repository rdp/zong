package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.format.SP;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.StaffTextStampingRenderer;


/**
 * Class for a text stamping belonging to a staff, e.g. for lyric
 * and directions.
 *
 * @author Andreas Wenger
 */
public final class StaffTextStamping
	extends Stamping
{

  private final FormattedText text;
  private final SP position;
  
  
  /**
   * Creates a new {@link StaffTextStamping} belonging to the given staff.
   * @param musicElement    the musical element for which this stamping was created, or null
   * @param text            the formatted text to draw
   * @param parentStaff     the staff stamping this element belongs to
   * @param position        the position, relative to the left border of the score frame
   */
  public StaffTextStamping(
    StaffStamping parentStaff, MusicElement musicElement, FormattedText text, SP position)
  {
    super(parentStaff, Stamping.LEVEL_MUSIC, musicElement);
    this.text = text;
    this.position = position;
    updateBoundingShape();
  }
  
  
  /**
   * Gets the formatted text.
   */
  public FormattedText getText()
  {
  	return text;
  }
  
  
  /**
   * Gets the position where this text is placed.
   */
  public SP getPosition()
  {
  	return position;
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
    StaffTextStampingRenderer.paint(text, position, parentStaff, params);
  }
  
}
