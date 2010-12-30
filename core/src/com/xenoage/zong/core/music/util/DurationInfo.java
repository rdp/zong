package com.xenoage.zong.core.music.util;

import static com.xenoage.util.math.Fraction.fr;

import com.xenoage.util.math.Fraction;


/**
 * This class contains methods that
 * provide information about a duration,
 * e.g. how much flags or which symbol it needs.
 *
 * @author Andreas Wenger
 */
public final class DurationInfo
{
	
	public enum Type
	{
		Whole, Half, Quarter, Eighth, _16th, _32th, _64th, _128th, _256th;
	}
  
  
  /**
   * Gets the type of symbol that
   * must be used for the given duration for a notehead.
   */
  public static Type getNoteheadSymbolType(Fraction duration)
  {
    int length = duration.getNumerator() * 4 /
      duration.getDenominator(); //looseness does not matter
    if (length >= 4)
      return Type.Whole;
    else if (length >= 2)
      return Type.Half;
    else
      return Type.Quarter;
  }
  
  
  /**
   * Gets the type of rest that must be used for the given duration.
   * If the duration doesn't fit to a type exactly, the next bigger
   * type is returned (so that tuplets are handled correctly) - TODO: better strategy needed?
   */
  public static Type getRestType(Fraction duration)
  {
    float length = duration.getNumerator() * 4f /
      duration.getDenominator(); //looseness does not matter
    if (length <= 1/64f)
    	return Type._256th;
    else if (length <= 1/32f)
      return Type._128th;
    else if (length <= 1/16f)
      return Type._64th;
    else if (length <= 1/8f)
      return Type._32th;
    else if (length <= 1/4f)
      return Type._16th;
    else if (length <= 1/2f)
      return Type.Eighth;
    else if (length <= 1)
      return Type.Quarter;
    else if (length <= 2)
      return Type.Half;
    else
      return Type.Whole;
  }
  
  
  /**
   * Gets the number of flags of a unbeamed chord with the given
   * duration. If the duration is equal or greater than 1/4,
   * 0 is returned.
   * 
   * TODO: if performance is critical, create a Duration class for
   * chords and rests that consists of a Fraction and the number of flags.
   */
  public static int getFlagsCount(Fraction duration)
  {
  	int d = duration.getDenominator();
    int n = duration.getNumerator();
  	//quick test for common cases
    float f = duration.toFloat();
  	if (f >= 0.25)
  		return 0; //4th or bigger
  	else if (Math.abs(f - 0.125f) < 0.0001f)
  		return 1; //8th
  	else if (Math.abs(f - 0.0625f) < 0.0001f)
  		return 2; //16th
    //compute the value
    while (n > 2)
    {
      d /= 2;
      n = n - 1;
    }
    if (d >= 256)
      return 6;
    else if (d >= 128)
      return 5;
    else if (d >= 64)
      return 4;
    else if (d >= 32)
      return 3;
    else if (d >= 16)
      return 2;
    else if (d >= 8)
      return 1;
    else
      return 0;
  }
  
  
  /**
   * Gets the number of dots of the given fraction.
   * At maximum two dots are returned, otherwise 0 is returned.
   */
  public static int getDotsCount(Fraction duration)
  {
    int num = duration.getNumerator();
    int den = duration.getDenominator();
    //try to find dots:
    //find the base fraction
    int dotDenominator = 256; //start with 256th notes
    while (dotDenominator > 1 &&
      duration.compareTo(fr(1, dotDenominator)) > 0)
    {
      dotDenominator /= 2;
    }
    //find the dots
    if (num == 3 && den == 2 * dotDenominator)
    {
      //1/dotDenominator with one dot
      return 1;
    }
    else if (num == 7 && den == 4 * dotDenominator)
    {
      //1/dotDenominator with two dots
      return 2;
    }
    else
    {
    	//more than 2 dots? Not handled.
      return 0;
    }
  }

}
