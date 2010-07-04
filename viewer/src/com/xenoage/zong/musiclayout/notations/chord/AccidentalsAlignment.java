package com.xenoage.zong.musiclayout.notations.chord;

import com.xenoage.pdlib.PVector;
import com.xenoage.zong.core.music.chord.Accidental;


/**
 * This class stores
 * the alignment of the accidentals of a chord.
 * 
 * Some rules are adepted from
 * "Ross: The Art of Music Engraving", page 130 ff, and
 * "Chlapik: Die Praxis des Notengraphikers", page 48 ff.
 *
 * @author Andreas Wenger
 */
public final class AccidentalsAlignment
{
  
  public static final float WIDTH_GAP_ACCTONOTE = 0.5f;
  public static final float WIDTH_GAP_ACCTOACC = 0.25f;
  public static final float WIDTH_DOUBLESHARP = 0.95f;
  public static final float WIDTH_SHARP = 1f;
  public static final float WIDTH_NATURAL = 0.96f;
  public static final float WIDTH_FLAT = 0.97f;
  public static final float WIDTH_DOUBLEFLAT = 1.8f;
  
  
  private final PVector<AccidentalAlignment> accidentals;
  private final float width;
  
  
  /**
   * Creates a new {@link AccidentalsAlignment}.
   * @param accidentals  the positions of the accidentals
   * @param width        the needed width for all accidentals
   */
  public AccidentalsAlignment(PVector<AccidentalAlignment> accidentals, float width)
  {
  	//must be sorted upwards
  	for (int i = 0; i < accidentals.size() - 1; i++)
  	{
  		if (accidentals.get(i).getLinePosition() > accidentals.get(i + 1).getLinePosition())
  			throw new IllegalArgumentException("Accidentals must be sorted upwards");
  	}
  	this.accidentals = accidentals;
  	this.width = width;
  }
  
  
  /**
   * Gets the accidentals of this chord.
   */
  public PVector<AccidentalAlignment> getAccidentals()
  {
    return accidentals;
  }
  
  
  /**
   * Gets the width of the accidentals of this chord.
   * This is the distance between the left side of
   * the leftmost accidental and the beginning of
   * the notes.
   */
  public float getWidth()
  {
    return width;
  }
  
  
  /**
   * Gets the width of the accidental of the given type.
   */
  public static float getAccidentalWidth(Accidental.Type type)
  {
    if (type.equals(Accidental.Type.DoubleSharp))
      return WIDTH_DOUBLESHARP;
    else if (type.equals(Accidental.Type.Sharp))
      return WIDTH_SHARP;
    else if (type.equals(Accidental.Type.Natural))
      return WIDTH_NATURAL;
    else if (type.equals(Accidental.Type.Flat))
      return WIDTH_FLAT;
    else if (type.equals(Accidental.Type.DoubleFlat))
      return WIDTH_DOUBLEFLAT;
    return 0;
  }

  
  /**
   * Computes the width of the given accidental having the greatest width.
   */
  public static float computeAccidentalsMaxWidth(Accidental.Type[] types)
  {
    float maxWidth = 0;
    for (Accidental.Type type : types)
    {
      float width = getAccidentalWidth(type);
      if (width > maxWidth)
        maxWidth = width;
    }
    return maxWidth;
  }

}
