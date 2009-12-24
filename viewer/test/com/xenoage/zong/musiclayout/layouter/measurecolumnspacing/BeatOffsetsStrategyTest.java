package com.xenoage.zong.musiclayout.layouter.measurecolumnspacing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


import com.xenoage.util.Delta;
import com.xenoage.util.Range;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.key.TraditionalKey;
import com.xenoage.zong.data.music.time.NormalTime;
import com.xenoage.zong.io.musicxml.in.MxlScoreDocumentFileInputTest;
import com.xenoage.zong.musiclayout.spacing.horizontal.BeatOffset;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;
import com.xenoage.zong.util.ArrayTools;


/**
 * Test cases for a {@link BeatOffsetsStrategy}.
 * 
 * @author Andreas Wenger
 */
public class BeatOffsetsStrategyTest
{
	
	private BeatOffsetsStrategy strategy;
  
  private final float width_1_8 = 1.5f;
  private final float width_1_6 = 1.7f;
  private final float width_1_4 = 2f;
  private final float width_3_8 = 2.5f;
  private final float width_1_2 = 3f;
  private final float width_1_1 = 5f;
  
  private final Fraction dur_1_8 = new Fraction(1, 8);
  private final Fraction dur_1_6 = new Fraction(1, 6);
  private final Fraction dur_1_4 = new Fraction(1, 4);
  private final Fraction dur_3_8 = new Fraction(3, 8);
  private final Fraction dur_1_2 = new Fraction(1, 2);
  private final Fraction dur_1_1 = new Fraction(1, 1);
  
  
  @Before public void setUp()
  {
  	strategy = new BeatOffsetsStrategy();
  	strategy.guaranteedMinimalDistance = 0.5f;
  }
  

  @Test public void testComputeMeasureBeats()
  {
    //must have 5 beats at 0, 2, 3, 4, 8.
    List<Fraction> beats = strategy.computeVoicesBeats(createVoiceSpacings(createTestScore1Voice())).getLinkedList();
    assertEquals(5, beats.size());
    assertEquals(new Fraction(0), beats.get(0));
    assertEquals(new Fraction(2, 8), beats.get(1));
    assertEquals(new Fraction(3, 8), beats.get(2));
    assertEquals(new Fraction(4, 8), beats.get(3));
    assertEquals(new Fraction(8, 8), beats.get(4));
  }
  
  
  @Test public void testComputeDistance()
  {
    Voice voice = createTestScore1Voice().getStaff(0).getMeasures().get(0).getVoices().get(0);
    SpacingElement[] spacings = createTestSpacingElements1Voice();
    LinkedList<BeatOffset> emptyList = new LinkedList<BeatOffset>();
    //distance: the offsets of the notes and rests are interesting,
    //not the ones of the clefs, key signatures and time signatures,
    //so the method has to use the last occurrence of a beat.
    //distance between beat 0 and 4: must be 6
    float distance = strategy.computeMinimalDistance(
      fr(0), fr(4), false, voice, spacings, emptyList, 1);
    assertEquals(6, distance, Delta.DELTA_FLOAT);
    //distance between beat 0 and 5: must be 0 (beat 5 isn't used)
    distance = strategy.computeMinimalDistance(
      fr(0), fr(5), false, voice, spacings, emptyList, 1);
    assertEquals(0, distance, Delta.DELTA_FLOAT);
    //distance between beat 0 and 2: must be 2
    distance = strategy.computeMinimalDistance(
      fr(0), fr(2), false,voice, spacings, emptyList, 1);
    assertEquals(2, distance, Delta.DELTA_FLOAT);
    //distance between beat 5 and 8: must be 0 (beat 5 isn't used)
    distance = strategy.computeMinimalDistance(
      fr(5), fr(8), false, voice, spacings, emptyList, 1);
    assertEquals(0, distance, Delta.DELTA_FLOAT);
  }
  
  
  /**
   * Compute offsets of the common beats.
   */
  @Test public void computeBeatOffsets1()
  {
    Score score = createTestScore3Voices();
    
    BeatOffset[] beatOffsets = strategy.computeBeatOffsetsFromVoiceSpacings(
    	createVoiceSpacings(score), new Fraction(4, 4)).toArray(new BeatOffset[0]); //TIDY
    float is = score.getScoreFormat().getInterlineSpace();
    
    //2: half note, 4: quarter note, 8: eighth note, 3: quarter triplet
    //^: dominating voice
    //voice 1:   | 4     4     4     4     |     
    //                               ^^^^^
    //voice 2:   | 8  8  8  8  2           |
    //             ^^^^^^^^^^^^
    //voice 3:   | 3   3   3   8  8  4     |
    //                         ^^^^^^
    //used:        *  ** * **  *  *  *     *
    //checked:     *     *     *     *     *
    assertEquals(10, beatOffsets.length);
    assertEquals(new Fraction(0), beatOffsets[0].getBeat());
    assertEquals((0)*is, beatOffsets[0].getOffsetMm(), Delta.DELTA_FLOAT_2);
    assertEquals(new Fraction(1, 4), beatOffsets[3].getBeat());
    assertEquals((2*width_1_8)*is, beatOffsets[3].getOffsetMm(), Delta.DELTA_FLOAT_2);
    assertEquals(new Fraction(2, 4), beatOffsets[6].getBeat());
    assertEquals((4*width_1_8)*is, beatOffsets[6].getOffsetMm(), Delta.DELTA_FLOAT_2);
    assertEquals(new Fraction(3, 4), beatOffsets[8].getBeat());
    assertEquals((6*width_1_8)*is, beatOffsets[8].getOffsetMm(), Delta.DELTA_FLOAT_2);
    assertEquals(new Fraction(4, 4), beatOffsets[9].getBeat());
    assertEquals((6*width_1_8+width_1_4)*is, beatOffsets[9].getOffsetMm(), Delta.DELTA_FLOAT_2);
  }
  
  
  /**
   * Compute offsets of the common beats,
   * this time for an incomplete measure.
   */
  @Test public void computeBeatOffsets2()
  {
  	Score score = createTestScore3VoicesIncomplete();
    BeatOffset[] beatOffsets = strategy.computeBeatOffsetsFromVoiceSpacings(
    	createVoiceSpacings(score), new Fraction(4, 4)).toArray(new BeatOffset[0]); //TIDY
    float is = score.getScoreFormat().getInterlineSpace();
    
    //2: half note, 4: quarter note, 8: eighth note, 3: quarter triplet, x: missing (empty)
    //^: dominating voice
    //voice 1:   | 4     4     4     8  xxx|
    //                         ^^^^^^^^^
    //voice 2:   | 8  8  8  8  4     xxxxxx|
    //             ^^^^^^^^^^^^
    //voice 3:   | 3   3   3   8  xxxxxxxxx|
    //                         
    //used:        *  ** * **  *     *     *
    //checked:     *     *     *     *     *
    
    assertEquals(9, beatOffsets.length);
    assertEquals(new Fraction(0), beatOffsets[0].getBeat());
    assertEquals((0)*is, beatOffsets[0].getOffsetMm(), Delta.DELTA_FLOAT_2);
    assertEquals(new Fraction(1, 4), beatOffsets[3].getBeat());
    assertEquals((2*width_1_8)*is, beatOffsets[3].getOffsetMm(), Delta.DELTA_FLOAT_2);
    assertEquals(new Fraction(2, 4), beatOffsets[6].getBeat());
    assertEquals((4*width_1_8)*is, beatOffsets[6].getOffsetMm(), Delta.DELTA_FLOAT_2);
    assertEquals(new Fraction(3, 4), beatOffsets[7].getBeat());
    assertEquals((4*width_1_8+width_1_4)*is, beatOffsets[7].getOffsetMm(), Delta.DELTA_FLOAT_2);
    assertEquals(new Fraction(4, 4), beatOffsets[8].getBeat());
    assertEquals((4*width_1_8+width_1_4+width_1_8)*is, beatOffsets[8].getOffsetMm(), Delta.DELTA_FLOAT_2);
  }
  
  
  /**
   * Test file "BeatOffsetsStrategyTest-1.xml".
   */
  @Test public void computeBeatOffsets_File1()
  {
    Score score = MxlScoreDocumentFileInputTest.loadXMLTestScore("BeatOffsetsStrategyTest-1.xml");
    
    LinkedList<VoiceSpacing> voiceSpacings = createVoiceSpacings(score);
    BeatOffset[] beatOffsets = strategy.computeBeatOffsetsFromVoiceSpacings(
    	voiceSpacings, new Fraction(3, 4)).toArray(new BeatOffset[0]); //TIDY
    
    //file must have 5 beat offsets with increasing mm offsets
    assertEquals(5, beatOffsets.length);
    assertEquals(new Fraction(0, 4), beatOffsets[0].getBeat());
    assertEquals(new Fraction(1, 4), beatOffsets[1].getBeat());
    assertEquals(new Fraction(2, 4), beatOffsets[2].getBeat());
    assertEquals(new Fraction(5, 8), beatOffsets[3].getBeat());
    assertEquals(new Fraction(3, 4), beatOffsets[4].getBeat());
    for (int i = 0; i < beatOffsets.length - 1; i++)
    {
    	assertTrue(beatOffsets[i].getOffsetMm() < beatOffsets[i + 1].getOffsetMm());
    }
    
    //distance between beat 1/4 and 2/4 must be width_1_4
    float is = score.getScoreFormat().getInterlineSpace();
    assertEquals(width_1_4 * is, beatOffsets[2].getOffsetMm() - beatOffsets[1].getOffsetMm(), Delta.DELTA_FLOAT_2);
  }
  
  
  /**
   * Test file "BeatOffsetsStrategyTest-2.xml".
   */
  @Test public void computeBeatOffsets_File2()
  {
    Score score = MxlScoreDocumentFileInputTest.loadXMLTestScore("BeatOffsetsStrategyTest-2.xml");
    
    LinkedList<VoiceSpacing> voiceSpacings = createVoiceSpacings(score);
    BeatOffset[] beatOffsets = strategy.computeBeatOffsetsFromVoiceSpacings(
    	voiceSpacings, new Fraction(3, 4)).toArray(new BeatOffset[0]); //TIDY
    
    //file must have 6 beat offsets with increasing mm offsets
    assertEquals(6, beatOffsets.length);
    assertEquals(new Fraction(0, 4), beatOffsets[0].getBeat());
    assertEquals(new Fraction(1, 8), beatOffsets[1].getBeat());
    assertEquals(new Fraction(1, 4), beatOffsets[2].getBeat());
    assertEquals(new Fraction(2, 4), beatOffsets[3].getBeat());
    assertEquals(new Fraction(5, 8), beatOffsets[4].getBeat());
    assertEquals(new Fraction(3, 4), beatOffsets[5].getBeat());
    for (int i = 0; i < beatOffsets.length - 1; i++)
    {
    	assertTrue(beatOffsets[i].getOffsetMm() < beatOffsets[i + 1].getOffsetMm());
    }
    
    //distance between beat 1/4 and 2/4 must be width_1_4
    float is = score.getScoreFormat().getInterlineSpace();
    assertEquals(width_1_4 * is, beatOffsets[3].getOffsetMm() - beatOffsets[2].getOffsetMm(), Delta.DELTA_FLOAT_2);
  }
  
  
  /**
   * Test file "BeatOffsetsStrategyTest-3.xml".
   */
  @Test public void computeBeatOffsets_File3()
  {
    Score score = MxlScoreDocumentFileInputTest.loadXMLTestScore("BeatOffsetsStrategyTest-3.xml");
    
    LinkedList<VoiceSpacing> voiceSpacings = createVoiceSpacings(score);
    BeatOffset[] beatOffsets = strategy.computeBeatOffsetsFromVoiceSpacings(
    	voiceSpacings, new Fraction(5, 4)).toArray(new BeatOffset[0]); //TIDY
    
    //file must have 8 beat offsets with increasing mm offsets
    //special difficulty: last eighth note must be further to the right as preceding quarter
    //in other voice, even the distance between the whole note and the last eighth would be big enough
    assertEquals(8, beatOffsets.length);
    assertEquals(new Fraction(0, 4), beatOffsets[0].getBeat());
    assertEquals(new Fraction(1, 8), beatOffsets[1].getBeat());
    assertEquals(new Fraction(1, 4), beatOffsets[2].getBeat());
    assertEquals(new Fraction(2, 4), beatOffsets[3].getBeat());
    assertEquals(new Fraction(3, 4), beatOffsets[4].getBeat());
    assertEquals(new Fraction(4, 4), beatOffsets[5].getBeat());
    assertEquals(new Fraction(9, 8), beatOffsets[6].getBeat());
    assertEquals(new Fraction(5, 4), beatOffsets[7].getBeat());
    for (int i = 0; i < beatOffsets.length - 1; i++)
    {
    	assertTrue("beat " + (i + 1) + " wrong",
    		beatOffsets[i].getOffsetMm() < beatOffsets[i + 1].getOffsetMm());
    }
    
    //distance between beat 1/4 and 2/4 must be width_1_4
    float is = score.getScoreFormat().getInterlineSpace();
    assertEquals(width_1_4 * is, beatOffsets[3].getOffsetMm() - beatOffsets[2].getOffsetMm(), Delta.DELTA_FLOAT_2);
  }
  
  
  /**
   * Test file "BeatOffsetsStrategyTest-4.xml".
   */
  @Test public void computeBeatOffsets_File4()
  {
    Score score = MxlScoreDocumentFileInputTest.loadXMLTestScore("BeatOffsetsStrategyTest-4.xml");
    
    LinkedList<VoiceSpacing> voiceSpacings = createVoiceSpacings(score);
    BeatOffset[] beatOffsets = strategy.computeBeatOffsetsFromVoiceSpacings(
    	voiceSpacings, new Fraction(3, 4)).toArray(new BeatOffset[0]); //TIDY
    
    //distance between beat 1/4 and 2/4 must be width_1_4
    float is = score.getScoreFormat().getInterlineSpace();
    assertEquals(width_1_4 * is, beatOffsets[3].getOffsetMm() - beatOffsets[2].getOffsetMm(), Delta.DELTA_FLOAT_2);
  }
  
  
  private Score createTestScore1Voice()
  {
    Score score = new Score();
    score.getScoreFormat().setInterlineSpace(1);
    score.addPart(0, 1);
    Staff staff = score.getStaff(0);
    score.addEmptyMeasures(1);
    Measure m = staff.getMeasures().get(0);
    m.addNoVoiceElement(new Clef(ClefType.G));
    m.addNoVoiceElement(new TraditionalKey(-3));
    m.addNoVoiceElement(new NormalTime(6, 4));
    //beats: 0, 2, 3, 4, 8.
    Voice voice = m.getVoices().get(0);
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 4));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 2));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 2));
    return score;
  }
  
  
  private SpacingElement[] createTestSpacingElements1Voice()
  {
    return new SpacingElement[] {
      new SpacingElement(null, fr(0), 1), //clef. width: 3
      new SpacingElement(null, fr(0), 4), //key. width: 2
      new SpacingElement(null, fr(0), 6), //time. width: 3
      new SpacingElement(null, fr(0), 9), //note. width: 2
      new SpacingElement(null, fr(2), 11), //note. width: 2
      new SpacingElement(null, fr(3), 13), //note. width: 2
      new SpacingElement(null, fr(4), 15), //note. width: 2
      new SpacingElement(null, fr(8), 17) //note. width: 2
    };
  }
  
  
  private Score createTestScore3Voices()
  {
    Score score = new Score();
    score.getScoreFormat().setInterlineSpace(10);
    score.addPart(0, 2);
    Staff staff = score.getStaff(0);
    score.addEmptyMeasures(1);
    Measure[] measures = new Measure[2];
    measures[0] = staff.getMeasures().get(0);
    measures[0].addNoVoiceElement(new NormalTime(4, 4));
    
    //2: half note, 4: quarter note, 8: eighth note, 3: quarter triplet
    //voice 1: | 4     4     4     4     |   (staff 1)
    //voice 2: | 8  8  8  8  2           |   (staff 1)
    //voice 3: | 3   3   3   8  8  4     |   (staff 2)
    
    //voice 1 (staff 1)
    Voice voice = measures[0].getVoices().get(0);
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 4));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 4));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 4));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 4));
    
    //voice 2 (staff 1)
    measures[0].addVoice();
    voice = measures[0].getVoices().get(1);
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 2));
    
    //voice 3 (staff 2)
    staff = score.getStaff(1);
    measures[1] = staff.getMeasures().get(0);
    voice = measures[1].getVoices().get(0);
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 6));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 6));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 6));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 4));
    
    return score;
  }
  
  
  private Score createTestScore3VoicesIncomplete()
  {
    Score score = new Score();
    score.getScoreFormat().setInterlineSpace(2);
    score.addPart(0, 2);
    Staff staff = score.getStaff(0);;
    score.addEmptyMeasures(1);
    Measure[] measures = new Measure[2];
    measures[0] = staff.getMeasures().get(0);
    measures[0].addNoVoiceElement(new NormalTime(4, 4));
    
    //2: half note, 4: quarter note, 8: eighth note, 3: quarter triplet, x: missing (empty)
    //voice 1: | 4     4     4     8  xxx|   (staff 1)
    //voice 2: | 8  8  8  8  4     xxxxxx|   (staff 1)
    //voice 3: | 3   3   3   8  xxxxxxxxx|   (staff 2)
    
    //voice 1 (staff 1)
    Voice voice = measures[0].getVoices().get(0);
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 4));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 4));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 4));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    
    //voice 2 (staff 1)
    measures[0].addVoice();
    voice = measures[0].getVoices().get(1);
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 4));
    
    //voice 3 (staff 2)
    staff = score.getStaff(1);
    measures[1] = staff.getMeasures().get(0);
    voice = measures[1].getVoices().get(0);
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 6));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 6));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 6));
    voice.addNote(new Pitch(0, 0, 0), new Fraction(1, 8));
    
    return score;
  }
  
  
  private static Fraction fr(int quarters)
  {
    return new Fraction(quarters, 4);
  }
  
  
  /**
   * Create {@link VoiceSpacing}s for the first measure column
   * of the given {@link Score}.
   */
  private LinkedList<VoiceSpacing> createVoiceSpacings(Score score)
  {
  	LinkedList<VoiceSpacing> ret = new LinkedList<VoiceSpacing>();
  	for (int iStaff : new Range(0, score.getStavesCount() - 1))
  	{
	  	Measure measure = score.getStaff(iStaff).getMeasures().get(0);
  		for (Voice voice : measure.getVoices())
  		{
  			Fraction beat = new Fraction(0);
  			ArrayList<SpacingElement> se = new ArrayList<SpacingElement>();
  			float offset = 0;
  			for (MusicElement e : voice.getElements())
  			{
  				//compute width
  				float width = 0;
  				if (e.getDuration() != null)
  				{
	  				if (e.getDuration().equals(dur_1_8))
	  					width = width_1_8;
	  				else if (e.getDuration().equals(dur_1_6))
	  					width = width_1_6;
	  				else if (e.getDuration().equals(dur_1_4))
	  					width = width_1_4;
	  				else if (e.getDuration().equals(dur_3_8))
	  					width = width_3_8;
	  				else if (e.getDuration().equals(dur_1_2))
	  					width = width_1_2;
	  				else if (e.getDuration().equals(dur_1_1))
	  					width = width_1_1;
  				}
  				//create spacing element with offset
  				se.add(new SpacingElement(e, beat, offset));
  				beat = beat.add(e.getDuration());
  				offset += width;
  			}
  			se.add(new SpacingElement(null, beat, offset));
  			ret.add(new VoiceSpacing(voice, ArrayTools.toSpacingElementArray(se)));
  		}
  	}
  	return ret;
  }

}
