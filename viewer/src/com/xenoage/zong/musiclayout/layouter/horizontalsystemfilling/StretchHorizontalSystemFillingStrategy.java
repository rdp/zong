package com.xenoage.zong.musiclayout.layouter.horizontalsystemfilling;

import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.BeatOffset;
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
  	for (MeasureColumnSpacing mcs : systemArrangement.getMeasureColumnSpacings())
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
  	MeasureColumnSpacing[] newMCSpacings = new MeasureColumnSpacing[systemArrangement.getMeasureColumnSpacings().length];
  	for (int iColumn = 0; iColumn < systemArrangement.getMeasureColumnSpacings().length; iColumn++)
  	{
  		MeasureColumnSpacing column = systemArrangement.getMeasureColumnSpacings()[iColumn];
  		//beat offsets
  		BeatOffset[] newBeatOffsets = new BeatOffset[column.getBeatOffsets().length];
			for (int iBeatOffset = 0; iBeatOffset < column.getBeatOffsets().length; iBeatOffset++)
			{
				BeatOffset oldBeatOffset = column.getBeatOffsets()[iBeatOffset];
				//stretch the offset
				newBeatOffsets[iBeatOffset] = oldBeatOffset.changeOffsetMm(oldBeatOffset.getOffsetMm() * stretch);
			}
			BeatOffset[] newBarlineOffsets = new BeatOffset[column.getBarlineOffsets().length];
			for (int iBarlineOffset = 0; iBarlineOffset < column.getBarlineOffsets().length; iBarlineOffset++)
			{
				BeatOffset oldBarlineOffset = column.getBarlineOffsets()[iBarlineOffset];
				//stretch the offset
				newBarlineOffsets[iBarlineOffset] = oldBarlineOffset.changeOffsetMm(oldBarlineOffset.getOffsetMm() * stretch);
			}
  		//measures
			MeasureSpacing[] newMeasureSpacings = new MeasureSpacing[column.getMeasureSpacings().length];
  		for (int iMeasure = 0; iMeasure < column.getMeasureSpacings().length; iMeasure++)
  		{
  			MeasureSpacing oldMS = column.getMeasureSpacings()[iMeasure];
  			//voices
  			VoiceSpacing[] newVSs = new VoiceSpacing[oldMS.getVoicesCount()];
  			for (int iVoice = 0; iVoice < oldMS.getVoicesCount(); iVoice++)
  			{
  				VoiceSpacing oldVS = oldMS.getVoice(iVoice);
  				//spacing elements
  				SpacingElement[] newSEs = new SpacingElement[oldVS.getSpacingElements().length];
  				for (int iElement = 0; iElement < oldVS.getSpacingElements().length; iElement++)
  				{
  					SpacingElement oldSE = oldVS.getSpacingElements()[iElement];
  					//stretch the offset
  					newSEs[iElement] = oldSE.changeOffset(oldSE.getOffset() * stretch);
  				}
  				newVSs[iVoice] = new VoiceSpacing(oldVS.getVoice(), oldVS.getInterlineSpace(), newSEs);
  			}
  			newMeasureSpacings[iMeasure] = new MeasureSpacing(
  				oldMS.getMP(), newVSs, oldMS.getLeadingSpacing());
  		}
  		
  		newMCSpacings[iColumn] = new MeasureColumnSpacing(column.getScore(),
  			newMeasureSpacings, newBeatOffsets, newBarlineOffsets);
  		
  	}
  	
  	//create and return the new system
  	return systemArrangement.changeSpacings(newMCSpacings, usableWidth);
	}

}
