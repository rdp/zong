package com.xenoage.zong.musiclayout.layouter.measurecolumnspacing;

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
	public Tuple3<MeasureColumnSpacing, VoiceSpacingsCache, NotationsCache> computeMeasureColumnSpacing(
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
    			voice, ScoreController.getInterlineSpace(lc.getScore(), MP.atStaff(iStaff)),
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
    NotationsCache leadingNotations = (createLeading ? new NotationsCache() : null);
    MeasureSpacing[] measureSpacings = new MeasureSpacing[column.size()];
    It<Measure> measures = new It<Measure>(column);
    for (Measure measure : measures)
    {
    	//create leading spacing, if needed
    	MeasureLeadingSpacing leadingSpacing = null;
      if (createLeading)
      {
      	MusicContext mc = ScoreController.getMusicContext(lc.getScore(),
      		mp(measures.getIndex(), measureIndex, 0, Fraction._0), At);
      	Tuple2<MeasureLeadingSpacing, NotationsCache> ls =
      		measureLeadingSpacingStrategy.computeLeadingSpacing(mc);
      	leadingSpacing = ls.get1();
      	leadingNotations.setAll(ls.get2());
      }
      //create measure spacing
      measureSpacings[measures.getIndex()] = new MeasureSpacing(
      	atMeasure(measures.getIndex(), measureIndex),
      	getVoiceSpacingsArray(measure, alignedVoiceSpacings), leadingSpacing);
    }
    
    //create arrays for beat offsets
    BeatOffset[] beatOffsetsArray = new BeatOffset[beatOffsets.size()];
    beatOffsets.toArray(beatOffsetsArray);
    BeatOffset[] barlineOffsetsArray = new BeatOffset[barlineOffsets.size()];
    barlineOffsets.toArray(barlineOffsetsArray);
    
    return new Tuple3<MeasureColumnSpacing, VoiceSpacingsCache, NotationsCache>(
    	new MeasureColumnSpacing(lc.getScore(), measureSpacings, beatOffsetsArray,
    		barlineOffsetsArray), optimalVoiceSpacings, leadingNotations);
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
