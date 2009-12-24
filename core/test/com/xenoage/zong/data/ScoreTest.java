package com.xenoage.zong.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.xenoage.zong.util.demo.ScoreDemo;


/**
 * Test cases for a Score.
 *
 * @author Andreas Wenger
 */
public class ScoreTest
{

  @Test public void createScore()
  {
    //score format and info must be set
    Score score = new Score();
    assertNotNull(score.getScoreFormat());
    assertNotNull(score.getScoreInfo());
    //System.out.println(ScoreTest.createDemoScore32Measures().getDivisions());
    //score uses 256th notes and 1/6 notes = 192 divisions
    assertEquals(192, ScoreDemo.createDemoScore32Measures().computeDivisions());
  }
  
  
}
