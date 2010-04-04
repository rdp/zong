package com.xenoage.zong.musiclayout.spacing.horizontal;

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
  private final SpacingElement[] spacingElements;
  
  
  /**
   * Creates a {@link MeasurevoiceSpacing} for the given {@link Voice},
   * using the given {@link SpacingElement}s.
   */
  public VoiceSpacing(Voice voice, float interlineSpace, SpacingElement[] spacingElements)
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
  
  
  public float getInterlineSpace()
  {
  	return interlineSpace;
  }
  
  
  /**
   * Gets the {@link SpacingElement}s of this voice.
   * 
   * TIDY: don't return array, return a element by given index
   */
  public SpacingElement[] getSpacingElements()
  {
    return spacingElements;
  }
  

}
