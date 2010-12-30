package com.xenoage.zong.core.music.clef;

import static com.xenoage.zong.core.music.Pitch.pi;

import com.xenoage.zong.core.music.Pitch;


/**
 * This is the type of a clef.
 * 
 * It contains the octave change, the pitch, the line position,
 * and the minimum line position allowed for flats and sharps
 * of a key signature.
 * 
 * It contains also convenience methods to create a
 * normal G- and F-clef.
 *
 * @author Andreas Wenger
 */
public enum ClefType
{
	
	G(0, pi('G', 0, 4), 2, 3, 1),
	F(0, pi('F', 0, 3), 6, 1, -1);
  
  private final int octaveChange;
  private final Pitch pitch;
  private final int line;
  
  private final int minSharpLine;
  private final int minFlatLine;
  
  
  
  /**
   * Creates a new type of clef with the given values.
   */
  private ClefType(int octaveChange, Pitch pitch, int line, int minSharpLine, int minFlatLine)
  {
    this.octaveChange = octaveChange;
    this.pitch = pitch;
    this.line = line;
    this.minSharpLine = minSharpLine;
    this.minFlatLine = minFlatLine;
  }


  /**
   * Gets the line position of this type of clef.
   */
  public int getLine()
  {
    return line;
  }
  
  
  /**
   * Gets the octave change in octaves of this type
   * of clef, e.g. -1 means one octave deeper.
   */
  public int getOctaveChange()
  {
    return octaveChange;
  }


  /**
   * Gets the pitch of the line position this type
   * of clef sits on.
   */
  public Pitch getPitch()
  {
    return pitch;
  }
  
  
  /**
   * Gets the minimum line position allowed for a sharp
   * symbol of a key signature.
   */
  public int getMinSharpLine()
  {
    return minSharpLine;
  }

  
  /**
   * Gets the minimum line position allowed for a flat
   * symbol of a key signature.
   */
  public int getMinFlatLine()
  {
    return minFlatLine;
  }
  
  
  /**
	 * Computes and returns the line position of the given pitch, that is the
	 * vertical offset of the note in half-spaces from the bottom line at a
	 * 5-lines-staff. Some examples:
	 * <ul>
	 * <li>0: note is on the bottom line</li>
	 * <li>-2: note is on the first lower leger line</li>
	 * <li>5: note is between the 3rd and 4th line from below</li>
	 * </ul>
	 */
	public int computeLinePosition(Pitch pitch)
	{
		Pitch clefPitch = this.pitch;
		int ret = pitch.getStep() + 7 * pitch.getOctave();
		ret -= clefPitch.getStep() + 7 * clefPitch.getOctave();
		ret += getLine();
		ret -= getOctaveChange() * 7;
		return ret;
	}
	
	
	/**
	 * Gets the lowest line that may be used for a key signature.
	 */
	public int getKeySignatureLowestLine(int fifth)
	{
		if (fifth < 0)
		{
			return getMinFlatLine();
		}
		else
		{
			return getMinSharpLine();
		}
	}


}
