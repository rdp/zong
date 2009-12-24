package com.xenoage.zong.data.text;


/**
 * Single-line part of a text. This can be a substring (using a font)
 * or a symbol (using a SVG or texture).
 *
 * @author Andreas Wenger
 */
public interface FormattedTextElement
{
	
	
	/**
   * Gets the text of this element, or "" if inapplicable.
   */
  public String getText();
  
  
  /**
   * Gets the style of this element.
   */
  public FormattedTextStyle getStyle();
  
  
  /**
   * Gets the ascent of this element in mm.
   */
  public float getAscent();
  
  
  /**
   * Gets the descent of this element in mm.
   */
  public float getDescent();
  
  
  /**
   * Gets the leading of this element in mm.
   */
  public float getLeading();
  
  
  /**
   * Gets the width of this element in mm.
   */
  public float getWidth();
  
  
}
