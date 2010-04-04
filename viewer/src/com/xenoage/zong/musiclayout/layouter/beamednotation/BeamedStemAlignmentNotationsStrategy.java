package com.xenoage.zong.musiclayout.layouter.beamednotation;

import java.util.ArrayList;

import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.beam.Beam;
import com.xenoage.zong.core.music.beam.Beam.HorizontalSpan;
import com.xenoage.zong.core.music.beam.Beam.VerticalSpan;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterContext;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.beamednotation.alignment.SingleMeasureSingleStaffStrategy;
import com.xenoage.zong.musiclayout.layouter.beamednotation.alignment.SingleMeasureTwoStavesStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;


/**
 * This strategy computes the alignments of the stems and the widths
 * of a given {@link Beam}.
 * 
 * @author Andreas Wenger
 */
public class BeamedStemAlignmentNotationsStrategy
	implements ScoreLayouterStrategy
{

	private SingleMeasureSingleStaffStrategy singleMeasureSingleStaffStrategy = 
		new SingleMeasureSingleStaffStrategy();
	private SingleMeasureTwoStavesStrategy singleMeasureTwoStavesStrategy = 
		new SingleMeasureTwoStavesStrategy();
	
	
	/**
	 * This strategy computes the lengths of the stems of beamed notes.
	 * It only works for chords, in which all the stems point in the same
	 * direction! (like computed by {@link BeamedStemDirectionNotationsStrategy}).
	 * The updated chord notations are returned.
	 */
	public NotationsCache computeNotations(ScoreLayouterContext lc, Beam beam,
		ArrayList<MeasureColumnSpacing> measureColumnSpacings,
		NotationsCache notations)
	{
		Globals globals = lc.getScore().getGlobals();
		
		//choose appropriate strategy
		if (beam.getHorizontalSpan(globals) == HorizontalSpan.SingleMeasure)
		{
			if (beam.getVerticalSpan(globals) == VerticalSpan.SingleStaff)
			{
				MP firstMP = globals.getMP(beam.getFirstWaypoint().getChord());
				return singleMeasureSingleStaffStrategy.computeNotations(lc, beam,
					measureColumnSpacings.get(firstMP.getMeasure()), notations);
			}
			else if (beam.getVerticalSpan(globals) == VerticalSpan.TwoAdjacentStaves)
			{
				return singleMeasureTwoStavesStrategy.computeNotations(beam, notations);
			}
			else
			{
				throw new IllegalStateException("No strategy for more than two or non-adjacent staves");
			}
		}
		else
		{
			throw new IllegalStateException("Multi-measure beams are not supported yet");
		}
	}

}
