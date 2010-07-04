package com.xenoage.zong.musiclayout.layouter.horizontalsystemfilling;

import static com.xenoage.pdlib.PVector.pvec;

import com.xenoage.pdlib.PVector;
import com.xenoage.zong.musiclayout.BeatOffset;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.spacing.ColumnSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;


/**
 * This horizontal system filling strategy
 * stretches all measures within the given system,
 * so that they use the whole useable width
 * of the system.
 * 
 * @author Andreas Wenger
 */
public class StretchHorizontalSystemFillingStrategy
  implements HorizontalSystemFillingStrategy
{
	
	private static StretchHorizontalSystemFillingStrategy instance = null;
	
	
	public static StretchHorizontalSystemFillingStrategy getInstance()
	{
		if (instance == null)
			instance = new StretchHorizontalSystemFillingStrategy();
		return instance;
	}
	
	
	private StretchHorizontalSystemFillingStrategy()
	{
	}
	
  
  /**
   * Stretches the measures of the given system, so that
   * it uses the whole usable width of the system.
   */
	public SystemArrangement computeSystemArrangement(SystemArrangement systemArrangement,
		float usableWidth)
	{
		 //compute width of all voice spacings
  	//(leading spacings are not stretched)
  	float voicesWidth = 0;
  	float leadingsWidth = 0;
  	for (ColumnSpacing mcs : systemArrangement.getColumnSpacings())
  	{
  		voicesWidth += mcs.getVoicesWidth();
  		leadingsWidth += mcs.getLeadingWidth();
  	}
  	
  	//compute the stretching factor for the voice spacings
  	if (voicesWidth == 0)
  		return systemArrangement;
  	float stretch = (usableWidth - leadingsWidth) / voicesWidth;
  	
  	//stretch the voice spacings
  	//measure columns
  	PVector<ColumnSpacing> newMCSpacings = pvec();
  	for (ColumnSpacing column : systemArrangement.getColumnSpacings())
  	{
  		//beat offsets
  		PVector<BeatOffset> newBeatOffsets = pvec();
			for (BeatOffset oldBeatOffset : column.getBeatOffsets())
			{
				//stretch the offset
				newBeatOffsets = newBeatOffsets.plus(
					oldBeatOffset.withOffsetMm(oldBeatOffset.getOffsetMm() * stretch));
			}
			PVector<BeatOffset> newBarlineOffsets = pvec();
			for (BeatOffset oldBarlineOffset : column.getBarlineOffsets())
			{
				//stretch the offset
				newBarlineOffsets = newBarlineOffsets.plus(
					oldBarlineOffset.withOffsetMm(oldBarlineOffset.getOffsetMm() * stretch));
			}
  		//measures
			PVector<MeasureSpacing> newMeasureSpacings = pvec();
  		for (MeasureSpacing oldMS : column.getMeasureSpacings())
  		{
  			//voices
  			PVector<VoiceSpacing> newVSs = pvec();
  			for (VoiceSpacing oldVS : oldMS.getVoiceSpacings())
  			{
  				//spacing elements
  				PVector<SpacingElement> newSEs = pvec();
  				for (SpacingElement oldSE : oldVS.getSpacingElements())
  				{
  					//stretch the offset
  					newSEs = newSEs.plus(oldSE.withOffset(oldSE.getOffset() * stretch));
  				}
  				newVSs = newVSs.plus(new VoiceSpacing(oldVS.getVoice(), oldVS.getInterlineSpace(), newSEs));
  			}
  			newMeasureSpacings = newMeasureSpacings.plus(new MeasureSpacing(
  				oldMS.getMP(), newVSs, oldMS.getLeadingSpacing()));
  		}
  		
  		newMCSpacings = newMCSpacings.plus(new ColumnSpacing(column.getScore(),
  			newMeasureSpacings, newBeatOffsets, newBarlineOffsets));
  		
  	}
  	
  	//create and return the new system
  	return systemArrangement.withSpacings(newMCSpacings, usableWidth);
	}

}
