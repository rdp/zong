package com.xenoage.zong.data.music;

import static org.junit.Assert.*;

import com.xenoage.util.math.Fraction;

import org.junit.Test;


/**
 * Test cases for a Duration.
 *
 * @author Andreas Wenger
 */
public class DurationTest
{
  
  
  /**
   * Test conversion from and to normal fractions.
   */
  @Test public void testFractionConversion()
  {
    Duration dur;
    Fraction frac, resFrac;
    //normal 1/1 note
    frac = new Fraction(1, 1);
    dur = new Duration(frac, 0);
    assertEquals(frac, dur.getFraction());
    assertEquals(0, dur.getDots());
    //1/1 note with one dot (=3/2)
    frac = new Fraction(1, 1);
    dur = new Duration(frac, 1);
    resFrac = dur.getFraction();
    assertEquals(3, resFrac.getNumerator());
    assertEquals(2, resFrac.getDenominator());
    //1/1 note with two dots (=7/4)
    frac = new Fraction(1, 1);
    dur = new Duration(frac, 2);
    resFrac = dur.getFraction();
    assertEquals(7, resFrac.getNumerator());
    assertEquals(4, resFrac.getDenominator());
    //normal 1/8 note
    frac = new Fraction(1, 8);
    dur = new Duration(frac, 0);
    assertEquals(frac, dur.getFraction());
    assertEquals(0, dur.getDots());
    //1/8 note with one dot (=3/16)
    frac = new Fraction(1, 8);
    dur = new Duration(frac, 1);
    resFrac = dur.getFraction();
    assertEquals(3, resFrac.getNumerator());
    assertEquals(16, resFrac.getDenominator());
    //1/8 note with two dots (=7/32)
    frac = new Fraction(1, 8);
    dur = new Duration(frac, 2);
    resFrac = dur.getFraction();
    assertEquals(7, resFrac.getNumerator());
    assertEquals(32, resFrac.getDenominator());
  }
  
  
  /**
   * Tests the equals-method.
   */
  @Test public void equals()
  {
    Duration dur1, dur2;
    dur1 = new Duration(new Fraction(1, 4), 2);
    dur2 = new Duration(new Fraction(1, 4), 2);
    assertEquals(dur1, dur2);
    dur1 = new Duration(new Fraction(1, 16), 0);
    dur2 = new Duration(new Fraction(1, 16), 1);
    assertFalse(dur1.equals(dur2));
  }
  

}
