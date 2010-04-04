package com.xenoage.zong.core.music;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.zong.core.music.util.BeatInterval.AtOrAfter;
import static com.xenoage.zong.core.music.util.BeatInterval.BeforeOrAt;
import static com.xenoage.zong.core.music.util.VoiceElementSelection.Last;
import static com.xenoage.zong.core.music.util.VoiceElementSide.Start;
import static com.xenoage.zong.core.music.util.VoiceElementSide.Stop;

import java.util.ArrayList;

import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.util.BeatE;
import com.xenoage.zong.core.music.util.MPE;
import com.xenoage.zong.util.exceptions.IllegalMPException;


/**
 * Staff of any size.
 * 
 * A vocal staff that is visible throughout the whole
 * score is an instance of this class as well
 * as a small ossia staff that is only displayed
 * over a single measure.
 * 
 * Staves are divided into measures.
 *
 * @author Andreas Wenger
 */
public final class Staff
{
	
  //the measures from the beginning to the ending
  //of the score (even the invisible ones)
  private final PVector<Measure> measures;
  
  //number of lines in this staff
  private final int linesCount;
  
  //interline space in this staff, or null for default
  private final Float interlineSpace;
  
  
  /**
   * Creates a new {@link Staff}.
   */
  public Staff(PVector<Measure> measures, int linesCount, Float interlineSpace)
  {
  	if (measures.size() == 0)
  		throw new IllegalArgumentException("A staff must have at least one measure");
    this.measures = measures;
    this.linesCount = linesCount;
    this.interlineSpace = interlineSpace;
  }
  
  
  /**
   * Creates a new {@link Staff} with the given number of empty measures.
   */
  public Staff(int measuresCount, int linesCount, Float interlineSpace)
  {
  	ArrayList<Measure> measures = new ArrayList<Measure>(measuresCount);
  	for (int i = 0; i < measuresCount; i++)
  		measures.add(Measure.createMinimal());
  	this.measures = pvec(measures);
    this.linesCount = linesCount;
    this.interlineSpace = interlineSpace;
  }
  
  
  /**
   * Creates a minimal staff with no content.
   */
  public static Staff createMinimal()
  {
  	return new Staff(0, 5, null);
  }
  
  
  /**
   * Adds the given number of empty measures at the end
   * of the staff.
   */
  public Staff plusEmptyMeasures(int measuresCount)
  {
  	PVector<Measure> measures = this.measures;
    for (int i = 0; i < measuresCount; i++)
    {
    	measures = measures.plus(Measure.createMinimal());
    }
    return new Staff(measures, linesCount, interlineSpace);
  }
  
  
  /**
   * Gets the list of measures.
   */
  public Vector<Measure> getMeasures()
  {
    return measures;
  }
  
  
  /**
   * Gets the number of lines of this measure in mm.
   */
  public int getLinesCount()
  {
  	return linesCount;
  }
  
  
  /**
   * Gets the interline space of this staff in mm, or null for default.
   */
  public Float getInterlineSpace()
  {
  	return interlineSpace;
  }
  
  
  /**
   * Gets the measure with the given index, or throws an
   * {@link IllegalMPException} if there is none.
   * Only the measure index of the given position is relevant.
   */
  public Measure getMeasure(MP pos)
  {
  	int index = pos.getMeasure();
  	if (index >= 0 && index < measures.size())
  		return measures.get(index);
  	else
  		throw new IllegalMPException(pos);
  }
  
  
  /**
   * Gets the voice within the measure at the given position, or throws an
   * {@link IllegalMPException} if there is none.
   * Only the measure index and voice index of the given position are relevant.
   */
  public Voice getVoice(MP pos)
  {
  	return getMeasure(pos).getVoice(pos);
  }
  
  
  /**
	 * Gets the {@link VoiceElement} before the given position (also over measure
	 * boundaries) together with the {@link MP} where it starts,
	 * or null, if there is none (begin of score or everything is empty)
	 * If a {@link VoiceElement} starts at the given beat, the preceding {@link VoiceElement}
	 * is returned. If the given beat is within a {@link VoiceElement}, not that
	 * element but the {@link VoiceElement} before that element is returned.
	 */
	public MPE<VoiceElement> getVoiceElementEndingAtOrBefore(MP mp)
	{
		//find the last voice element ending at or before the current beat
		//in the given measure
		BeatE<VoiceElement> ret = getVoice(mp).getElement(Last, Stop, BeforeOrAt, mp.getBeat());
		if (ret == null)
		{
			//no result in this measure. loop through the preceding measures.
			for (int i = mp.getMeasure() - 1; i >= 0; i--)
			{
				BeatE<VoiceElement> pve = getVoice(mp.withMeasure(i)).getElement(
					Last, Start, AtOrAfter, Fraction._0);
				if (pve != null)
					return MPE.mpe(pve.getElement(), mp.withMeasure(i).withBeat(pve.getBeat()));
			}
		}
		//nothing found
		return null;
	}
	
	
	/**
	 * Sets the measure with the given index.
	 * If out of current range, empty measures up to the given
	 * one are created.
	 */
	public Staff withMeasure(int index, Measure measure)
	{
		PVector<Measure> measures = this.measures;
		while (measures.size() < index + 1)
		{
			measures = measures.plus(Measure.createMinimal());
		}
		return new Staff(measures.with(index, measure), linesCount, interlineSpace);
	}
  

}
