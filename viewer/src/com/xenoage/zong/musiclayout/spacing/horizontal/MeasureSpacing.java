package com.xenoage.zong.musiclayout.spacing.horizontal;

import com.xenoage.zong.core.music.MP;


/**
 * This class contains the spacing
 * of a measure of a single staff.
 * 
 * @author Andreas Wenger
 */
public final class MeasureSpacing
{
  
	private final MP mp;
	private final VoiceSpacing[] voiceSpacings;
  private final MeasureLeadingSpacing leadingSpacing;
  

  /**
   * Creates an empty measure spacing for one staff.
   * @param mp              the musical position of the measure
   * @param voiceSpacings   the list of voices
   * @param leadingSpacing  the elements at the beginning (initial clef, key signature, ...), or null
   */
  public MeasureSpacing(MP mp, VoiceSpacing[] voiceSpacings,
  	MeasureLeadingSpacing leadingSpacing)
  {
  	this.mp = mp;
    this.voiceSpacings = voiceSpacings;
    this.leadingSpacing = leadingSpacing;
  }
  
  
  /**
   * Gets the musical position of the measure.
   */
  public MP getMP()
  {
  	return mp;
  }
  
  
  /**
   * Gets the leading spacing.
   */
  public MeasureLeadingSpacing getLeadingSpacing()
  {
    return leadingSpacing;
  }
  
  
  /**
   * Gets the number of voices.
   */
  public int getVoicesCount()
  {
    return voiceSpacings.length;
  }


	/**
	 * Gets the spacing of the given voice.
	 */
	public VoiceSpacing getVoice(int index)
	{
	  return voiceSpacings[index];
	}
  
  
}
