package com.xenoage.zong.musiclayout.spacing.horizontal;

import static com.xenoage.pdlib.PVector.pvec;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.SortedList;
import com.xenoage.util.math.Fraction;
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
	private final PVector<VoiceSpacing> voiceSpacings;
  private final LeadingSpacing leadingSpacing;
  
  //cache
  private final PVector<Fraction> usedBeats;
  

  /**
   * Creates an empty measure spacing for one staff.
   * @param mp              the musical position of the measure
   * @param voiceSpacings   the list of voices
   * @param leadingSpacing  the elements at the beginning (initial clef, key signature, ...), or null
   */
  public MeasureSpacing(MP mp, PVector<VoiceSpacing> voiceSpacings,
  	LeadingSpacing leadingSpacing)
  {
  	this.mp = mp;
    this.voiceSpacings = voiceSpacings;
    this.leadingSpacing = leadingSpacing;
    
    //compute the list of all used beats
    SortedList<Fraction> usedBeats = new SortedList<Fraction>(false);
    for (VoiceSpacing vs : voiceSpacings)
    {
    	for (SpacingElement se : vs.getSpacingElements())
    	{
    		usedBeats.add(se.getBeat());
    	}
    }
    this.usedBeats = pvec(usedBeats.getLinkedList());
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
  public LeadingSpacing getLeadingSpacing()
  {
    return leadingSpacing;
  }


	/**
	 * Gets the voice spacings.
	 */
	public PVector<VoiceSpacing> getVoiceSpacings()
	{
	  return voiceSpacings;
	}
	
	
	/**
	 * Gets a sorted list of all used beats in this measure.
	 */
	public PVector<Fraction> getUsedBeats()
	{
		return usedBeats;
	}
  
  
}
