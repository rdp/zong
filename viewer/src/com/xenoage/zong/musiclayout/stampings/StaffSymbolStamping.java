package com.xenoage.zong.musiclayout.stampings;

import java.awt.Color;

import com.xenoage.util.math.Rectangle2f;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.stampings.StaffSymbolStampingRenderer;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.data.music.MusicElement;
import com.xenoage.zong.data.music.format.SP;


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

	private Color color;
	
  private SP position;
  private float scaling;
  private boolean mirrorV;
  
  
  /**
   * Creates a new symbol stamping belonging to no staff.
   * @param parentStaff     the staff stamping this element belongs to
   * @param musicElement    the musical element for which this layout element was created, or null
   * @param color           the color of the stamping, or null for default color
   * @param position        the position of the symbol
   * @param scaling         the scaling. e.g. 1 means, that it fits perfect
   *                        to the staff size
   * @param mirrorV         vertically mirroring of the symbol
   */
  public StaffSymbolStamping(
    StaffStamping parentStaff, MusicElement musicElement, Color color, SP position,
    float scaling, boolean mirrorV)
  {
    super(parentStaff, Stamping.LEVEL_MUSIC, musicElement);
    this.color = color;
    this.position = position;
    this.scaling = scaling;
    this.mirrorV = mirrorV;
  }
  
  
  /**
   * Gets the position where this symbol is placed.
   */
  public SP getPosition()
  {
  	return position;
  }
  
  
  /**
   * Gets the symbol.
   */
  protected abstract Symbol getSymbol();
  
  
  /**
   * Updates the bounding rectangle.
   * This method must be called after creating an instance
   * of this class.
   */
  protected void updateBoundingShape()
  {
    Symbol symbol = getSymbol();
    Rectangle2f bounds = symbol.getBoundingRect();
    bounds = bounds.scale(scaling * parentStaff.getInterlineSpace());
    bounds = bounds.move(position.xMm, parentStaff.computeYMm(position.yLp));
    clearBoundingShape();
    addBoundingShape(bounds);
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
