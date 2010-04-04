package com.xenoage.zong.musiclayout.stampings;

import static com.xenoage.zong.core.music.format.SP.sp;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.time.NormalTime;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.StaffSymbolStampingRenderer;


/**
 * Class for a normal time signature stamping.
 * It consists of a fraction, like "4/4" or "7/16".
 *
 * @author Andreas Wenger
 */
public class NormalTimeStamping
  extends Stamping
{
 
  private float positionX;
  private NormalTime normalTime;
  private float numeratorOffset;
  private float denominatorOffset;
  private float digitGap;
  
  
  /**
   * Creates a new stamping for the given {@link NormalTime}.
   * @param normalTime         the normal time signature
   * @param positionX          the horizontal position in mm
   * @param parentStaff        the staff stamping this element belongs to
   * @param numeratorOffset    the horizontal offset of the numerator in interline spaces
   * @param denominatorOffset  the horizontal offset of the denominator in interline spaces
   * @param digitGap           the gap between the digits in interline spaces
   */
  public NormalTimeStamping(NormalTime normalTime,
    float positionX, StaffStamping parentStaff,
    float numeratorOffset, float denominatorOffset, float digitGap)
  {
    super(parentStaff, LEVEL_MUSIC, null);
    this.normalTime = normalTime;
    this.positionX = positionX;
    this.numeratorOffset = numeratorOffset;
    this.denominatorOffset = denominatorOffset;
    this.digitGap = digitGap;
    //TODO: bounding shape
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    SymbolPool symbolPool = App.getInstance().getSymbolPool();
    float interlineSpace = parentStaff.getInterlineSpace();
    float linesCount = parentStaff.getLinesCount();
    
    //write numerator digits
    float offsetX = numeratorOffset * interlineSpace;
    String s = Integer.toString(normalTime.getNumerator());
    for (int i = 0; i < s.length(); i++)
    {
      int d = s.charAt(i) - '0'; //TIDY
      Symbol symbol = symbolPool.getSymbol(CommonSymbol.getDigit(d));
      if (symbol != null)
      {
        float symbolWidth = symbol.getBoundingRect().size.width;
        StaffSymbolStampingRenderer.paint(symbol, null,
        	sp(positionX + offsetX, linesCount + 1), 1, parentStaff, false, params);
        offsetX += (symbolWidth + digitGap) * interlineSpace;
      }
    }
    
    //write denominator digits
    offsetX = denominatorOffset * interlineSpace;
    s = Integer.toString(normalTime.getDenominator());
    for (int i = 0; i < s.length(); i++)
    {
      int d = s.charAt(i) - '0'; //TIDY
      Symbol symbol = symbolPool.getSymbol(CommonSymbol.getDigit(d));
      if (symbol != null)
      {
        float symbolWidth = symbol.getBoundingRect().size.width;
        StaffSymbolStampingRenderer.paint(symbol, null,
        	sp(positionX + offsetX, linesCount - 3),
        	1, parentStaff, false, params);
        offsetX += (symbolWidth + digitGap) * interlineSpace;
      }
    }
  }

}
