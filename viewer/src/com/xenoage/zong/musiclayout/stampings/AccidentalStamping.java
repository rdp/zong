package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.chord.Accidental;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.format.SP;


/**
 * Stamping of an accidental.
 * 
 * TIDY: needed? use a general StaffSymbolStamping class
 * using the symbol directory.
 *
 * @author Andreas Wenger
 */
public class AccidentalStamping
  extends StaffSymbolStamping
{
  
	private Accidental.Type accidental;
	
  
  /**
   * Creates a new {@link AccidentalStamping}.
   * @param chord           the chord this accidental belongs to
   * @param accidental      the type of the accidental
   * @param parentStaff     the staff stamping this element belongs to
   * @param position        the position of the symbol
   * @param scaling         the scaling. e.g. 1 means, that it fits perfect
   *                        to the staff size
   */
  public AccidentalStamping(Chord chord, Accidental.Type accidental,
    StaffStamping parentStaff, SP position, float scaling)
  {
    super(parentStaff, chord, null, position, scaling, false);
    this.accidental = accidental;
    updateBoundingShape();
  }
  
  
  /**
   * Gets the symbol.
   */
  @Override protected Symbol getSymbol()
  {
  	return App.getInstance().getSymbolPool().getSymbol(
  		CommonSymbol.getAccidental(accidental));
  }

}
