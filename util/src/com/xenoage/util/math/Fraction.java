package com.xenoage.util.math;

import com.xenoage.util.MathTools;


/**
 * This class represents a fraction of
 * two integer values.
 * 
 * It can for example be used to
 * represent durations.
 * 
 * When possible, the fraction is
 * cancelled automatically.
 *
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class Fraction
  implements Comparable<Fraction>
{
	
	public static final Fraction _0 = new Fraction(0);
  
  private final int numerator;
  private final int denominator;
  

  /**
   * Creates a new fraction with the given numerator.
   * The denominator is 1.
   */
  public Fraction(int number)
  {
    this.numerator = number;
    this.denominator = 1;
  }
  
  
  /**
   * Creates a new Fraction with the given numerator
   * and denominator.
   */
  public Fraction(int numerator, int denominator)
  {
  	if (denominator == 0)
      throw new IllegalArgumentException("Denominator may not be 0");
  	int gcd = MathTools.clampMin(MathTools.gcd(numerator, denominator), 1);
    this.numerator = numerator / gcd;
    this.denominator = denominator / gcd;
  }
  
  
  /**
   * Convenience method to create a {@link Fraction}
   * (much shorter than </code>new Fraction(...)</code>).
   */
  public static Fraction fr(int numerator, int denominator)
  {
  	return new Fraction(numerator, denominator);
  }

  
  /**
   * Gets the numerator.
   */
  public int getNumerator()
  {
    return numerator;
  }

  
  /**
   * Gets the denominator.
   */
  public int getDenominator()
  {
    return denominator;
  }
  
  
  /**
   * Adds the given Fraction to this one and returns the result.
   */
  public Fraction add(Fraction fraction)
  {
    if (fraction == null)
      return this;
    int numerator = 
      this.numerator * fraction.denominator +
      this.denominator * fraction.numerator;
    int denominator = this.denominator * fraction.denominator;
    return new Fraction(numerator, denominator);
  }
  
  
  /**
   * Subtracts the given fraction from this one and returns the result..
   */
  public Fraction sub(Fraction fraction)
  {
    if (fraction == null)
      return this;
    int numerator = 
      this.numerator * fraction.denominator -
      this.denominator * fraction.numerator;
    int denominator = this.denominator * fraction.denominator;
    return new Fraction(numerator, denominator);
  }
  
  
  /**
   * Inverts this fraction and returns the result.
   */
  public Fraction invert()
  {
    return new Fraction(-numerator, denominator);
  }
  
  
  /**
   * Creates a new fraction with the given number of
   * divisions and the given divisions per quarter note.
   */
  public static Fraction fromDivisions(int divisions, int divsPerQuarterNote)
  {
    return new Fraction(divisions, 4 * divsPerQuarterNote);
  }
  
  
  /**
   * Returns the number of divisions of this
   * fraction relative to the given divisions
   * per quarter note.
   */
  public int computeDivisions(int divsPerQuarterNote)
  {
    return divsPerQuarterNote * 4 * numerator / denominator;
  }


  /**
   * Compares this fraction with the given one.
   * @return  the value <code>0</code> if this fraction is
   *    equal to the given one; -1 if this fraction is numerically less
   *    than the given one; 1 if this fraction is numerically
   *    greater than the given one.
   * @since   1.2
   */
  public int compareTo(Fraction fraction)
  {
    Fraction compare = this.sub(fraction);
    if (compare.numerator < 0)
      return -1;
    else if (compare.numerator == 0)
      return 0;
    else
      return 1;
  }
  
  
  /**
   * Returns this fraction as a String in the
   * format "numerator/denominator", e.g. "3/4".
   */
  @Override public String toString()
  {
    return numerator + "/" + denominator;
  }
  
  
  /**
   * Returns true, if the given object is a fraction
   * that is numerically equal to this one, otherwise
   * false.
   */
  @Override public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    else if (o instanceof Fraction)
    {
      Fraction fraction = (Fraction) o;
      if (this.numerator == 0 && fraction.numerator == 0)
        return true;
      else
        return (this.numerator == fraction.numerator &&
          this.denominator == fraction.denominator);
    }
    else
    {
      return false;
    }
  }
  
  
  /**
   * Returns this fraction as a float value (which may
   * have rounding errors).
   */
  public float toFloat()
  {
    return 1.0f * numerator / denominator;
  }
  
  
  /**
   * Parses the given string in the format "x/y" or "x".
   */
  public static Fraction parse(String s)
  {
  	int slashPos = s.indexOf("/");
  	if (slashPos == -1)
  	{
  		return new Fraction(Integer.parseInt(s));
  	}
  	else
  	{
  		return new Fraction(Integer.parseInt(s.substring(0, slashPos)), Integer.parseInt(s.substring(slashPos + 1)));
  	}
  }
  
  public Fraction divideBy(Fraction fraction)
  {
	  return new Fraction(this.numerator * fraction.denominator, this.denominator * fraction.numerator);
  }

}
