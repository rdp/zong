package com.xenoage.zong.data.music;

import static org.junit.Assert.*;

import com.xenoage.util.math.Fraction;

import org.junit.Test;


/**
 * Test cases for a DurationInfo class.
 *
 * @author Andreas Wenger
 */
public class DurationInfoTest
{
  
  @Test public void getFlagsCount()
  {
    //simple notes
    assertEquals(0, DurationInfo.getFlagsCount(new Fraction(1, 4)));
    assertEquals(1, DurationInfo.getFlagsCount(new Fraction(1, 8)));
    assertEquals(2, DurationInfo.getFlagsCount(new Fraction(1, 16)));
    assertEquals(3, DurationInfo.getFlagsCount(new Fraction(1, 32)));
    assertEquals(4, DurationInfo.getFlagsCount(new Fraction(1, 64)));
    assertEquals(5, DurationInfo.getFlagsCount(new Fraction(1, 128)));
    assertEquals(3, DurationInfo.getFlagsCount(new Fraction(2, 64)));
    //single dotted quarter note
    assertEquals(0, DurationInfo.getFlagsCount(new Fraction(3, 8)));
    //single dotted eighth note
    assertEquals(1, DurationInfo.getFlagsCount(new Fraction(3, 16)));
    //double dotted quarter note
    assertEquals(0, DurationInfo.getFlagsCount(new Fraction(7, 16))); 
    //whole note
    assertEquals(0, DurationInfo.getFlagsCount(new Fraction(1, 1)));
  }

}
