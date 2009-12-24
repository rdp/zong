package com.xenoage.zong.musiclayout.layouter;

import com.xenoage.zong.Zong;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.VocByString;
import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.layout.frames.ScoreFrameChain;
import com.xenoage.zong.musiclayout.ScoreFrameLayout;
import com.xenoage.zong.musiclayout.ScoreLayout;
import com.xenoage.zong.musiclayout.layouter.arrangement.FrameArrangementStrategy;
import com.xenoage.zong.musiclayout.layouter.arrangement.SystemArrangementStrategy;
import com.xenoage.zong.musiclayout.layouter.beamednotation.BeamedStemAlignmentNotationsStrategy;
import com.xenoage.zong.musiclayout.layouter.beamednotation.BeamedStemDirectionNotationsStrategy;
import com.xenoage.zong.musiclayout.layouter.measurecolumnspacing.BarlinesBeatOffsetsStrategy;
import com.xenoage.zong.musiclayout.layouter.measurecolumnspacing.BeatOffsetBasedVoiceSpacingStrategy;
import com.xenoage.zong.musiclayout.layouter.measurecolumnspacing.BeatOffsetsStrategy;
import com.xenoage.zong.musiclayout.layouter.measurecolumnspacing.MeasureColumnSpacingStrategy;
import com.xenoage.zong.musiclayout.layouter.measurecolumnspacing.MeasureLeadingSpacingStrategy;
import com.xenoage.zong.musiclayout.layouter.measurecolumnspacing.SeparateVoiceSpacingStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.AccidentalsAlignmentStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.ArticulationsAlignmentStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.NotationStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.NotesAlignmentStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.StemAlignmentStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.StemDirectionStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.BeamStampingStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.CurvedLineStampingStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.DirectionStampingStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.LyricStampingStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.NoVoiceElementStampingStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.ScoreFrameLayoutStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.StaffStampingsStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.TupletStampingStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.VoiceElementStampingStrategy;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.VoltaStampingStrategy;
import com.xenoage.zong.musiclayout.layouter.voicenotation.VoiceStemDirectionNotationsStrategy;
import com.xenoage.util.logging.Log;


/**
 * A score layouter creates the content for
 * score frames from a given score.
 * 
 * @author Andreas Wenger
 */
public class ScoreLayouter
{
  
  //the score layout created by this layouter
  private ScoreLayout layout = null;
  
  //strategy for the layouter and its context
  private ScoreLayoutStrategy strategy;
  private ScoreLayouterContext context;
  
  
  /**
   * Creates a new ScoreLayouter for the given list of connected score frames.
   */
  public ScoreLayouter(ScoreFrameChain scoreFrameChain, SymbolPool symbolPool)
  {
    this.context = new ScoreLayouterContext(scoreFrameChain, symbolPool);
    this.strategy = createStrategyTree();
  }
  
  
  /**
   * Recomputes the whole layout and returns it.
   * If something fails, an error layout is returned.
   */
  public ScoreLayout createLayout()
  {
  	try
  	{
  		layout = createLayoutWithExceptions();
  	}
  	catch (Exception ex)
  	{
  		//exception during the layouting process.
  		//show error page, but still allow saving or other things
  		Log.log(ex);
  		layout = ScoreLayout.createErrorLayout(context.getScore());
  	}
    
    //set the layout of the frames - TODO: move elsewhere?
  	ScoreFrameChain chain = context.getScoreFrameChain();
    int scoreFrameIndex = 0;
    for (ScoreFrameLayout sfLayout : layout.getScoreFrameLayouts())
    {
    	ScoreFrame scoreFrame = chain.getFrame(scoreFrameIndex);
      if (scoreFrame != null)
      {
        scoreFrame.setLayout(sfLayout);
      }
      else
      {
        break;
      }
      scoreFrameIndex++;
    }
    
    return layout;
  }
  
  
  /**
   * Recomputes the whole layout and returns it.
   * If something fails, an exception is thrown.
   */
  ScoreLayout createLayoutWithExceptions()
  {
  	return strategy.computeScoreLayout(context);
  }
  
  
  /**
   * Gets the score this layouter is working with.
   */
  public Score getScore()
  {
    return context.getScore();
  }
  
  
  /**
   * Gets the current layout.
   */
  public ScoreLayout getLayout()
  {
    return layout;
  }
  
  
  /**
   * Creates the strategy tree.
   * See "Dokumentation/Skizzen/Layoutengine-12-2008/Baum.odg"
   */
  ScoreLayoutStrategy createStrategyTree()
  {
  	//notation subtree
  	NotationStrategy notationStrategy = new NotationStrategy(
			new StemDirectionStrategy(),
			new NotesAlignmentStrategy(),
			new AccidentalsAlignmentStrategy(),
			new StemAlignmentStrategy(),
			new ArticulationsAlignmentStrategy());
  	//measure column subtree
  	MeasureColumnSpacingStrategy measureColumnSpacingStrategy = new MeasureColumnSpacingStrategy(
			new SeparateVoiceSpacingStrategy(),
			new BeatOffsetsStrategy(),
			new BarlinesBeatOffsetsStrategy(),
			new BeatOffsetBasedVoiceSpacingStrategy(),
			new MeasureLeadingSpacingStrategy(
				notationStrategy));
  	//complete tree
  	return new ScoreLayoutStrategy(
  		notationStrategy,
  		new BeamedStemDirectionNotationsStrategy(
  			notationStrategy),
  		new VoiceStemDirectionNotationsStrategy(
  			notationStrategy),
  		measureColumnSpacingStrategy,
  		new FrameArrangementStrategy(
  			new SystemArrangementStrategy(
  				measureColumnSpacingStrategy)),
  		new BeamedStemAlignmentNotationsStrategy(),
  		new ScoreFrameLayoutStrategy(
  			new StaffStampingsStrategy(),
  			new VoiceElementStampingStrategy(),
  			new NoVoiceElementStampingStrategy(),
  			new BeamStampingStrategy(),
  			new CurvedLineStampingStrategy(),
  			new LyricStampingStrategy(),
  			new VoltaStampingStrategy(),
  			new DirectionStampingStrategy(),
  			new TupletStampingStrategy()));
  }
  
  
  
  /**
   * Gets the localized name of the given layouter strategy class, e.g.
   * "Empty staff lines over the whole page".
   * 
   * TIDY: move elsewhere
   */
  @SuppressWarnings("unchecked")
  public static String getName(Class strategyClass)
  {
  	String className = strategyClass.getName();
    if (className.startsWith(Zong.PACKAGE + "."))
      className = className.substring((Zong.PACKAGE + ".").length());
    return Lang.get(new VocByString("Strategy", className));
  }
  

}
