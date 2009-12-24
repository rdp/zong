package com.xenoage.zong.musiclayout.layouter.notation;

import static com.xenoage.util.math.Fraction.fr;
import static org.junit.Assert.*;

import com.xenoage.util.Delta;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterTest;

import org.junit.Before;
import org.junit.Test;


/**
 * Test cases for the {@link NotationStrategy}.
 *
 * @author Andreas Wenger
 */
public class NotationStrategyTest
{
  
  private NotationStrategy strategy;
  
  
  @Before public void setUp()
  {
  	strategy = ScoreLayouterTest.getNotationStrategy();
    //use hardcoded values for testing
  	NotationStrategy.widths = new NotationStrategy.DurationWidth[] {
  		strategy.new DurationWidth(1, 8, 2),
  		strategy.new DurationWidth(1, 4, 3),
  		strategy.new DurationWidth(1, 2, 4),
  		strategy.new DurationWidth(1, 1, 6)
    };
  }

  
  @Test public void testComputeWidth()
  {
    //duration 1/4 = width 3
    assertEquals(3, strategy.computeWidth(fr(1, 4)), Delta.DELTA_FLOAT);
    //duration 2/4 (=1/2) = width 4
    assertEquals(4, strategy.computeWidth(fr(2, 4)), Delta.DELTA_FLOAT);
    //duration 1/1 = width 6
    assertEquals(6, strategy.computeWidth(fr(1, 1)), Delta.DELTA_FLOAT);
    //duration 1/3 = width 3.1111...
    assertEquals((3+4)*((1f/3)/(3f/4)),
    	strategy.computeWidth(fr(1, 3)), Delta.DELTA_FLOAT_2);
    //duration 1/16 = width 2 (like duration 1/8)
    assertEquals(2,
    	strategy.computeWidth(fr(1, 16)), Delta.DELTA_FLOAT_2);
    //duration 2/1 = width 6 (like duration 1/1)
    assertEquals(6,
    	strategy.computeWidth(fr(1, 1)), Delta.DELTA_FLOAT_2);
  }
  
  
}
