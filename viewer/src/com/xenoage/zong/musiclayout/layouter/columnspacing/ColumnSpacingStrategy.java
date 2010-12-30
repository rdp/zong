package com.xenoage.zong.musiclayout.layouter.columnspacing;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.Range.range;
import static com.xenoage.zong.core.music.MP.atMeasure;
import static com.xenoage.zong.core.music.MP.mp;
import static com.xenoage.zong.core.music.util.BeatInterval.At;
import static com.xenoage.zong.io.score.ScoreController.getMeasureBeats;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.iterators.It;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.lang.Tuple3;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.header.ColumnHeader;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.MusicContext;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.util.Column;
import com.xenoage.zong.io.score.ScoreController;
import com.xenoage.zong.musiclayout.BeatOffset;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterContext;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.layouter.cache.VoiceSpacingsCache;
import com.xenoage.zong.musiclayout.spacing.ColumnSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.LeadingSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;


/**
 * A {@link ColumnSpacingStrategy}
 * computes a single {@link ColumnSpacing} from
 * the given measure column.
 * 
 * @author Andreas Wenger
 */
public class ColumnSpacingStrategy
	implements ScoreLayouterStrategy
{
	
	//used strategies
	private final SeparateVoiceSpacingStrategy separateVoiceSpacingStrategy;
	private final BeatOffsetsStrategy beatOffsetsStrategy;
	private final BarlinesBeatOffsetsStrategy barlinesBeatOffsetsStrategy;
	private final BeatOffsetBasedVoiceSpacingStrategy beatBasedVoiceSpacingStrategy;
	private final LeadingSpacingStrategy measureLeadingSpacingStrategy;
	
	
	/**
	 * Creates a new {@link ColumnSpacingStrategy}.
	 */
	public ColumnSpacingStrategy(SeparateVoiceSpacingStrategy separateVoiceSpacingStrategy,
		BeatOffsetsStrategy beatOffsetsStrategy,
		BarlinesBeatOffsetsStrategy barlinesBeatOffsetsStrategy,
		BeatOffsetBasedVoiceSpacingStrategy beatBasedVoiceSpacingStrategy,
		LeadingSpacingStrategy measureLeadingSpacingStrategy)
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
   * @param measureIndex    index of this measure
   * @param column          the measure column for which the spacing is computed
   * @param createLeading   true, if a leading spacing has to be created, otherwise false
   * @param notations       the already computed notations
   * @param lc              the context of the layouter
   * @return                the measure column spacing is returned, and also the optimal
   *                        voice spacings for all voices within the measure column (useful,
   *                        since these can be reused later) and the notations of the
   *                        leading spacing, if created, otherwise null
   */
	public Tuple3<ColumnSpacing, VoiceSpacingsCache, NotationsCache> computeMeasureColumnSpacing(
		int measureIndex, Column column, boolean createLeading, NotationsCache notations,
		ColumnHeader columnHeader, ScoreLayouterContext lc)
	{
		//beats within this measure column
		Fraction measureBeats = getMeasureBeats(lc.getScore(), measureIndex);
		
		//compute the optimal spacings for each voice separately
		VoiceSpacingsCache optimalVoiceSpacings = new VoiceSpacingsCache();
    for (int iStaff : range(column))
    {
    	Measure measure = column.get(iStaff);
    	for (Voice voice : measure.getVoices())
    	{
    		optimalVoiceSpacings.set(separateVoiceSpacingStrategy.computeVoiceSpacing(
    			voice, measure, ScoreController.getInterlineSpace(lc.getScore(), MP.atStaff(iStaff)),
    			createLeading, notations, measureBeats), voice);
    	}
    }
		
    //compute the beat offsets of this measure column
    PVector<BeatOffset> beatOffsets = beatOffsetsStrategy.computeBeatOffsets(
    	column, optimalVoiceSpacings, measureBeats);
    
    //recompute beat offsets with respect to barlines
    Tuple2<PVector<BeatOffset>, PVector<BeatOffset>> offsets =
    	barlinesBeatOffsetsStrategy.computeBeatOffsets(beatOffsets, columnHeader,
    		lc.getMaxInterlineSpace());
    beatOffsets = offsets.get1();
    PVector<BeatOffset> barlineOffsets = offsets.get2();
    
    //compute the spacings for the whole column, so that are equal beats are aligned
    VoiceSpacingsCache alignedVoiceSpacings = new VoiceSpacingsCache();
    for (Measure measure : column)
    {
    	for (Voice voice : measure.getVoices())
    	{
    		alignedVoiceSpacings.set(beatBasedVoiceSpacingStrategy.computeVoiceSpacing(
    			optimalVoiceSpacings.get(voice), beatOffsets), voice);
    	}
    }
    
    //compute spacings for each measure
    NotationsCache leadingNotations = (createLeading ? NotationsCache.empty : null);
    PVector<MeasureSpacing> measureSpacings = pvec();
    It<Measure> measures = new It<Measure>(column);
    for (Measure measure : measures)
    {
    	//create leading spacing, if needed
    	LeadingSpacing leadingSpacing = null;
      if (createLeading)
      {
      	MusicContext mc = ScoreController.getMusicContext(lc.getScore(),
      		mp(measures.getIndex(), measureIndex, 0, Fraction._0), At, null);
      	Tuple2<LeadingSpacing, NotationsCache> ls =
      		measureLeadingSpacingStrategy.computeLeadingSpacing(mc);
      	leadingSpacing = ls.get1();
      	leadingNotations = leadingNotations.merge(ls.get2());
      }
      //create measure spacing
      measureSpacings = measureSpacings.plus(new MeasureSpacing(
      	atMeasure(measures.getIndex(), measureIndex),
      	getVoiceSpacingsArray(measure, alignedVoiceSpacings), leadingSpacing));
    }
    
    return new Tuple3<ColumnSpacing, VoiceSpacingsCache, NotationsCache>(
    	new ColumnSpacing(lc.getScore(), measureSpacings, beatOffsets,
    		barlineOffsets), optimalVoiceSpacings, leadingNotations);
	}
	
	
	/**
	 * Gets the {@link VoiceSpacing}s of the given {@link Measure},
	 * using the given {@link VoiceSpacingsCache}.
	 */
	private PVector<VoiceSpacing> getVoiceSpacingsArray(Measure measure, VoiceSpacingsCache voiceSpacings)
	{
		PVector<VoiceSpacing> ret = pvec();
    for (Voice voice : measure.getVoices())
    {
    	ret = ret.plus(voiceSpacings.get(voice));
    }
    return ret;
	}
	

}
