package com.xenoage.zong.musiclayout.layouter;

import static org.junit.Assert.fail;

import java.io.FileInputStream;

import org.junit.Test;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.io.musicxml.in.MxlScoreDocumentFileInput;
import com.xenoage.zong.io.musicxml.in.MxlScoreDocumentFileInputTest;
import com.xenoage.zong.layout.frames.ScoreFrame;
import com.xenoage.zong.layout.frames.ScoreFrameChain;
import com.xenoage.zong.layout.frames.ScoreFrameChainTry;
import com.xenoage.zong.musiclayout.ScoreLayout;
import com.xenoage.zong.musiclayout.layouter.notation.AccidentalsAlignmentStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.ArticulationsAlignmentStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.NotationStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.NotesAlignmentStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.StemAlignmentStrategy;
import com.xenoage.zong.musiclayout.layouter.notation.StemDirectionStrategy;


/**
 * Test cases for the {@link ScoreLayouter} class.
 * 
 * @author Andreas Wenger
 */
public class ScoreLayouterTest
{
	
	/**
	 * Computes and returns the {@link ScoreLayout} of the
	 * given {@link Score} (on a 1x1 m frame).
	 */
	public static ScoreLayout createScoreLayout(Score score)
	{
		ScoreLayouter layouter = new ScoreLayouter(ScoreFrameChainTry.createScoreFrame1m1m(score), null);
		return layouter.createLayout();
	}
	
	
	/**
   * Try to layout all official MusicXML 1.1 and 2.0 sample files.
   * We can not test for the correct layout of course, but at least
   * we want to have no exceptions.
   */
  @Test public void testSampleFiles()
  {
  	for (String file : MxlScoreDocumentFileInputTest.getSampleFiles())
    {
    	try
    	{
    		Score score = new MxlScoreDocumentFileInput().read(file).getScore(0);
    		ScoreFrame scoreFrame = new ScoreFrame(new Point2f(0, 0), new Size2f(150, 10000));
    		new ScoreLayouter(ScoreFrameChain.createLimitedChain(score, scoreFrame), null).createLayoutWithExceptions();
    	}
      catch (Exception ex)
      {
        ex.printStackTrace();
        fail("Failed to layout file: " + file);
      }
    }
  }
	
	
	public static NotationStrategy getNotationStrategy()
	{
		return new NotationStrategy(
			new StemDirectionStrategy(),
			new NotesAlignmentStrategy(),
			new AccidentalsAlignmentStrategy(),
			new StemAlignmentStrategy(),
			new ArticulationsAlignmentStrategy());
	}

}
