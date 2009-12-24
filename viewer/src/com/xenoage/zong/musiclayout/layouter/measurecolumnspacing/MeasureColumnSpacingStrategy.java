package com.xenoage.zong.musiclayout.layouter.measurecolumnspacing;

import java.util.ArrayList;

import com.xenoage.util.iterators.It;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.lang.Tuple3;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.data.header.MeasureColumnHeader;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.MusicContext;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.MeasureColumn;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterContext;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.layouter.cache.VoiceSpacingsCache;
import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.BeatOffset;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureLeadingSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;


/**
 * A {@link MeasureColumnSpacingStrategy}
 * computes a single {@link MeasureColumnSpacing} from
 * the given measure column.
 * 
 * @author Andreas Wenger
 */
public class MeasureColumnSpacingStrategy
	implements ScoreLayouterStrategy
{
	
	//used strategies
	private final SeparateVoiceSpacingStrategy separateVoiceSpacingStrategy;
	private final BeatOffsetsStrategy beatOffsetsStrategy;
	private final BarlinesBeatOffsetsStrategy barlinesBeatOffsetsStrategy;
	private final BeatOffsetBasedVoiceSpacingStrategy beatBasedVoiceSpacingStrategy;
	private final MeasureLeadingSpacingStrategy measureLeadingSpacingStrategy;
	
	
	/**
	 * Creates a new {@link MeasureColumnSpacingStrategy}.
	 */
	public MeasureColumnSpacingStrategy(SeparateVoiceSpacingStrategy separateVoiceSpacingStrategy,
		BeatOffsetsStrategy beatOffsetsStrategy,
		BarlinesBeatOffsetsStrategy barlinesBeatOffsetsStrategy,
		BeatOffsetBasedVoiceSpacingStrategy beatBasedVoiceSpacingStrategy,
		MeasureLeadingSpacingStrategy measureLeadingSpacingStrategy)
	{
		this.separateVoiceSpacingStrategy = separateVoiceSpacingStrategy;
		this.beatOffsetsStrategy = beatOffsetsStrategy;
		this.barlinesBeatOffsetsStrategy = barlinesBeatOffsetsStrategy;
		this.beatBasedVoiceSpacingStrategy = beatBasedVoiceSpacingStrategy;
		this.measureLeadingSpacingStrategy = measureLeadingSpacingStrategy;
	}

	
	/**
   * Creates the optimum horizontal spacing for the given measure column.
   * If createLeading is true, a leading spacing is created.
   * @param measureColumn   the measure column for which the spacing is computed
   * @param createLeading   true, if a leading spacing has to be created, otherwise false
   * @param notations       the already computed notations
   * @param lc              the context of the layouter
   * @return                the measure column spacing is returned, and also the optimal
   *                        voice spacings for all voices within the measure column (useful,
   *                        since these can be reused later) and the notations of the
   *                        leading spacing, if created, otherwise null
   */
	public Tuple3<MeasureColumnSpacing, VoiceSpacingsCache, NotationsCache> computeMeasureColumnSpacing(
		MeasureColumn measureColumn, boolean createLeading, NotationsCache notations,
		MeasureColumnHeader measureHeader, ScoreLayouterContext lc)
	{
		//beats within this measure column
		Fraction measureBeats = lc.getScore().getController().getMeasureBeats(
			measureColumn.get(0).getIndex());
		
		//compute the optimal spacings for each voice separately
		VoiceSpacingsCache optimalVoiceSpacings = new VoiceSpacingsCache();
    for (Measure measure : measureColumn)
    {
    	for (Voice voice : measure.getVoices())
    	{
    		optimalVoiceSpacings.set(separateVoiceSpacingStrategy.computeVoiceSpacing(
    			voice, createLeading, notations, measureBeats), voice);
    	}
    }
		
    //compute the beat offsets of this measure column
    ArrayList<BeatOffset> beatOffsets = beatOffsetsStrategy.computeBeatOffsets(
    	measureColumn, optimalVoiceSpacings, measureBeats);
    
    //recompute beat offsets with respect to barlines
    Tuple2<ArrayList<BeatOffset>, ArrayList<BeatOffset>> offsets =
    	barlinesBeatOffsetsStrategy.computeBeatOffsets(beatOffsets, measureHeader, lc.getMaxInterlineSpace());
    beatOffsets = offsets.get1();
    ArrayList<BeatOffset> barlineOffsets = offsets.get2();
    
    //compute the spacings for the whole column, so that are equal beats are aligned
    VoiceSpacingsCache alignedVoiceSpacings = new VoiceSpacingsCache();
    for (Measure measure : measureColumn)
    {
    	for (Voice voice : measure.getVoices())
    	{
    		alignedVoiceSpacings.set(beatBasedVoiceSpacingStrategy.computeVoiceSpacing(
    			optimalVoiceSpacings.get(voice), beatOffsets), voice);
    	}
    }
    
    //compute spacings for each measure
    NotationsCache leadingNotations = (createLeading ? new NotationsCache() : null);
    MeasureSpacing[] measureSpacings = new MeasureSpacing[measureColumn.size()];
    It<Measure> measures = new It<Measure>(measureColumn);
    for (Measure measure : measures)
    {
    	//create leading spacing, if needed
    	MeasureLeadingSpacing leadingSpacing = null;
      if (createLeading)
      {
      	MusicContext mc = lc.getScore().getController().getMusicContextAt(
      		new ScorePosition(measures.getIndex(), measure.getIndex(), Fraction._0, 0));
      	Tuple2<MeasureLeadingSpacing, NotationsCache> ls =
      		measureLeadingSpacingStrategy.computeLeadingSpacing(mc);
      	leadingSpacing = ls.get1();
      	leadingNotations.setAll(ls.get2());
      }
      //create measure spacing
      measureSpacings[measures.getIndex()] = new MeasureSpacing(
      	measure, getVoiceSpacingsArray(measure, alignedVoiceSpacings), leadingSpacing);
    }
    
    //create arrays for beat offsets
    BeatOffset[] beatOffsetsArray = new BeatOffset[beatOffsets.size()];
    beatOffsets.toArray(beatOffsetsArray);
    BeatOffset[] barlineOffsetsArray = new BeatOffset[barlineOffsets.size()];
    barlineOffsets.toArray(barlineOffsetsArray);
    
    return new Tuple3<MeasureColumnSpacing, VoiceSpacingsCache, NotationsCache>(
    	new MeasureColumnSpacing(measureSpacings, beatOffsetsArray, barlineOffsetsArray),
    	optimalVoiceSpacings, leadingNotations);
	}
	
	
	/**
	 * Gets the {@link VoiceSpacing}s of the given {@link Measure} as an
	 * array, using the given {@link VoiceSpacingsCache}.
	 * 
	 * //LAYOUT-PERFORMANCE (needed 2 of 60 seconds)
	 */
	private VoiceSpacing[] getVoiceSpacingsArray(Measure measure, VoiceSpacingsCache voiceSpacings)
	{
		VoiceSpacing[] ret = new VoiceSpacing[measure.getVoices().size()];
		It<Voice> voices = new It<Voice>(measure.getVoices());
    for (Voice voice : voices)
    {
    	ret[voices.getIndex()] = voiceSpacings.get(voice);
    }
    return ret;
	}
	

}
