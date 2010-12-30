package com.xenoage.zong.core.music.key;

import static com.xenoage.zong.core.music.Pitch.pi;

import com.xenoage.util.MathTools;
import com.xenoage.zong.core.music.Pitch;


/**
 * Traditional key signature in the circle of fifth and a mode (like major or
 * minor).
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public final class TraditionalKey
	implements Key
{
	
	public enum Mode
	{
		Major,
		Minor
	};

	private final int fifth;
	private final Mode mode;


	/**
	 * Creates a traditional key with the given number within the circle of
	 * fifth (mode: major).
	 */
	public TraditionalKey(int fifth)
	{
		this(fifth, Mode.Major);
	}


	/**
	 * Creates a traditional key with the given number within the circle of
	 * fifth and mode.
	 */
	public TraditionalKey(int fifth, Mode mode)
	{
		this.fifth = fifth;
		this.mode = mode;
	}


	/**
	 * Returns the alterations from the notes from C (0) to B (6).
	 */
	@Override public int[] getAlterations()
	{
		int[] ret;
		switch (fifth)
		{
			case +7:
				ret = new int[] { +1, +1, +1, +1, +1, +1, +1 };
				break;
			case +6:
				ret = new int[] { +1, +1, +1, +1, +1, +1, 0 };
				break;
			case +5:
				ret = new int[] { +1, +1, 0, +1, +1, +1, 0 };
				break;
			case +4:
				ret = new int[] { +1, +1, 0, +1, +1, 0, 0 };
				break;
			case +3:
				ret = new int[] { +1, 0, 0, +1, +1, 0, 0 };
				break;
			case +2:
				ret = new int[] { +1, 0, 0, +1, 0, 0, 0 };
				break;
			case +1:
				ret = new int[] { 0, 0, 0, +1, 0, 0, 0 };
				break;
			case 0:
				ret = new int[] { 0, 0, 0, 0, 0, 0, 0 };
				break;
			case -1:
				ret = new int[] { 0, 0, 0, 0, 0, 0, -1 };
				break;
			case -2:
				ret = new int[] { 0, 0, -1, 0, 0, 0, -1 };
				break;
			case -3:
				ret = new int[] { 0, 0, -1, 0, 0, -1, -1 };
				break;
			case -4:
				ret = new int[] { 0, -1, -1, 0, 0, -1, -1 };
				break;
			case -5:
				ret = new int[] { 0, -1, -1, 0, -1, -1, -1 };
				break;
			case -6:
				ret = new int[] { -1, -1, -1, 0, -1, -1, -1 };
				break;
			case -7:
				ret = new int[] { -1, -1, -1, -1, -1, -1, -1 };
				break;
			default:
				ret = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		}
		return ret;
	}
	

	public int getFifth()
	{
		return fifth;
	}


	public Mode getMode()
	{
		return mode;
	}


	/**
	 * Gets the step of the pitch of the flat/sharp with the given index. For
	 * example index = 0 at Eb flat will return Pitch.B.
	 */
	public int getStep(int index)
	{
		if (fifth < 0)
		{
			switch (index)
			{
				case 0:
					return Pitch.B;
				case 1:
					return Pitch.E;
				case 2:
					return Pitch.A;
				case 3:
					return Pitch.D;
				case 4:
					return Pitch.G;
				case 5:
					return Pitch.C;
				case 6:
					return Pitch.F;
			}
		}
		else
		{
			switch (index)
			{
				case 0:
					return Pitch.F;
				case 1:
					return Pitch.C;
				case 2:
					return Pitch.G;
				case 3:
					return Pitch.D;
				case 4:
					return Pitch.A;
				case 5:
					return Pitch.E;
				case 6:
					return Pitch.B;
			}
		}
		return 0;
	}


	/**
	 * Returns the nearest higher {@link Pitch} in the current key.
	 */
	@Override public Pitch getNearestHigherPitch(Pitch pitch)
	{
		int step = (pitch.getStep() + 1) % 7;
		int octave = pitch.getOctave() + (pitch.getStep() + 1) / 7;
		int alter = getAlterations()[step];
		return pi(step, alter, octave);
	}


	/**
	 * Returns the nearest lower {@link Pitch} in the current key.
	 */
	@Override public Pitch getNearestLowerPitch(Pitch pitch)
	{
		byte step;
		byte alter;
		byte octave;
		step = (byte) ((pitch.getStep() - 1 + 7) % 7);
		octave = (byte) (pitch.getOctave() + (pitch.getStep() - 1 + 7) / 7 - 1);
		alter = (byte) getAlterations()[step];
		return pi(step, alter, octave);
	}
	
	
	/**
   * Gets the line position for the sharp or flat
   * with the given 0-based index, when C4 is on the given line position.
   */
	public static int getLinePosition(int index, boolean sharp,
		int linePositionC4, int linePositionMin)
  {
  	int ret = linePositionC4 + 2 +
        (sharp ? getSharpLinePositionGKey(index) : getFlatLinePositionGKey(index));
    ret = MathTools.modMin(ret, 7, linePositionMin);
    return ret;
  }
	
	
	/**
   * Gets the line position for the flat
   * with the given 0-based index when there is a normal G key.
   */
  public static int getFlatLinePositionGKey(int index)
  {
    switch (index)
    {
      case 0: return 4; //Bb
      case 1: return 7; //Eb
      case 2: return 3; //Ab
      case 3: return 6; //Db
      case 4: return 2; //Gb
      case 5: return 5; //Cb
      case 6: return 1; //Fb
      default: throw new IllegalArgumentException("Invalid index: " + index);
    }
  }
  
  
  /**
   * Gets the line position for the sharp
   * with the given 0-based index when there is a normal G key.
   */
  public static int getSharpLinePositionGKey(int index)
  {
    switch (index)
    {
      case 0: return 8; //F#
      case 1: return 5; //C#
      case 2: return 9; //G#
      case 3: return 6; //D#
      case 4: return 3; //A#
      case 5: return 7; //E#
      case 6: return 4; //H#
      default: throw new IllegalArgumentException("Invalid index: " + index);
    }
  }

	
	@Override public String toString()
	{
		return "Fifths: " + fifth;
	}
	

}
