package com.xenoage.zong.musiclayout.spacing.horizontal;

import com.xenoage.pdlib.PVector;
import com.xenoage.zong.core.music.Voice;



/**
 * This class contains the spacing of one voice
 * of a measure of a single staff.
 *
 * @author Andreas Wenger
 */
public final class VoiceSpacing
{
  
	private final Voice voice;
	private final float interlineSpace;
  private final PVector<SpacingElement> spacingElements;
  
  
  /**
   * Creates a {@link MeasurevoiceSpacing} for the given {@link Voice},
   * using the given {@link SpacingElement}s.
   */
  public VoiceSpacing(Voice voice, float interlineSpace, PVector<SpacingElement> spacingElements)
  {
  	this.voice = voice;
  	this.interlineSpace = interlineSpace;
    this.spacingElements = spacingElements;
  }
  
  
  /**
   * Gets the {@link Voice} this spacing belongs to.
   */
  public Voice getVoice()
  {
  	return voice;
  }
  
  
  /**
   * Gets the interline space in mm of this voice.
   */
  public float getInterlineSpace()
  {
  	return interlineSpace;
  }
  
  
  /**
   * Gets the {@link SpacingElement}s of this voice.
   */
  public PVector<SpacingElement> getSpacingElements()
  {
    return spacingElements;
  }
  

}
