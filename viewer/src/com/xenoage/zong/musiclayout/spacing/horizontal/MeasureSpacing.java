package com.xenoage.zong.musiclayout.spacing.horizontal;

import com.xenoage.zong.data.music.Measure;



/**
 * This class contains the spacing
 * of a measure of a single staff.
 * 
 * @author Andreas Wenger
 */
public final class MeasureSpacing
{
  
	private final Measure measure;
	private final VoiceSpacing[] voiceSpacings;
  private final MeasureLeadingSpacing leadingSpacing;
  

  /**
   * Creates an empty measure spacing for one staff.
   * @param measure         the musical data of the measure
   * @param voiceSpacings   the list of voices
   * @param leadingSpacing  the elements at the beginning (initial clef, key signature, ...), or null
   */
  public MeasureSpacing(Measure measure, VoiceSpacing[] voiceSpacings,
  	MeasureLeadingSpacing leadingSpacing)
  {
  	this.measure = measure;
    this.voiceSpacings = voiceSpacings;
    this.leadingSpacing = leadingSpacing;
  }
  
  
  /**
   * Gets the musical data of the measure.
   */
  public Measure getMeasure()
  {
  	return measure;
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
