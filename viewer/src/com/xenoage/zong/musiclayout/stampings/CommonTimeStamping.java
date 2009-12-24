package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.data.music.format.SP;
import com.xenoage.zong.data.music.time.CommonTime;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.StaffSymbolStampingRenderer;


/**
 * Class for a C time signature stamping.
 *
 * @author Andreas Wenger
 */
public class CommonTimeStamping
  extends Stamping
{
 
  private float positionX;
  private CommonTime commonTime;
  
  
  /**
   * Creates a new stamping for the given {@link CommonTime}.
   * @param commonTime         the common time signature
   * @param positionX          the horizontal position in mm
   * @param parentStaff        the staff stamping this element belongs to
   */
  public CommonTimeStamping(CommonTime commonTime,
    float positionX, StaffStamping parentStaff)
  {
    super(parentStaff, LEVEL_MUSIC, null);
    this.commonTime = commonTime;
    this.positionX = positionX;
    //TODO: bounding shape
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    SymbolPool symbolPool = App.getInstance().getSymbolPool();
    float linesCount = parentStaff.getLinesCount();
    
    //write C symbol
    Symbol symbol = symbolPool.getSymbol(CommonSymbol.TimeCommon);
    if (symbol != null)
    {
      StaffSymbolStampingRenderer.paint(symbol, null,
      	new SP(positionX, linesCount - 1), 1, parentStaff, false, params);
    }
  }

}
