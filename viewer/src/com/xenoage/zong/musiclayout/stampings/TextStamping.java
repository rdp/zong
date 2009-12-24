package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.TextStampingRenderer;


/**
 * Class for a text stamping, e.g. a part name.
 * 
 * If the text belongs to a certain staff, use the
 * {@link StaffTextStamping} class instead.
 *
 * @author Andreas Wenger
 */
public final class TextStamping
	extends Stamping
{

  private final FormattedText text;
  private final Point2f position;
  
  
  /**
   * Creates a new {@link TextStamping}.
   * @param text            the formatted text to draw
   * @param position        the position, relative to the left border of the score frame
   */
  public TextStamping(FormattedText text, Point2f position)
  {
    super(Stamping.LEVEL_TEXT, null);
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
   * Gets the position where this text is placed,
   * relative to the top left corner of the score frame.
   */
  public Point2f getPosition()
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
    TextStampingRenderer.paint(text, position, params);
  }
  
}
