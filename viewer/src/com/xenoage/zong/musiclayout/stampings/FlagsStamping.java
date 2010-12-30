package com.xenoage.zong.musiclayout.stampings;

import static com.xenoage.zong.core.music.format.SP.sp;

import com.xenoage.util.math.Rectangle2f;
import com.xenoage.util.math.Shape;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.format.SP;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.StaffSymbolStampingRenderer;


/**
 * Class for a flags stamping. It consists
 * of one ore more flags needed for unbeamed
 * eighth, 16th, 32th notes and so on.
 * 
 * The quarter rest is centered around the middle
 * line of the staff, the half rest sits on the
 * middle line and the whole rest hangs on the
 * line over the middle staff.
 *
 * @author Andreas Wenger
 */
public class FlagsStamping
  extends Stamping
{
  
  public static final int FLAG_DOWN = 0;
  public static final int FLAG_UP = 1;
  
  private final int flag;
  private final int flagsCount;
  private final SP position;
  
  
  /**
   * Creates a new flags stamping.
   * @param parentLayout    the layout this element belongs to
   * @param chord           the chord this flag belongs to
   * @param flag            one of the FLAG_-constants
   * @param flagsCount      the number of flags
   * @param parentStaff     the staff stamping this element belongs to
   * @param position        the position of the flag. the vertical coordinate is the
   *                        line position where the flag starts. This should
   *                        always be the end position of the stem.
   */
  public FlagsStamping(int flag, int flagsCount,
    StaffStamping parentStaff, Chord chord, SP position)
  {
    super(parentStaff, Level.Music, chord,
    	createBoundingShape(flag, flagsCount, parentStaff, position));
    this.flag = flag;
    this.flagsCount = flagsCount;
    this.position = position;
  }
  
  
  private static Shape createBoundingShape(int flag, int flagsCount,
  	StaffStamping parentStaff, SP position)
  {
  	Symbol symbol = App.getInstance().getSymbolPool().getSymbol(CommonSymbol.NoteFlag);
    float flagsDistance = getFlagsDistance(flag);
    float interlineSpace = parentStaff.getInterlineSpace();
    Rectangle2f flagsBounds = null;
    for (int i = 0; i < flagsCount; i++)
    {
      Rectangle2f bounds = symbol.getBoundingRect();
      if (flag == FLAG_UP)
      {
      	bounds = bounds.move(0, -bounds.size.height);
      }
      bounds = bounds.scale(interlineSpace);
      bounds = bounds.move(0, -i * flagsDistance * interlineSpace);
      if (flagsBounds == null)
        flagsBounds = bounds;
      else
      	flagsBounds = flagsBounds.extend(bounds);
    }
    flagsBounds.move(position.xMm, parentStaff.computeYMm(position.yLp));
    return flagsBounds;
  }
  
  
  /**
   * Gets the distance between the flags in interline spaces.
   */
  private static float getFlagsDistance(int flag)
  {
  	return (flag == FLAG_DOWN ? -1 : 1);
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
  	Symbol symbol = App.getInstance().getSymbolPool().getSymbol(CommonSymbol.NoteFlag);
  	boolean flagsMirrored = (flag == FLAG_UP);
  	float flagsDistance = getFlagsDistance(flag);
  	//draw all flags
  	for (int i = 0; i < flagsCount; i++)
    {
  		StaffSymbolStampingRenderer.paint(symbol, null,
  			sp(position.xMm, position.yLp + //TODO: flag position is not correct yet
          flagsDistance * 0.2f /* move a little bit into the stem */ +
            i * 2 * flagsDistance), 1, parentStaff, flagsMirrored, params);
    }
  }

}
