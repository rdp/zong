package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.format.SP;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.*;


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
  extends Stamping
{

  private SP position;
  
  
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
    super(parentStaff, LEVEL_MUSIC, chord);
    this.position = position;
    
    //TODO: bounding shape. needed?
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    Symbol symbol = App.getInstance().getSymbolPool().getSymbol(CommonSymbol.NoteDot);
    StaffSymbolStampingRenderer.paint(symbol, null, position, 1, parentStaff, false, params);
  }
  
}
