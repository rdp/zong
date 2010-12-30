package com.xenoage.zong.musiclayout.stampings;

import static com.xenoage.zong.core.music.format.SP.sp;
import static com.xenoage.zong.core.music.key.TraditionalKey.getLinePosition;
import static com.xenoage.zong.musiclayout.LayoutSettings.layoutSettings;

import com.xenoage.util.math.Rectangle2f;
import com.xenoage.util.math.Shape;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.StaffSymbolStampingRenderer;


/**
 * Class for a key signature stamping.
 * It consists of a number of flats and/or sharps.
 * 
 * At the moment this stamping can only
 * be created for a TraditionalKey.
 *
 * @author Andreas Wenger
 */
public final class KeySignatureStamping
  extends Stamping
{
 
  private final float positionX;
  private final TraditionalKey traditionalKey;
  private final int linePositionC4;
  private final int linePositionMin;
  
  
  /**
   * Creates a new key signature stamping for
   * a traditional key.
   * @param traditionalKey    the key signature
   * @param linePositionC4    the line position of a C4.
   * @param linePositionMin   the minimal line position for a sharp/flat
   * @param positionX         the horizontal position in mm
   * @param parentStaff       the staff stamping this element belongs to
   */
  public KeySignatureStamping(TraditionalKey traditionalKey,
    int linePositionC4, int linePositionMin, float positionX,
    StaffStamping parentStaff)
  {
    super(parentStaff, Level.Music, null,
    	createBoundingShape(traditionalKey, parentStaff, linePositionC4, linePositionMin, positionX));
    this.traditionalKey = traditionalKey;
    this.linePositionC4 = linePositionC4;
    this.linePositionMin = linePositionMin;
    this.positionX = positionX;
  }
  
  
  private static Shape createBoundingShape(TraditionalKey traditionalKey,
  	StaffStamping parentStaff, int linePositionC4, int linePositionMin, float positionX)
  {
    int fifth = traditionalKey.getFifth();
    if (fifth == 0)
      return null;
    boolean useSharps = (fifth > 0);
    float distance = (useSharps ?
    	layoutSettings().widthSharp : layoutSettings().widthFlat);
    Symbol symbol = App.getInstance().getSymbolPool().getSymbol(
    	useSharps ? CommonSymbol.AccidentalSharp : CommonSymbol.AccidentalFlat);
    //create bounding shape
    Rectangle2f shape = null;
    fifth = Math.abs(fifth);
    float interlineSpace = parentStaff.getInterlineSpace();
    for (int i = 0; i < fifth; i++)
    {
      int linePosition = getLinePosition(i, useSharps, linePositionC4, linePositionMin);
      Rectangle2f bounds = symbol.getBoundingRect();
      bounds = bounds.scale(interlineSpace);
      bounds = bounds.move(positionX + i * distance * interlineSpace,
        parentStaff.computeYMm(linePosition));
      if (shape == null)
      	shape = bounds;
      else
      	shape = shape.extend(bounds);
    }
    return shape;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    int fifth = traditionalKey.getFifth();
    if (fifth == 0)
      return;
    boolean useSharps = (fifth > 0);
    float distance = (useSharps ?
    	layoutSettings().widthSharp : layoutSettings().widthFlat);
    Symbol symbol = App.getInstance().getSymbolPool().getSymbol(
    	useSharps ? CommonSymbol.AccidentalSharp : CommonSymbol.AccidentalFlat);
    //paint sharps/flats
    fifth = Math.abs(fifth);
    float interlineSpace = parentStaff.getInterlineSpace();
    for (int i = 0; i < fifth; i++)
    {
      int linePosition = getLinePosition(i, useSharps, linePositionC4, linePositionMin);
      StaffSymbolStampingRenderer.paint(symbol, null,
      	sp(positionX + i * distance * interlineSpace, linePosition),
      	1, parentStaff, false, params);
    }
  }

}
