package com.xenoage.zong.musiclayout.stampings;

import static com.xenoage.zong.core.music.format.SP.sp;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.clef.Clef;


/**
 * Class for a clef stamping.
 *
 * @author Andreas Wenger
 */
public final class ClefStamping
  extends StaffSymbolStamping
{
  
  private final static float clefWidth = 3f; //TODO
  
  
  /**
   * Creates a new clef stamping.
   * @param clef            the clef
   * @param parentStaff     the staff stamping this element belongs to
   * @param xPosition       the horizontal position in mm
   * @param scaling         the scaling. e.g. 1 means, that it fits perfect
   *                        to the staff size. (TODO: does this also work for
   *                        break-half and break-whole? perhaps edit symbols)
   */
  public ClefStamping(Clef clef,
    StaffStamping parentStaff, float xPosition, float scaling)
  {
    super(parentStaff, clef,
    	App.getInstance().getSymbolPool().getSymbol(
    		CommonSymbol.getClef(clef.getType())), null, 
      sp(xPosition + clefWidth / 2 *
        parentStaff.getInterlineSpace(),
        clef.getType().getLine()),
      scaling, false);
  }


}
