package com.xenoage.zong.musiclayout.layouter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.xenoage.util.Range;
import com.xenoage.util.math.Point2f;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.music.BeamWaypoint;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.util.MeasureColumn;
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
import com.xenoage.zong.musiclayout.layouter.horizontalsystemfilling.HorizontalSystemFillingStrategy;
import com.xenoage.zong.musiclayout.layouter.measurecolumnspacing.MeasureColumnSpacingStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.NotationStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.ScoreFrameLayoutStrategy;
import com.xenoage.zong.musiclayout.layouter.verticalframefilling.VerticalFrameFillingStrategy;
import com.xenoage.zong.musiclayout.layouter.voicenotation.VoiceStemDirectionNotationsStrategy;
import com.xenoage.zong.musiclayout.spacing.MeasureColumnSpacing;


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
	private final MeasureColumnSpacingStrategy measureColumnSpacingStrategy;
	private final FrameArrangementStrategy frameArrangementStrategy;
	private final BeamedStemAlignmentNotationsStrategy beamedStemAlignmentNotationsStrategy;
	private final ScoreFrameLayoutStrategy scoreFrameLayoutStrategy;
	
	
	/**
	 * Creates a new {@link ScoreLayoutStrategy}.
	 */
	public ScoreLayoutStrategy(NotationStrategy notationStrategy,
		BeamedStemDirectionNotationsStrategy beamedStemDirectionNotationsStrategy,
		VoiceStemDirectionNotationsStrategy voiceStemDirectionNotationsStrategy,
		MeasureColumnSpacingStrategy measureColumnSpacingStrategy, FrameArrangementStrategy frameArrangementStrategy,
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
		NotationsCache notations = notationStrategy.computeNotations(lc);
		//update beamed notations with correct stem directions
		notations.setAll(computeBeamStemDirections(notations, lc));
		//TODO: stem directions dependent on their voice
		for (int iStaff : new Range(0, score.getStavesCount() - 1))
		{
			notations.setAll(voiceStemDirectionNotationsStrategy.computeNotations(iStaff, notations, lc));
		}
		//optimal measure spacings
		ArrayList<MeasureColumnSpacing> optimalMeasureColumnSpacings = computeMeasureSpacings(notations, lc);
		//break into systems and frames
		ArrayList<FrameArrangement> frameArrangements = computeFrameArrangements(optimalMeasureColumnSpacings, notations, lc);
		//system stretching (horizontal)
		frameArrangements = fillSystemsHorizontally(frameArrangements, lc);
		//frame filling (vertical)
		frameArrangements = fillFramesVertically(frameArrangements, lc);
		//compute beams
		notations.setAll(computeBeamStemAlignments(frameArrangements, optimalMeasureColumnSpacings, notations, lc));
		//create score layout from the collected information
		ArrayList<ScoreFrameLayout> scoreFrameLayouts = createScoreFrameLayouts(frameArrangements, notations, lc);
		//create score layout
		return new ScoreLayout(lc.getScore(), scoreFrameLayouts);
	}
	
	
	/**
	 * Computes beamed notations with correct stem directions.
	 */
	NotationsCache computeBeamStemDirections(NotationsCache notations, ScoreLayouterContext lc)
	{
		NotationsCache ret = new NotationsCache();
		//go again through all elements, finding beams, and recompute stem direction
		Score score = lc.getScore();
		for (int iMeasure : new Range(0, score.getMeasuresCount() - 1))
		{
			MeasureColumn measureColumn = score.getMeasureColumn(iMeasure);
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
							if (chord.getBeamWaypoint() != null && chord.getBeamWaypoint().getType() == BeamWaypoint.Type.Stop)
							{
								ret.setAll(beamedStemDirectionNotationsStrategy.computeNotations(
									chord.getBeamWaypoint().getBeam(), notations, lc));
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	
	/**
	 * Computes the {@link MeasureColumnSpacing} for each measure
	 * (without leading spacing).
	 */
	ArrayList<MeasureColumnSpacing> computeMeasureSpacings(NotationsCache notations, ScoreLayouterContext lc)
	{
		Score score = lc.getScore();
		ArrayList<MeasureColumnSpacing> ret = new ArrayList<MeasureColumnSpacing>(score.getMeasuresCount());
		for (int iMeasure : new Range(0, score.getMeasuresCount() - 1))
		{
			ret.add(measureColumnSpacingStrategy.computeMeasureColumnSpacing(score.getMeasureColumn(iMeasure),
				false, notations, score.getScoreHeader().getMeasureColumnHeader(iMeasure), lc).get1()); //TODO: also save optimal voice spacings for later reuse
		}
		return ret;
	}
	
	
	/**
	 * Breaks the given measure column spacings into systems and frames
	 * and saves them in the cache.
	 */
	ArrayList<FrameArrangement> computeFrameArrangements(ArrayList<MeasureColumnSpacing> measureColumnSpacings,
		NotationsCache notations, ScoreLayouterContext lc)
	{
		ScoreFrameChain chain = lc.getScoreFrameChain();
		chain.clearAdditionalFrames(); //clear old stuff
		ArrayList<FrameArrangement> ret = new ArrayList<FrameArrangement>(chain.getLimitedFramesCount());
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
				FrameArrangement frameArr = frameArrangementStrategy.computeFrameArrangement(iMeasure, iSystem,
					scoreFrame.getSize(), measureColumnSpacings, notations, lc);
				if (frameArr.getSystemsCount() > 0)
				{
					//at least one measure in this frame
					ret.add(frameArr);
					if (additionalFrameIteration)
					{
						chain.addAdditionalFrame(scoreFrame);
					}
					iMeasure = frameArr.getSystem(frameArr.getSystemsCount() - 1).getEndMeasureIndex() + 1;
					iSystem += frameArr.getSystemsCount();
				}
				else
				{
					//no space for a measure in this frame. empty frame. but only, if existing frame.
					//do not create endless list of empty additional frames!
					if (!additionalFrameIteration)
					{
						ret.add(new FrameArrangement(new SystemArrangement[0], scoreFrame.getSize()));
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
				ret.add(new FrameArrangement(new SystemArrangement[0], scoreFrame.getSize()));
			}
			
			//next frame
			iFrame++;
		}
		return ret;
	}

	
	/**
	 * Fills the systems horizontally according to the {@link HorizontalSystemFillingStrategy}
	 * of the frame.
	 */
	ArrayList<FrameArrangement> fillSystemsHorizontally(ArrayList<FrameArrangement> frameArrangements,
		ScoreLayouterContext lc)
	{
		ScoreFrameChain chain = lc.getScoreFrameChain();
		ArrayList<FrameArrangement> ret = new ArrayList<FrameArrangement>(frameArrangements.size());
		for (int iFrame : new Range(frameArrangements))
	  {
			FrameArrangement frameArr = frameArrangements.get(iFrame);
			HorizontalSystemFillingStrategy hFill = chain.getFrame(iFrame).getHorizontalSystemFillingStrategy();
			if (hFill != null)
			{
				//apply strategy
				SystemArrangement[] systemArrs = new SystemArrangement[frameArr.getSystemsCount()];
			  for (int iSystem = 0; iSystem < frameArr.getSystemsCount(); iSystem++)
			  {
			  	SystemArrangement oldSystemArr = frameArr.getSystem(iSystem);
			  	float usableWidth = frameArr.getUsableSize().width - oldSystemArr.getMarginLeft() - oldSystemArr.getMarginRight();
			  	systemArrs[iSystem] = hFill.computeSystemArrangement(oldSystemArr, usableWidth);
			  }
			  ret.add(new FrameArrangement(systemArrs, frameArr.getUsableSize()));
			}
			else
			{
				//unmodified frame
				ret.add(frameArr);
			}
		}
		return ret;
	}
	
	
	/**
	 * Fills the frames vertically according to the {@link VerticalFrameFillingStrategy}
	 * of the frame.
	 */
	ArrayList<FrameArrangement> fillFramesVertically(ArrayList<FrameArrangement> frameArrs,
		ScoreLayouterContext lc)
	{
		ScoreFrameChain chain = lc.getScoreFrameChain();
		ArrayList<FrameArrangement> ret = new ArrayList<FrameArrangement>(frameArrs.size());
		Score score = lc.getScore();
	  for (int iFrame : new Range(frameArrs))
	  {
	  	VerticalFrameFillingStrategy vFill = chain.getFrame(iFrame).getVerticalFrameFillingStrategy();
	  	if (vFill != null)
	  	{
	  		ret.add(vFill.computeFrameArrangement(frameArrs.get(iFrame), score));
	  	}
	  }
	  return ret;
	}
	
	
	/**
	 * Computes beamed notations with correct stem alignments.
	 */
	NotationsCache computeBeamStemAlignments(ArrayList<FrameArrangement> frameArrangements,
		ArrayList<MeasureColumnSpacing> optimalMeasureColumnSpacings, NotationsCache notations, ScoreLayouterContext lc)
	{
		NotationsCache ret = new NotationsCache();
		//collect actual measure column spacings from all frames
		//(now also including leading spacings and possibly stretched measures)
		Score score = lc.getScore();
		ArrayList<MeasureColumnSpacing> measureColumnSpacings = new ArrayList<MeasureColumnSpacing>(score.getMeasuresCount());
		for (FrameArrangement frameArr : frameArrangements)
		{
			for (int iSystem : new Range(0, frameArr.getSystemsCount() - 1))
			{
				SystemArrangement systemArr = frameArr.getSystem(iSystem);
				for (int iMeasure : new Range(0, systemArr.getMeasureColumnSpacings().length - 1))
				{
					measureColumnSpacings.add(systemArr.getMeasureColumnSpacings()[iMeasure]);
				}
			}
		}
		//if not all measures were arranged (because of missing space), add their
		//optimal spacings to the list
		for (int iMeasure = measureColumnSpacings.size(); iMeasure < score.getMeasuresCount(); iMeasure++)
		{
			measureColumnSpacings.add(optimalMeasureColumnSpacings.get(iMeasure));
		}
		//go again through all elements, finding beams, and recompute stem alignment
		for (int iMeasure : new Range(0, score.getMeasuresCount() - 1))
		{
			MeasureColumn measureColumn = score.getMeasureColumn(iMeasure);
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
							if (chord.getBeamWaypoint() != null && chord.getBeamWaypoint().getType() == BeamWaypoint.Type.Stop)
							{
								ret.setAll(beamedStemAlignmentNotationsStrategy.computeNotations(
									chord.getBeamWaypoint().getBeam(), measureColumnSpacings, notations));
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
	ArrayList<ScoreFrameLayout> createScoreFrameLayouts(ArrayList<FrameArrangement> frameArrs,
		NotationsCache notations, ScoreLayouterContext lc)
	{
		ArrayList<ScoreFrameLayout> ret = new ArrayList<ScoreFrameLayout>(frameArrs.size());
		List<ContinuedElement> continuedElements = new LinkedList<ContinuedElement>();
		for (FrameArrangement frameArr : frameArrs)
		{
			ScoreFrameLayout sfl = scoreFrameLayoutStrategy.computeScoreFrameLayout(
				frameArr, notations, continuedElements, lc);
			ret.add(sfl);
			continuedElements = sfl.getContinuedElements();
		}
		return ret;
	}
	

}
