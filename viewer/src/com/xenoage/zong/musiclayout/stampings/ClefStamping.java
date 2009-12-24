package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.format.SP;


/**
 * Class for a clef stamping.
 *
 * @author Andreas Wenger
 */
public class ClefStamping
  extends StaffSymbolStamping
{
  
  private static float clefWidth = 3f; //TODO
  
  private ClefType clefType;
  
  
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
    super(parentStaff, clef, null,
      new SP(xPosition + clefWidth / 2 *
        parentStaff.getInterlineSpace(),
        clef.getType().getLine()),
      scaling, false);
    this.clefType = clef.getType();
    updateBoundingShape();
  }
  
  
  /**
   * Gets the symbol.
   */
  @Override protected Symbol getSymbol()
  {
  	return App.getInstance().getSymbolPool().getSymbol(
  		CommonSymbol.getClef(clefType));
  }


}
