package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.*;


/**
 * Class for a tuplet stamping.
 * 
 * A tuplet stamping consists of a number painted above or below
 * the chords that form the tuplet, and optionally a bracket.
 *
 * @author Andreas Wenger
 */
public final class TupletStamping
  extends Stamping
{

  private final float x1mm, x2mm;
  private final float y1lp, y2lp;
  private final boolean bracket;
  private final FormattedText text;
  
  
  /**
   * Creates a new {@link TupletStamping}.
   * @param x1mm     the horizontal start position in mm
   * @param x2mm     the horizontal end position in mm
   * @param y1lp     the vertical start position as a LP
   * @param y1lp     the vertical end position as a LP
   * @param bracket  true, if a bracket should be drawn, otherwise false
   * @param text     the text in the middle of the tuplet bracket, or null
   */
  public TupletStamping(float x1, float x2, float lp1, float lp2, boolean bracket, FormattedText text,
  	StaffStamping parentStaff)
  {
  	super(parentStaff, Level.Music, null, null);
  	this.x1mm = x1;
  	this.x2mm = x2;
  	this.y1lp = lp1;
  	this.y2lp = lp2;
  	this.bracket = bracket;
  	this.text = text;
  }
  
  
	public float getX1mm()
	{
		return x1mm;
	}

	
	public float getX2mm()
	{
		return x2mm;
	}
	
	
	public float getY1lp()
	{
		return y1lp;
	}

	
	public float getY2lp()
	{
		return y2lp;
	}

	
	public boolean isBracket()
	{
		return bracket;
	}

	
	public FormattedText getText()
	{
		return text;
	}


	/**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    TupletStampingRenderer.paint(this, params);
  }
  
}
