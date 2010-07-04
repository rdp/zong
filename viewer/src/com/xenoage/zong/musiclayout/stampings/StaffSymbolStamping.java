package com.xenoage.zong.musiclayout.stampings;

import java.awt.Color;

import com.xenoage.util.math.Rectangle2f;
import com.xenoage.util.math.Shape;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.format.SP;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.StaffSymbolStampingRenderer;


/**
 * Class for a symbol stamping that belongs
 * to a staff.
 * 
 * This stamping can be used for all musical symbols.
 * To attach individual context menus (e.g. noteheads need
 * have different methods than clefs), extend this class
 * and overwrite the createHandler() method.
 *
 * @author Andreas Wenger
 */
public abstract class StaffSymbolStamping
  extends Stamping
{

	private final Symbol symbol;
	private final Color color;
	
  private final SP position;
  private final float scaling;
  private final boolean mirrorV;
  
  
  /**
   * Creates a new symbol stamping belonging to no staff.
   * @param parentStaff     the staff stamping this element belongs to
   * @param musicElement    the musical element for which this layout element was created, or null
   * @param symbol          the musical symbol
   * @param color           the color of the stamping, or null for default color
   * @param position        the position of the symbol
   * @param scaling         the scaling. e.g. 1 means, that it fits perfect
   *                        to the staff size
   * @param mirrorV         vertically mirroring of the symbol
   */
  public StaffSymbolStamping(
    StaffStamping parentStaff, MusicElement musicElement, Symbol symbol,
    Color color, SP position, float scaling, boolean mirrorV)
  {
    super(parentStaff, Level.Music, musicElement,
    	createBoundingShape(symbol, scaling, parentStaff, position));
    this.symbol = symbol;
    this.color = color;
    this.position = position;
    this.scaling = scaling;
    this.mirrorV = mirrorV;
  }
  
  
  /**
   * Gets the symbol.
   */
  protected Symbol getSymbol()
  {
  	return symbol;
  }
  
  
  /**
   * Gets the position where this symbol is placed.
   */
  public SP getPosition()
  {
  	return position;
  }
  
  
  /**
   * Creates the bounding geometry.
   */
  private static Shape createBoundingShape(Symbol symbol, float scaling,
  	StaffStamping parentStaff, SP position)
  {
    Rectangle2f bounds = symbol.getBoundingRect();
    bounds = bounds.scale(scaling * parentStaff.getInterlineSpace());
    bounds = bounds.move(position.xMm, parentStaff.computeYMm(position.yLp));
    return bounds;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    StaffSymbolStampingRenderer.paint(getSymbol(), color,
    	position, scaling, parentStaff, mirrorV, params);
  }
  
  
}
