package com.xenoage.zong.data.music;

import com.xenoage.util.math.Fraction;


/**
 * A duration consists of a base fraction
 * and optional dots.
 * 
 * For example a base fraction of 1/2 with
 * one dot represents a 1/2 + 1/4 = 3/8 duration.
 * 
 * This class can be used when the Fraction class
 * is not detailled enough to represent an unique
 * layout of a duration.
 *
 * @author Andreas Wenger
 */
public final class Duration
{
  
  private final Fraction baseFraction;
  private final int dots;
  
  
  /**
   * Creates a new Duration with the given base fraction
   * and number of dots.
   */
  public Duration(Fraction baseFraction, int dots)
  {
    this.baseFraction = baseFraction;
    this.dots = dots;
  }
  
  
  /**
   * Creates a copy of the given Duration.
   */
  public Duration(final Duration duration)
  {
    this.baseFraction = duration.baseFraction;
    this.dots = duration.dots;
  }
  
  
  /**
   * Creates a new Duration with the given fraction.
   * It is tried to convert the given fraction into a
   * 1/1, 1/2, 1/4, ... 1/256 note with up to two dots.
   * If this is not possible the given fraction is used
   * as the base fraction with no dots.
   */
  public Duration(Fraction duration)
  {
    int num = duration.getNumerator();
    int den = duration.getDenominator();
    //try to find dots:
    //find the base fraction
    int dotDenominator = 256; //start with 256th notes
    while (dotDenominator > 1 &&
      duration.compareTo(new Fraction(1, dotDenominator)) > 0)
    {
      dotDenominator /= 2;
    }
    //find the dots
    if (num == 3 && den == 2 * dotDenominator)
    {
      //1/dotDenominator with one dot
      this.baseFraction = new Fraction(1, dotDenominator);
      this.dots = 1;
    }
    else if (num == 7 && den == 4 * dotDenominator)
    {
      //1/dotDenominator with two dots
      this.baseFraction = new Fraction(1, dotDenominator);
      this.dots = 2;
    }
    else
    {
    	//use the given fraction with no dots
      this.baseFraction = duration;
      this.dots = 0;
    }
  }
  
  
  /**
   * Computes and returns this duration as a fraction.
   * The base fraction is expanded by the number of dots.
   */
  public Fraction getFraction()
  {
    Fraction ret = baseFraction;
    if (dots > 0)
    {
      //for each dot: add another half of the duration
      int dotsCounter = this.dots;
      Fraction halfDuration = baseFraction;
      halfDuration = new Fraction(halfDuration.getNumerator(), halfDuration.getDenominator() * 2);
      while (dotsCounter > 0)
      {
      	ret = ret.add(halfDuration);
      	halfDuration = new Fraction(halfDuration.getNumerator(), halfDuration.getDenominator() * 2);
        dotsCounter--;
      }
    }
    return ret;
  }


  /**
   * Gets the base fraction of this duration.
   */
  public Fraction getBaseFraction()
  {
    return baseFraction;
  }


  /**
   * Gets the number of dots used in this duration.
   */
  public int getDots()
  {
    return dots;
  }
  
  
  /**
   * Returns true, if the given object is a duration
   * that has the same fraction value as this one, otherwise
   * false.
   */
  @Override public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    else if (o instanceof Duration)
    {
      return (((Duration)o).getFraction().equals(this.getFraction()));
    }
    else
    {
      return false;
    }
  }
  

}
