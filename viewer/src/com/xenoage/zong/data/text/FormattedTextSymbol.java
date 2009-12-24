package com.xenoage.zong.data.text;

import java.awt.Font;

import com.xenoage.zong.app.symbols.Symbol;


/**
 * Musical {@link Symbol} within a formatted text.
 *
 * @author Andreas Wenger
 */
public class FormattedTextSymbol
	implements FormattedTextElement
{

  private final Symbol symbol;
  private final FormattedTextStyle style;
  private final float scaling;
  private final float offsetX;
  
  private final float ascent, width;
  
 
  /**
   * Creates a new {@link FormattedTextSymbol}.
   * @param symbol   the symbol    
   * @param sizePt   the size of the symbol in pt (relative to the ascent
   *                 height defined in the symbol)
   */
  public FormattedTextSymbol(Symbol symbol, float sizePt)
  {
  	this.symbol = symbol;
  	this.style = new FormattedTextStyle(sizePt);
    //compute scaling
  	this.scaling = computeScaling(symbol, sizePt);
  	//compute ascent and width
    this.ascent = this.scaling; //TODO ??
    this.width = (symbol.getRightBorder() - symbol.getLeftBorder()) * this.scaling;
    //horizontal offset: align symbol to the left side
    this.offsetX = -symbol.getLeftBorder() * this.scaling;
  }
  
  
  public Symbol getSymbol()
  {
  	return symbol;
  }
  
  
  public float getScaling()
  {
  	return scaling;
  }

  
  public FormattedTextStyle getStyle()
  {
    return style;
  }

  
  public String getText()
  {
    return "";
  }
  
  
  /**
   * Gets the ascent of this symbol in mm.
   */
  public float getAscent()
  {
  	return ascent;
  }
  
  
  /**
   * Gets the descent of this symbol in mm. This is always 0.
   */
  public float getDescent()
  {
  	return 0;
  }
  
  
  /**
   * Gets the leading of this symbol in mm. This is always 0.
   */
  public float getLeading()
  {
  	return 0;
  }
  
  
  /**
   * Gets the width of this symbol in mm.
   */
  public float getWidth()
  {
  	return width;
  }
  
  
  /**
   * Gets the horizontal offset of the symbol (that must be added
   * so that the symbol is left-aligned).
   */
  public float getOffsetX()
  {
  	return offsetX;
  }
  
  
  public Font getFont()
  {
  	return style.getFont();
  }
  
  
  /**
   * Computes and returns the scaling factor that is needed to draw
   * the given symbol fitting to the given font size.
   */
  public static float computeScaling(Symbol symbol, float sizePt)
  {
  	//TODO: 0.65f is a constant that defines that the ascent has 65% of the complete hight
  	return sizePt * 0.3528f / symbol.getAscentHeight() * 0.65f;
  }
  
  
  @Override public String toString()
  {
  	return "[symbol]";
  }
  
}
