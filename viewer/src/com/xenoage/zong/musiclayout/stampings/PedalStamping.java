package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.direction.Pedal;
import com.xenoage.zong.core.music.format.SP;


/**
 * Stamping of a pedal symbol.
 *
 * @author Andreas Wenger
 */
public final class PedalStamping
  extends StaffSymbolStamping
{
	
  
  /**
   * Creates a new {@link PedalStamping}.
   * @param pedal           the pedal symbol
   * @param parentStaff     the staff stamping this element belongs to
   * @param position        the position of the symbol
   * @param scaling         the scaling. e.g. 1 means, that it fits perfect
   *                        to the staff size
   */
  public PedalStamping(Pedal pedal,
    StaffStamping parentStaff, SP position, float scaling)
  {
    super(parentStaff, pedal,
    	App.getInstance().getSymbolPool().getSymbol(CommonSymbol.getPedal(pedal.getType())),
  		null, position, scaling, false);
  }

}
