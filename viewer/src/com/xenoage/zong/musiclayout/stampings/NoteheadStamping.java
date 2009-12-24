package com.xenoage.zong.musiclayout.stampings;

import java.awt.Color;

import com.xenoage.util.math.Rectangle2f;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.format.SP;


/**
 * Class for an notehead stamping.
 *
 * @author Andreas Wenger
 */
public class NoteheadStamping
  extends StaffSymbolStamping
{
  
  
  public static final int NOTEHEAD_WHOLE = 0;
  public static final int NOTEHEAD_HALF = 1;
  public static final int NOTEHEAD_QUARTER = 2;
  
  public static final int SIDE_LEFT = 0;
  public static final int SIDE_CENTER = 1;
  public static final int SIDE_RIGHT = 2;
  
  private int notehead;
  
  
  /**
   * Creates a new notehead stamping.
   * @param chord           the chord this notehead belongs to
   * @param notehead        one of the NOTEHEAD_-constants
   * @param color           the color of the notehead, or null for default
   * @param parentStaff     the staff stamping this element belongs to
   * @param position        the position of the symbol
   * @param side            the horizontal side where the note sits.
   *                        use one of the SIDE_-constants.
   * @param scaling         the scaling. e.g. 1 means, that it fits perfect
   *                        to the staff size
   */
  public NoteheadStamping(Chord chord, int notehead, Color color,
    StaffStamping parentStaff, SP position , int side, float scaling)
  {
    super(parentStaff, chord, color,
      new SP(computePositionX(position.xMm, notehead, side, parentStaff), position.yLp),
      scaling, false);
    this.notehead = notehead;
    updateBoundingShape();
  }
  
  
  /**
   * Gets the symbol.
   */
  @Override protected Symbol getSymbol()
  {
  	return getSymbol(notehead);
  }
  
  
  private static Symbol getSymbol(int notehead)
  {
    SymbolPool pool = App.getInstance().getSymbolPool();
  	if (notehead == NOTEHEAD_WHOLE)
      return pool.getSymbol(CommonSymbol.NoteWhole);
    else if (notehead == NOTEHEAD_HALF)
      return pool.getSymbol(CommonSymbol.NoteHalf);
    else
      return pool.getSymbol(CommonSymbol.NoteQuarter);
  }
  
  
  private static float computePositionX(
    float positionX, int notehead, int side, StaffStamping staff)
  {
    float ret = positionX;
    Symbol symbol = getSymbol(notehead);
    Rectangle2f bounds;
    if (symbol != null)
      bounds = symbol.getBoundingRect();
    else
      bounds = new Rectangle2f(0, 0, 0, 0);
    float interlineSpace = staff.getInterlineSpace();
    float lineWidth = staff.getLineWidth();
    ret += (bounds.size.width / 2) * interlineSpace - lineWidth / 2;
    return ret;
  }
  

}
