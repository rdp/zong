package com.xenoage.zong.core.music;


/**
 * Pitch is represented as a combination of the step of the
 * diatonic scale, the chromatic alteration, and the octave.
 * The step element uses the numbers 0 (C) to 6 (B). 
 * The alter element represents chromatic alteration in
 * number of semitones (e.g., -1 for flat, 1 for sharp).
 * The octave element is represented
 * by the numbers 0 to 9, where 4 indicates the octave
 * started by middle C.
 * (Parts copied from the MusicXML specification)
 *
 * @author Andreas Wenger
 */
public final class Pitch
  implements Comparable<Pitch>
{
  
  public static final int C = 0;
  public static final int D = 1;
  public static final int E = 2;
  public static final int F = 3;
  public static final int G = 4;
  public static final int A = 5;
  public static final int B = 6;
  
  
  private final int step;
  private final int alter;
  private final int octave;
  
  
  private Pitch(int step, int alter, int octave)
  {
  	if (step < 0 || step > 6)
  		throw new IllegalArgumentException("Invalid step: " + step);
    this.step = step;
    this.alter = alter;
    this.octave = octave;
  }
  
  
  private Pitch(char step, int alter, int octave)
  {
    switch (step)
    {
      case 'C': this.step = 0; break;
      case 'D': this.step = 1; break;
      case 'E': this.step = 2; break;
      case 'F': this.step = 3; break;
      case 'G': this.step = 4; break;
      case 'A': this.step = 5; break;
      case 'B': this.step = 6; break;
      default: throw new IllegalArgumentException("Unknown pitch step: " + step);
    }
    this.alter = alter;
    this.octave = octave;
  }
  
  
  public static Pitch pi(int step, int alter, int octave)
  {
  	return new Pitch(step, alter, octave);
  }
  
  
  public static Pitch pi(char step, int alter, int octave)
  {
  	return new Pitch(step, (byte) alter, (byte) octave);
  }
  
  
  public static Pitch pi(int step, int octave)
  {
  	return new Pitch((byte) step, (byte) 0, (byte) octave);
  }
  
  
  public static Pitch pi(char step, int octave)
  {
  	return new Pitch((byte) step, (byte) 0, (byte) octave);
  }
  
  
  /**
   * Gets a copy of this pitch without alter.
   */
  public Pitch withoutAlter()
  {
  	return pi(step, 0, octave);
  }

  
  public int getStep()
  {
    return step;
  }

  
  public int getAlter()
  {
    return alter;
  }

  
  public int getOctave()
  {
    return octave;
  }
  
  
  public char getStepAsChar()
  {
  	switch (step)
  	{
  		case 0: return 'C';
  		case 1: return 'D';
  		case 2: return 'E';
  		case 3: return 'F';
  		case 4: return 'G';
  		case 5: return 'A';
  		case 6: return 'B';
  	}
  	throw new IllegalStateException("Invalid step: " + step);
  }
  
  
  /**
   * Returns this Pitch as a String in the
   * format "(step,alter,octave)", e.g. "(1,0,4)".
   */
  @Override public String toString()
  {
    return "(" + step + "," + alter + "," + octave + ")";
  }
  
  
  /**
   * Compares this object to the specified object. The result is
   * <code>true</code> if and only if the argument is not
   * <code>null</code> and is an <code>Pitch</code> object that
   * contains the same values as this object.
   */
  @Override public boolean equals(Object obj)
  {
    if (obj instanceof Pitch)
    {
      Pitch pitch = (Pitch) obj;
      return (this.step == pitch.step &&
        this.alter == pitch.alter && this.octave == pitch.octave);
    }
    return false;
  }
  
  
  /**
   * Returns the hash code for this pitch.
   */
  @Override public int hashCode()
  {
  	return octave * 10000 + step * 100 + alter;
  }


  public int compareTo(Pitch pitch)
  {
    if (pitch == null)
      return 0;
    int thisval = this.octave * 12 +
      getStepSemitones(this.step) + this.alter;
    int pitchval = pitch.octave * 12 +
      getStepSemitones(pitch.step) + pitch.alter;
    if (thisval > pitchval)
      return 1;
    else if (thisval < pitchval)
      return -1;
    else
      return 0;
  }
  
  
  private int getStepSemitones(int step)
  {
    switch (step)
    {
      case 0: return 0;
      case 1: return 2;
      case 2: return 4;
      case 3: return 5;
      case 4: return 7;
      case 5: return 9;
      case 6: return 11;
      default: return 0;
    }
  }
  

}
