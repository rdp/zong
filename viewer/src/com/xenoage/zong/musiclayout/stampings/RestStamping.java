package com.xenoage.zong.musiclayout.stampings;

import static com.xenoage.zong.core.music.format.SP.sp;

import java.util.HashMap;

import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.app.symbols.WarningSymbol;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.core.music.util.DurationInfo;


/**
 * Class for a rest stamping.
 * 
 * The quarter rest is centered around the middle
 * line of the staff, the half rest sits on the
 * middle line and the whole rest hangs on the
 * line over the middle staff.
 *
 * @author Andreas Wenger
 */
public final class RestStamping
  extends StaffSymbolStamping
{
  
  private static HashMap<DurationInfo.Type, CommonSymbol> durationSymbolMapping =
  	new HashMap<DurationInfo.Type, CommonSymbol>();
  
  static
  {
  	durationSymbolMapping.put(DurationInfo.Type.Whole, CommonSymbol.RestWhole);
  	durationSymbolMapping.put(DurationInfo.Type.Half, CommonSymbol.RestHalf);
  	durationSymbolMapping.put(DurationInfo.Type.Quarter, CommonSymbol.RestQuarter);
  	durationSymbolMapping.put(DurationInfo.Type.Eighth, CommonSymbol.RestEighth);
  	durationSymbolMapping.put(DurationInfo.Type._16th, CommonSymbol.Rest16th);
  	durationSymbolMapping.put(DurationInfo.Type._32th, CommonSymbol.Rest32th);
  	durationSymbolMapping.put(DurationInfo.Type._64th, CommonSymbol.Rest64th);
  	durationSymbolMapping.put(DurationInfo.Type._128th, CommonSymbol.Rest128th);
  	durationSymbolMapping.put(DurationInfo.Type._256th, CommonSymbol.Rest256th);
  }
  
  
  /**
   * Creates a new rest stamping.
   * @param restElement     the music element
   * @param duration        whole, half, quarter, ...
   * @param parentStaff     the staff stamping this element belongs to
   * @param positionX       the horizontal position in mm
   * @param scaling         the scaling. e.g. 1 means, that it fits perfect
   *                        to the staff size. (TODO: does this also work for
   *                        break-half and break-whole? perhaps edit symbols)
   */
  public RestStamping(Rest restElement, DurationInfo.Type duration,
    StaffStamping parentStaff, float positionX, float scaling)
  {
    super(parentStaff, restElement, getSymbol(duration), null,
      sp(positionX, getLinePosition(parentStaff, duration)),
      scaling, false);
  }
  
  
  private static Symbol getSymbol(DurationInfo.Type duration)
  {
  	SymbolPool pool = App.getInstance().getSymbolPool();
    CommonSymbol cs = durationSymbolMapping.get(duration);
    if (cs != null)
    	return pool.getSymbol(cs);
    else
    	return WarningSymbol.instance;
  }
  
  
  private static int getLinePosition(StaffStamping staff, DurationInfo.Type duration)
  {
    if (duration == DurationInfo.Type.Whole)
    {
      //whole rest hangs on the line above the middle
      return staff.getLinesCount() + 1;
    }
    else if (duration == DurationInfo.Type.Half)
    {
      //half rest sits on the line under the middle
      return staff.getLinesCount() - 1;
    }
    else
    {
      //all other rests are centered on the middle line
      return staff.getLinesCount() - 1;
    }
  }

}
