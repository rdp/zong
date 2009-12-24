package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.util.MathTools;
import com.xenoage.util.math.Rectangle2f;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.data.music.format.SP;
import com.xenoage.zong.data.music.key.TraditionalKey;
import com.xenoage.zong.musiclayout.layouter.notation.NotationStrategy;
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
public class KeySignatureStamping
  extends Stamping
{
 
  private float positionX;
  private TraditionalKey traditionalKey;
  private int linePositionC4;
  private int linePositionMin;
  
  
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
    super(parentStaff, LEVEL_MUSIC, null);
    this.traditionalKey = traditionalKey;
    this.linePositionC4 = linePositionC4;
    this.linePositionMin = linePositionMin;
    this.positionX = positionX;
    updateBoundingShape();
  }
  
  
  private void updateBoundingShape()
  {
    int fifth = traditionalKey.getFifth();
    if (fifth == 0)
      return;
    boolean useSharps = (fifth > 0);
    float distance = (useSharps ? NotationStrategy.distanceSharps : NotationStrategy.distanceFlats);
    Symbol symbol = App.getInstance().getSymbolPool().getSymbol(
    	useSharps ? CommonSymbol.AccidentalSharp : CommonSymbol.AccidentalFlat);
    //create bounding shape
    Rectangle2f flagsBounds = null;
    fifth = Math.abs(fifth);
    float interlineSpace = parentStaff.getInterlineSpace();
    for (int i = 0; i < fifth; i++)
    {
      int linePosition = getLinePosition(i, useSharps);
      Rectangle2f bounds = symbol.getBoundingRect();
      bounds = bounds.scale(interlineSpace);
      bounds = bounds.move(positionX + i * distance * interlineSpace,
        parentStaff.computeYMm(linePosition));
      if (flagsBounds == null)
        flagsBounds = bounds;
      else
      	flagsBounds = flagsBounds.extend(bounds);
    }
    //add bounding shape
    clearBoundingShape();
    addBoundingShape(flagsBounds);
  }
  
  
  /**
   * Gets the line position for the flat
   * with the given 0-based index.
   */
  private int getFlatLinePosition(int index)
  {
    switch (index)
    {
      case 0: return 4; //Bb
      case 1: return 7; //Eb
      case 2: return 3; //Ab
      case 3: return 6; //Db
      case 4: return 2; //Gb
      case 5: return 5; //Cb
      default: return 1; //Fb
    }
  }
  
  
  /**
   * Gets the line position for the sharp
   * with the given 0-based index.
   */
  private int getSharpLinePosition(int index)
  {
    switch (index)
    {
      case 0: return 8; //F#
      case 1: return 5; //C#
      case 2: return 9; //G#
      case 3: return 6; //D#
      case 4: return 3; //A#
      case 5: return 7; //E#
      default: return 4; //H#
    }
  }
  
  
  /**
   * Gets the line position for the sharp or flat
   * with the given 0-based index.
   */
  private int getLinePosition(int index, boolean sharp)
  {
  	int ret = linePositionC4 + 2 +
        (sharp ? getSharpLinePosition(index) : getFlatLinePosition(index));
    ret = MathTools.modMin(ret, 7, linePositionMin);
    return ret;
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
    float distance = (useSharps ? NotationStrategy.distanceSharps : NotationStrategy.distanceFlats);
    Symbol symbol = App.getInstance().getSymbolPool().getSymbol(
    	useSharps ? CommonSymbol.AccidentalSharp : CommonSymbol.AccidentalFlat);
    //paint sharps/flats
    fifth = Math.abs(fifth);
    float interlineSpace = parentStaff.getInterlineSpace();
    for (int i = 0; i < fifth; i++)
    {
      int linePosition = getLinePosition(i, useSharps);
      StaffSymbolStampingRenderer.paint(symbol, null,
      	new SP(positionX + i * distance * interlineSpace, linePosition),
      	1, parentStaff, false, params);
    }
  }

}
