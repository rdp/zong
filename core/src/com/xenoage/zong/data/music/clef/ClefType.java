package com.xenoage.zong.data.music.clef;

import com.xenoage.zong.data.music.Pitch;


/**
 * This is the type of a clef.
 * 
 * It contains the id, name (TODO) of the clef, the symbol,
 * the octave change, the pitch, the line position,
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
	
	G(0, new Pitch('G', 0, 4), 2, 3, 1),
	F(0, new Pitch('F', 0, 3), 6, 1, -1);
  
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


}
