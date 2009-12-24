package com.xenoage.zong.data.music;

import static org.junit.Assert.*;

import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.key.TraditionalKey;

import org.junit.Test;


/**
 * Test cases for a MusicContext.
 *
 * @author Andreas Wenger
 */
public class MusicContextTest
{
  
  /**
   * Tests some accidentals in C major.
   */
  @Test public void testAccidentalsCMajor()
  {
    MusicContext context = new MusicContext();
    //a B5 needs no accidental
    assertNull(context.getAccidentalType(new Pitch(6, 0, 5)));
    //a Bb4 needs a flat
    assertEquals(Accidental.Type.Flat,
      context.getAccidentalType(new Pitch(6, -1, 4)));
    //a A##3 needs a double sharp
    assertEquals(Accidental.Type.DoubleSharp,
      context.getAccidentalType(new Pitch(5, 2, 3)));
  }
  
  
  /**
   * Tests some accidentals in Ab Major with
   * accidentals B(natural)4 and C#3.
   */
  @Test public void testAccidentalsAbMajor()
  {
    MusicContext context = new MusicContext(
      new Clef(ClefType.G), new TraditionalKey(-4),
      new Pitch[]{new Pitch(6, 0, 4), new Pitch(0, 1, 3)});
    //a B4 needs no accidental
    assertNull(context.getAccidentalType(new Pitch(6, 0, 4)));
    //a C#3 needs no accidental
    assertNull(context.getAccidentalType(new Pitch(0, 1, 3)));
    //a Eb6 needs no accidental
    assertNull(context.getAccidentalType(new Pitch(2, -1, 6)));
    //a C#4 needs a sharp
    assertEquals(Accidental.Type.Sharp,
      context.getAccidentalType(new Pitch(0, 1, 4)));
    //a Abb1 needs a double flat
    assertEquals(Accidental.Type.DoubleFlat,
      context.getAccidentalType(new Pitch(5, -2, 1)));
    //a A4 needs a natural
    assertEquals(Accidental.Type.Natural,
      context.getAccidentalType(new Pitch(5, 0, 4)));
  }

}
