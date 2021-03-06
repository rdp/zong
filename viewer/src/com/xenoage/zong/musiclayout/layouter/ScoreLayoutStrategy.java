package com.xenoage.zong.musiclayout.layouter;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.Range.range;
import static com.xenoage.util.lang.Tuple2.t;
import static com.xenoage.zong.core.music.util.Column.column;

import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Point2f;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.Globals;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.beam.Beam;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.util.Column;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.layout.frames.ScoreFrameChain;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.ScoreFrameLayout;
import com.xenoage.zong.musiclayout.ScoreLayout;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.continued.ContinuedElement;
import com.xenoage.zong.musiclayout.layouter.arrangement.FrameArrangementStrategy;
import com.xenoage.zong.musiclayout.layouter.beamednotation.BeamedStemAlignmentNotationsStrategy;
import com.xenoage.zong.musiclayout.layouter.beamednotation.BeamedStemDirectionNotationsStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.layouter.columnspacing.ColumnSpacingStrategy;
import com.xenoage.zong.musiclayout.layouter.horizontalsystemfilling.HorizontalSystemFillingStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.NotationStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.ScoreFrameLayoutStrategy;
import com.xenoage.zong.musiclayout.layouter.verticalframefilling.VerticalFrameFillingStrategy;
import com.xenoage.zong.musiclayout.layouter.voicenotation.VoiceStemDirectionNotationsStrategy;
import com.xenoage.zong.musiclayout.spacing.ColumnSpacing;


/**
 * This strategy creates a {@link ScoreLayout} from a given
 * {@link Score} and a list of {@link ScoreFrame}s.
 * 
 * @author Andreas Wenger
 */
public class ScoreLayoutStrategy
	implements ScoreLayouterStrategy
{
	
	//used strategies
	private final NotationStrategy notationStrategy;
	private final BeamedStemDirectionNotationsStrategy beamedStemDirectionNotationsStrategy;
	private final VoiceStemDirectionNotationsStrategy voiceStemDirectionNotationsStrategy;
	private final ColumnSpacingStrategy measureColumnSpacingStrategy;
	private final FrameArrangementStrategy frameArrangementStrategy;
	private final BeamedStemAlignmentNotationsStrategy beamedStemAlignmentNotationsStrategy;
	private final ScoreFrameLayoutStrategy scoreFrameLayoutStrategy;
	
	
	/**
	 * Creates a new {@link ScoreLayoutStrategy}.
	 */
	public ScoreLayoutStrategy(NotationStrategy notationStrategy,
		BeamedStemDirectionNotationsStrategy beamedStemDirectionNotationsStrategy,
		VoiceStemDirectionNotationsStrategy voiceStemDirectionNotationsStrategy,
		ColumnSpacingStrategy measureColumnSpacingStrategy, FrameArrangementStrategy frameArrangementStrategy,
		BeamedStemAlignmentNotationsStrategy beamedStemAlignmentNotationsStrategy,
		ScoreFrameLayoutStrategy scoreFrameLayoutStrategy)
	{
		this.notationStrategy = notationStrategy;
		this.beamedStemDirectionNotationsStrategy = beamedStemDirectionNotationsStrategy;
		this.voiceStemDirectionNotationsStrategy = voiceStemDirectionNotationsStrategy;
		this.measureColumnSpacingStrategy = measureColumnSpacingStrategy;
		this.frameArrangementStrategy = frameArrangementStrategy;
		this.beamedStemAlignmentNotationsStrategy = beamedStemAlignmentNotationsStrategy;
		this.scoreFrameLayoutStrategy = scoreFrameLayoutStrategy;
	}

	
	/**
	 * Computes and returns the layout of the given score
	 * within the given chain of score frames.
	 */
	public ScoreLayout computeScoreLayout(ScoreLayouterContext lc)
	{
		Score score = lc.getScore();
		//notations of elements
		NotationsCache notations = notationStrategy.computeNotations(lc.getScore(), lc.getSymbolPool());
		//update beamed notations with correct stem directions
		notations = notations.merge(computeBeamStemDirections(notations, lc));
		//TODO: stem directions dependent on their voice
		for (int iStaff : range(0, score.getStavesCount() - 1))
		{
			notations = notations.merge(voiceStemDirectionNotationsStrategy.computeNotations(iStaff, notations, lc));
		}
		//optimal measure spacings
		PVector<ColumnSpacing> optimalMeasureColumnSpacings = computeMeasureSpacings(notations, lc);
		//break into systems and frames
		Tuple2<PVector<FrameArrangement>, NotationsCache> t = computeFrameArrangements(
			optimalMeasureColumnSpacings, notations, lc);
		PVector<FrameArrangement> frameArrangements = t.get1();
		notations = notations.merge(t.get2());
		//system stretching (horizontal)
		frameArrangements = fillSystemsHorizontally(frameArrangements, lc);
		//frame filling (vertical)
		frameArrangements = fillFramesVertically(frameArrangements, lc);
		//compute beams
		notations = notations.merge(computeBeamStemAlignments(frameArrangements, optimalMeasureColumnSpacings, notations, lc));
		//create score layout from the collected information
		PVector<ScoreFrameLayout> scoreFrameLayouts =
			createScoreFrameLayouts(frameArrangements, notations, lc);
		//create score layout
		return new ScoreLayout(lc.getScore(), scoreFrameLayouts);
	}
	
	
	/**
	 * Computes beamed notations with correct stem directions.
	 */
	NotationsCache computeBeamStemDirections(NotationsCache notations, ScoreLayouterContext lc)
	{
		NotationsCache ret = NotationsCache.empty;
		//go again through all elements, finding beams, and recompute stem direction
		Score score = lc.getScore();
		Globals globals = score.getGlobals();
		for (int iMeasure : range(0, score.getMeasuresCount() - 1))
		{
			Column measureColumn = column(score, iMeasure);
			for (Measure measure : measureColumn)
			{
				for (Voice voice : measure.getVoices())
				{
					for (MusicElement element : voice.getElements())
					{
						if (element instanceof Chord)
						{
							Chord chord = (Chord) element;
							//compute each beam only one time (when the end waypoint is found)
							Beam beam = globals.getBeams().get(chord);
							if (beam != null && beam.getLastWaypoint().getChord() == chord)
							{
								ret = ret.merge(beamedStemDirectionNotationsStrategy.computeNotations(
									beam, notations, lc.getScore()));
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	
	/**
	 * Computes the {@link ColumnSpacing} for each measure
	 * (without leading spacing).
	 */
	PVector<ColumnSpacing> computeMeasureSpacings(NotationsCache notations, ScoreLayouterContext lc)
	{
		Score score = lc.getScore();
		PVector<ColumnSpacing> ret = pvec();
		for (int iMeasure : range(0, score.getMeasuresCount() - 1))
		{
			ret = ret.plus(measureColumnSpacingStrategy.computeMeasureColumnSpacing(iMeasure, column(score, iMeasure),
				false, notations, score.getScoreHeader().getColumnHeader(iMeasure), lc).get1()); //TODO: also save optimal voice spacings for later reuse
		}
		return ret;
	}
	
	
	/**
	 * Breaks the given measure column spacings into systems and frames.
	 * The list of frame arrangements and the list of created leading notations
	 * is returned.
	 */
	Tuple2<PVector<FrameArrangement>, NotationsCache> computeFrameArrangements(
		Vector<ColumnSpacing> measureColumnSpacings,
		NotationsCache notations, ScoreLayouterContext lc)
	{
		ScoreFrameChain chain = lc.getScoreFrameChain();
		chain.clearAdditionalFrames(); //clear old stuff
		PVector<FrameArrangement> ret = pvec();
		NotationsCache retLeadingNotations = NotationsCache.empty;
		int measuresCount = lc.getScore().getMeasuresCount();
		int iMeasure = 0;
		int iSystem = 0;
		int iFrame = 0;
		boolean additionalFrameIteration = false;
		while (true)
		{
			additionalFrameIteration = false;
			
			//find score frame
			ScoreFrame scoreFrame;
			if (iFrame < chain.getLimitedFramesCount())
			{
				//there is another existing score frame
				scoreFrame = chain.getFrame(iFrame);
			}
			else
			{
				//there is no another existing score frame
				if (!chain.isCompleteChain())
				{
					//we are not interested in the stuff after the last score frame. exit.
					break;
				}
				else if (iMeasure >= measuresCount)
				{
					//all measures layouted. exit.
					break;
				}
				else
				{
					//still material to layout and additional frames requested. create one.
					scoreFrame = new ScoreFrame(new Point2f(0, 0), chain.getAdditionalFrameSize());
					additionalFrameIteration = true;
				}
			}
			
			//some material left to layout?
			if (iMeasure < measuresCount)
			{
				//more measures to layout
				Tuple2<FrameArrangement, NotationsCache> t =
					frameArrangementStrategy.computeFrameArrangement(iMeasure, iSystem,
						scoreFrame.getSize(), measureColumnSpacings, notations, lc);
				FrameArrangement frameArr = t.get1();
				NotationsCache leadingNotations = t.get2();
				if (frameArr.getSystems().size() > 0)
				{
					//at least one measure in this frame. remember frame arrangement and leading notations
					ret = ret.plus(frameArr);
					retLeadingNotations = retLeadingNotations.merge(leadingNotations);
					if (additionalFrameIteration)
					{
						chain.addAdditionalFrame(scoreFrame);
					}
					iMeasure = frameArr.getSystems().getLast().getEndMeasureIndex() + 1;
					iSystem += frameArr.getSystems().size();
				}
				else
				{
					//no space for a measure in this frame. empty frame. but only, if existing frame.
					//do not create endless list of empty additional frames!
					if (!additionalFrameIteration)
					{
						ret = ret.plus(new FrameArrangement(new PVector<SystemArrangement>(), scoreFrame.getSize()));
					}
					else
					{
						break;
					}
				}
			}
			else
			{
				//no material left. empty frame.
				ret = ret.plus(new FrameArrangement(new PVector<SystemArrangement>(), scoreFrame.getSize()));
			}
			
			//next frame
			iFrame++;
		}
		return t(ret, retLeadingNotations);
	}

	
	/**
	 * Fills the systems horizontally according to the {@link HorizontalSystemFillingStrategy}
	 * of the frame.
	 */
	PVector<FrameArrangement> fillSystemsHorizontally(PVector<FrameArrangement> frameArrangements,
		ScoreLayouterContext lc)
	{
		ScoreFrameChain chain = lc.getScoreFrameChain();
		PVector<FrameArrangement> ret = pvec();
		for (int iFrame : range(frameArrangements))
	  {
			FrameArrangement frameArr = frameArrangements.get(iFrame);
			HorizontalSystemFillingStrategy hFill = chain.getFrame(iFrame).getHorizontalSystemFillingStrategy();
			if (hFill != null)
			{
				//apply strategy
				PVector<SystemArrangement> systemArrs = pvec();
			  for (SystemArrangement oldSystemArr : frameArr.getSystems())
			  {
			  	float usableWidth = frameArr.getUsableSize().width - oldSystemArr.getMarginLeft() - oldSystemArr.getMarginRight();
			  	systemArrs = systemArrs.plus(
			  		hFill.computeSystemArrangement(oldSystemArr, usableWidth));
			  }
			  ret = ret.plus(new FrameArrangement(systemArrs, frameArr.getUsableSize()));
			}
			else
			{
				//unmodified frame
				ret = ret.plus(frameArr);
			}
		}
		return ret;
	}
	
	
	/**
	 * Fills the frames vertically according to the {@link VerticalFrameFillingStrategy}
	 * of the frame.
	 */
	PVector<FrameArrangement> fillFramesVertically(PVector<FrameArrangement> frameArrs,
		ScoreLayouterContext lc)
	{
		ScoreFrameChain chain = lc.getScoreFrameChain();
		PVector<FrameArrangement> ret = pvec();
		Score score = lc.getScore();
	  for (int iFrame : range(frameArrs))
	  {
	  	VerticalFrameFillingStrategy vFill = chain.getFrame(iFrame).getVerticalFrameFillingStrategy();
	  	if (vFill != null)
	  	{
	  		ret = ret.plus(vFill.computeFrameArrangement(frameArrs.get(iFrame), score));
	  	}
	  }
	  return ret;
	}
	
	
	/**
	 * Computes beamed notations with correct stem alignments.
	 */
	NotationsCache computeBeamStemAlignments(PVector<FrameArrangement> frameArrangements,
		PVector<ColumnSpacing> optimalMeasureColumnSpacings, NotationsCache notations, ScoreLayouterContext lc)
	{
		NotationsCache ret = NotationsCache.empty;
		//collect actual measure column spacings from all frames
		//(now also including leading spacings and possibly stretched measures)
		Score score = lc.getScore();
		Globals globals = score.getGlobals();
		PVector<ColumnSpacing> columnSpacings = pvec();
		for (FrameArrangement frameArr : frameArrangements)
		{
			for (SystemArrangement systemArr : frameArr.getSystems())
			{
				columnSpacings = columnSpacings.plusAll(systemArr.getColumnSpacings());
			}
		}
		//if not all measures were arranged (because of missing space), add their
		//optimal spacings to the list
		for (int iMeasure = columnSpacings.size(); iMeasure < score.getMeasuresCount(); iMeasure++)
		{
			columnSpacings = columnSpacings.plus(optimalMeasureColumnSpacings.get(iMeasure));
		}
		//go again through all elements, finding beams, and recompute stem alignment
		for (int iMeasure : range(0, score.getMeasuresCount() - 1))
		{
			Column measureColumn = column(score, iMeasure);
			for (Measure measure : measureColumn)
			{
				for (Voice voice : measure.getVoices())
				{
					for (MusicElement element : voice.getElements())
					{
						if (element instanceof Chord)
						{
							Chord chord = (Chord) element;
							//compute each beam only one time (when the end waypoint is found)
							Beam beam = globals.getBeams().get(chord);
							if (beam != null && beam.getLastWaypoint().getChord() == chord)
							{
								ret = ret.merge(beamedStemAlignmentNotationsStrategy.computeNotations(lc.getScore(),
									beam, columnSpacings, notations));
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	
	/**
	 * Creates all {@link ScoreFrameLayout}s.
	 */
	PVector<ScoreFrameLayout> createScoreFrameLayouts(PVector<FrameArrangement> frameArrs,
		NotationsCache notations, ScoreLayouterContext lc)
	{
		PVector<ScoreFrameLayout> ret = pvec();
		PVector<ContinuedElement> continuedElements = pvec();
		for (FrameArrangement frameArr : frameArrs)
		{
			ScoreFrameLayout sfl = scoreFrameLayoutStrategy.computeScoreFrameLayout(
				frameArr, notations, continuedElements, lc);
			ret = ret.plus(sfl);
			continuedElements = sfl.getContinuedElements();
		}
		return ret;
	}
	

}
