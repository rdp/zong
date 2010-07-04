package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.format.SP;


/**
 * Class for a prolongation dot stamping.
 * 
 * Prolongation dots belong to a staff. They have
 * a horizontal position and a line position
 * around which they are centered.
 * They are 0.3 spaces wide.
 *
 * @author Andreas Wenger
 */
public class ProlongationDotStamping
  extends StaffSymbolStamping
{
  
  
  /**
   * Creates a new prolongation dot stamping belonging to the given staff.
   * @param parentLayout    the layout this element belongs to
   * @param chord           the chord this dot belongs to
   * @param parentStaff     the staff stamping this element belongs to
   * @param position        the position of the symbol
   */
  public ProlongationDotStamping(StaffStamping parentStaff, Chord chord,
    SP position)
  {
    super(parentStaff, chord,
    	App.getInstance().getSymbolPool().getSymbol(CommonSymbol.NoteDot),
    	null, position, 1, false);
  }
  
}
