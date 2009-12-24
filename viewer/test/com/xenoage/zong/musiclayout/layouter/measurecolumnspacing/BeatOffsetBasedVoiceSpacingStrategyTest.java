package com.xenoage.zong.musiclayout.layouter.measurecolumnspacing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import com.xenoage.zong.util.ArrayTools;
import com.xenoage.util.Delta;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.format.ScoreFormat;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.io.musicxml.in.MxlScoreDocumentFileInputTest;
import com.xenoage.zong.layout.frames.ScoreFrameChain;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterContext;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterTest;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.layouter.notation.NotationStrategy;
import com.xenoage.zong.musiclayout.spacing.horizontal.BeatOffset;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;

import org.junit.Test;


/**
 * Test cases for the {@link BeatOffsetBasedVoiceSpacingStrategy}.
 *
 * @author Andreas Wenger
 */
public class BeatOffsetBasedVoiceSpacingStrategyTest
{
	
  
  @Test public void testComputeSharedBeats()
  {
  	BeatOffsetBasedVoiceSpacingStrategy strategy = new BeatOffsetBasedVoiceSpacingStrategy();
    //list 1 beats: 0  3   789
    //list 2 beats: 0    5 7 9
    //shared beats: 0      7 9
    SpacingElement[] list1 = new SpacingElement[] {
      new SpacingElement(null, fr(0), 0f),
      new SpacingElement(null, fr(3), 0f),
      new SpacingElement(null, fr(7), 0f),
      new SpacingElement(null, fr(8), 0f),
      new SpacingElement(null, fr(9), 0f)};
    BeatOffset[] list2 = new BeatOffset[] {
      new BeatOffset(fr(0), 0f),
      new BeatOffset(fr(5), 0f),
      new BeatOffset(fr(7), 0f),
      new BeatOffset(fr(9), 0f)};
    List<BeatOffset> res = strategy.computeSharedBeats(list1, ArrayTools.toBeatOffsetArrayList(list2));
    assertEquals(3, res.size());
    assertEquals(fr(0), res.get(0).getBeat());
    assertEquals(fr(7), res.get(1).getBeat());
    assertEquals(fr(9), res.get(2).getBeat());
    //list 1 beats: 01 3
    //list 2 beats:   2 4  
    //shared beats: (none)
    list1 = new SpacingElement[] {
      new SpacingElement(null, fr(0), 0f),
      new SpacingElement(null, fr(1), 0f),
      new SpacingElement(null, fr(3), 0f)};
    list2 = new BeatOffset[] {
      new BeatOffset(fr(2), 0f),
      new BeatOffset(fr(4), 0f)};
    res = strategy.computeSharedBeats(list1, ArrayTools.toBeatOffsetArrayList(list2));
    assertEquals(0, res.size());
    //list 1 beats: 000033
    //list 2 beats: 0123
    //shared beats: 0 and 3 (no duplicate values!)
    list1 = new SpacingElement[] {
      new SpacingElement(null, fr(0), 0f),
      new SpacingElement(null, fr(0), 0f),
      new SpacingElement(null, fr(0), 0f),
      new SpacingElement(null, fr(0), 0f),
      new SpacingElement(null, fr(3), 0f),
      new SpacingElement(null, fr(3), 0f)};
    list2 = new BeatOffset[] {
      new BeatOffset(fr(0), 0f),
      new BeatOffset(fr(1), 0f),
      new BeatOffset(fr(2), 0f),
      new BeatOffset(fr(3), 0f)};
    res = strategy.computeSharedBeats(list1, ArrayTools.toBeatOffsetArrayList(list2));
    assertEquals(2, res.size());
    assertEquals(fr(0), res.get(0).getBeat());
    assertEquals(fr(3), res.get(1).getBeat());
  }
  
  
  @Test public void computeVoiceSpacingTest1()
  {
    //voice spacing:
    //beats:   ..2.4..78
    //offsets:   | |  || 
    //           | |  |�- 6
    //           | |  �-- 4
    //           | �----- 2
    //           �------- 1
    VoiceSpacing voiceSpacing = new VoiceSpacing(createVoiceWith1InterlineSpace(),
      new SpacingElement[] {
        new SpacingElement(null, fr(2), 1f),
        new SpacingElement(null, fr(4), 2f),
        new SpacingElement(null, fr(7), 4f),
        new SpacingElement(null, fr(8), 6f)});
    //given beat offsets:
    //beats:   0...4...8
    //offsets: |   |   | 
    //         |   |   �- 20
    //         |   �----- 8
    //         �--------- 0
    BeatOffset[] beatOffsets = new BeatOffset[] {
      new BeatOffset(fr(0), 0f),
      new BeatOffset(fr(4), 8f),
      new BeatOffset(fr(8), 20f)};
    //shared beats: 4, 8.
    //
    //resulting spacing:
    //beats:   ..2.4..78
    //offsets:   | |  || 
    //           | |  |�- (20 - 8) / (6 - 2) * (6 - 2) + 8 = 20 } (shared beats 4 and 8)
    //           | |  �-- (20 - 8) / (6 - 2) * (4 - 2) + 8 = 14 } (shared beats 4 and 8)
    //           | �-----  (8 - 0) / (2 - 0) * (2 - 0) + 0 =  8 } (shared beats 0 and 4)
    //           �-------  (8 - 0) / (2 - 0) * (1 - 0) + 0 =  4 } (shared beats 0 and 4)
    BeatOffsetBasedVoiceSpacingStrategy strategy = new BeatOffsetBasedVoiceSpacingStrategy();
    SpacingElement[] finalSpacing =
      strategy.computeVoiceSpacing(voiceSpacing, ArrayTools.toBeatOffsetArrayList(beatOffsets)).getSpacingElements();
    float is = new ScoreFormat().getInterlineSpace();
    assertEquals(4, finalSpacing.length);
    assertEquals(fr(2), finalSpacing[0].getBeat());
    assertEquals(4f / is, finalSpacing[0].getOffset(), Delta.DELTA_FLOAT_2);
    assertEquals(fr(4), finalSpacing[1].getBeat());
    assertEquals(8f / is, finalSpacing[1].getOffset(), Delta.DELTA_FLOAT_2);
    assertEquals(fr(7), finalSpacing[2].getBeat());
    assertEquals(14f / is, finalSpacing[2].getOffset(), Delta.DELTA_FLOAT_2);
    assertEquals(fr(8), finalSpacing[3].getBeat());
    assertEquals(20f / is, finalSpacing[3].getOffset(), Delta.DELTA_FLOAT_2);
  }
  
  
  @Test public void computeVoiceSpacingTest2()
  {
    //voice spacing:
    //beats:   0.2
    //offsets: | | 
    //         | �- 2
    //         �--- 0
    VoiceSpacing voiceSpacing = new VoiceSpacing(createVoiceWith1InterlineSpace(),
      new SpacingElement[] {
        new SpacingElement(null, fr(0), 0f),
        new SpacingElement(null, fr(2), 2f)});
    //given beat offsets:
    //beats:   0.2
    //offsets: | | 
    //         | �- 2
    //         �--- 0
    BeatOffset[] beatOffsets = new BeatOffset[] {
      new BeatOffset(fr(0), 0f),
      new BeatOffset(fr(2), 2f)};
    //shared beats: 0, 2.
    //
    //resulting spacing:
    //beats:   0.2
    //offsets: | | 
    //         | �- 2
    //         �--- 0
    BeatOffsetBasedVoiceSpacingStrategy strategy = new BeatOffsetBasedVoiceSpacingStrategy();
    SpacingElement[] finalSpacing =
      strategy.computeVoiceSpacing(voiceSpacing, ArrayTools.toBeatOffsetArrayList(beatOffsets)).getSpacingElements();
    float is = new ScoreFormat().getInterlineSpace();
    assertEquals(2, finalSpacing.length);
    assertEquals(fr(0), finalSpacing[0].getBeat());
    assertEquals(0f / is, finalSpacing[0].getOffset(), Delta.DELTA_FLOAT);
    assertEquals(fr(2), finalSpacing[1].getBeat());
    assertEquals(2f / is, finalSpacing[1].getOffset(), Delta.DELTA_FLOAT);
  }
  
  
  @Test public void computeVoiceSpacingTest3()
  {
    //voice spacing:
    //beats:   0.2
    //offsets: | | 
    //         | �- 2
    //         �--- 0
    VoiceSpacing voiceSpacing = new VoiceSpacing(createVoiceWith1InterlineSpace(),
      new SpacingElement[] {
        new SpacingElement(null, fr(0), 0f),
        new SpacingElement(null, fr(2), 2f)});
    //given beat offsets:
    //beats:   0.2
    //offsets: | | 
    //         | �- 6
    //         �--- 2
    BeatOffset[] beatOffsets = new BeatOffset[] {
      new BeatOffset(fr(0), 2f),
      new BeatOffset(fr(2), 6f)};
    //shared beats: 0, 2.
    //
    //resulting spacing:
    //beats:   0.2
    //offsets: | | 
    //         | �- 6
    //         �--- 2
    BeatOffsetBasedVoiceSpacingStrategy strategy = new BeatOffsetBasedVoiceSpacingStrategy();
    SpacingElement[] finalSpacing =
      strategy.computeVoiceSpacing(voiceSpacing, ArrayTools.toBeatOffsetArrayList(beatOffsets)).getSpacingElements();
    float is = new ScoreFormat().getInterlineSpace();
    assertEquals(2, finalSpacing.length);
    assertEquals(fr(0), finalSpacing[0].getBeat());
    assertEquals(2f / is, finalSpacing[0].getOffset(), Delta.DELTA_FLOAT);
    assertEquals(fr(2), finalSpacing[1].getBeat());
    assertEquals(6f / is, finalSpacing[1].getOffset(), Delta.DELTA_FLOAT);
  }
  
  
  /**
   * Test file "BeatOffsetBasedVoiceSpacingStrategyTest-1.xml".
   */
  @Test public void computeVoiceSpacing_File1()
  {
    Score score = MxlScoreDocumentFileInputTest.loadXMLTestScore("BeatOffsetBasedVoiceSpacingStrategyTest-1.xml");
    
    //compute voice spacing for voice 0
    ScoreLayouterContext context = new ScoreLayouterContext(
    	ScoreFrameChain.createLimitedChain(score, null), null);
    NotationStrategy notationStrategy = ScoreLayouterTest.getNotationStrategy();
    NotationsCache notations = notationStrategy.computeNotations(context);
    VoiceSpacing voiceSpacing = new SeparateVoiceSpacingStrategy().computeVoiceSpacing(
    	score.getStaff(0).getMeasures().get(0).getVoices().get(0), false, notations,
    	score.getController().getMeasureBeats(0));
    
    //use 2 mm interline space
    float is = 2;
    score.getScoreFormat().setInterlineSpace(is);
    
    //use the following beat offsets
    ArrayList<BeatOffset> beatOffsets = new ArrayList<BeatOffset>();
    beatOffsets.add(new BeatOffset(fr(0), 5f));
    beatOffsets.add(new BeatOffset(fr(1), 10f));
    beatOffsets.add(new BeatOffset(fr(2), 15f));
    
    //create voice spacing based on beat offsets
    BeatOffsetBasedVoiceSpacingStrategy strategy = new BeatOffsetBasedVoiceSpacingStrategy();
    SpacingElement[] finalSpacing =
      strategy.computeVoiceSpacing(voiceSpacing, beatOffsets).getSpacingElements();
    
    //check for right offsets
    assertEquals(7, finalSpacing.length);
    assertEquals(fr(0), finalSpacing[3].getBeat());
    assertEquals(5f / is, finalSpacing[3].getOffset(), Delta.DELTA_FLOAT);
    assertEquals(fr(1), finalSpacing[4].getBeat());
    assertEquals(10f / is, finalSpacing[4].getOffset(), Delta.DELTA_FLOAT);
    assertEquals(fr(2), finalSpacing[5].getBeat());
    assertEquals(15f / is, finalSpacing[5].getOffset(), Delta.DELTA_FLOAT);
  }
  
  
  /**
   * Test file "BeatOffsetBasedVoiceSpacingStrategyTest-2.xml".
   */
  @Test public void computeVoiceSpacing_File2()
  {
    Score score = MxlScoreDocumentFileInputTest.loadXMLTestScore("BeatOffsetBasedVoiceSpacingStrategyTest-2.xml");
    
    //compute voice spacing for voices 0 and 1
    ScoreLayouterContext context = new ScoreLayouterContext(
    	ScoreFrameChain.createLimitedChain(score, null), null);
    NotationStrategy notationStrategy = ScoreLayouterTest.getNotationStrategy();
    NotationsCache notations = notationStrategy.computeNotations(context);
    for (int voice = 0; voice <= 1; voice++)
    {
    	VoiceSpacing voiceSpacing = new SeparateVoiceSpacingStrategy().computeVoiceSpacing(
      	score.getStaff(0).getMeasures().get(0).getVoices().get(voice), false, notations,
      	score.getController().getMeasureBeats(0));
	    
	    //use 2 mm interline space
	    float is = 2;
	    score.getScoreFormat().setInterlineSpace(is);
	    
	    //use the following beat offsets
	    ArrayList<BeatOffset> beatOffsets = new ArrayList<BeatOffset>();
	    beatOffsets.add(new BeatOffset(fr(0), 5f));
	    beatOffsets.add(new BeatOffset(fr(1), 10f));
	    beatOffsets.add(new BeatOffset(fr(2), 15f));
	    
	    //create voice spacing based on beat offsets
	    BeatOffsetBasedVoiceSpacingStrategy strategy = new BeatOffsetBasedVoiceSpacingStrategy();
	    SpacingElement[] finalSpacing =
	      strategy.computeVoiceSpacing(voiceSpacing, beatOffsets).getSpacingElements();
	    
	    //voice 1 is missing 3 elements (clef, key, time)
	    int m = (voice == 1 ? 3 : 0);
	    
	    //check for right offsets
	    assertEquals(7 - m, finalSpacing.length);
	    assertEquals(fr(0), finalSpacing[3 - m].getBeat());
	    assertEquals(5f / is, finalSpacing[3 - m].getOffset(), Delta.DELTA_FLOAT);
	    assertEquals(fr(1), finalSpacing[4 - m].getBeat());
	    assertEquals(10f / is, finalSpacing[4 - m].getOffset(), Delta.DELTA_FLOAT);
	    assertEquals(fr(2), finalSpacing[5 - m].getBeat());
	    assertEquals(15f / is, finalSpacing[5 - m].getOffset(), Delta.DELTA_FLOAT);
    }
  }
  
  
  private static Fraction fr(int quarters)
  {
    return new Fraction(quarters, 4);
  }
  
  
  private Voice createVoiceWith1InterlineSpace()
  {
  	Measure measure = new Measure(null);
  	return measure.addVoice();
  }

}
