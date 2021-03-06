package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.chord.Articulation;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.format.SP;


/**
 * Stamping of an articulation.
 *
 * @author Andreas Wenger
 */
public final class ArticulationStamping
  extends StaffSymbolStamping
{
	
  
  /**
   * Creates a new {@link ArticulationStamping}.
   * @param chord           the chord this articulation belongs to
   * @param articulation    the type of the articulation
   * @param parentStaff     the staff stamping this element belongs to
   * @param position        the position of the symbol
   * @param scaling         the scaling. e.g. 1 means, that it fits perfect
   *                        to the staff size
   */
  public ArticulationStamping(Chord chord, Articulation.Type articulation,
    StaffStamping parentStaff, SP position, float scaling)
  {
    super(parentStaff, chord,
    	App.getInstance().getSymbolPool().getSymbol(
    		CommonSymbol.getArticulation(articulation)),
    		null, position, scaling, false);
  }

}
