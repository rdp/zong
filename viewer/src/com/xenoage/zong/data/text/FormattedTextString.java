package com.xenoage.zong.data.text;

import java.awt.Font;

import com.xenoage.util.text.TextMeasurer;


/**
 * Formatted substring of a text.
 *
 * @author Andreas Wenger
 */
public class FormattedTextString
	implements FormattedTextElement
{

  private final String text;
  private final FormattedTextStyle style;
  
  private final float ascent, descent, leading, width;
  
  
  public FormattedTextString(String text)
  {
    this(text, null);
  }
  
  
  public FormattedTextString(String text, FormattedTextStyle style)
  {
  	//text may not be empty
  	if (text.length() == 0)
  		throw new IllegalArgumentException("FormattedTextString must contain at least one character.");
  	//text may not contain a line break. this must be represented
		//as several FormattedTextParagraphs.
		if (text.indexOf("\n") > -1)
		{
			//throw new IllegalArgumentException("FormattedTextString may not contain line breaks.");
			//better, ignore it
			text = text.replaceAll("\n", "");
		}
		//text may not contain tabs. tabs are replaced by spaces.
		text = text.replaceAll("\t", " ");
		
    this.text = text;
    this.style = style;
    
    TextMeasurer measurer = new TextMeasurer(getFont(), text);
    this.ascent = measurer.getAscent();
    this.descent = measurer.getDescent();
    this.leading = measurer.getLeading();
    this.width = measurer.getWidth();
  }

  
  public FormattedTextStyle getStyle()
  {
    return style;
  }

  
  public String getText()
  {
    return text;
  }
  
  
  /**
   * Gets the ascent of this text in mm.
   */
  public float getAscent()
  {
  	return ascent;
  }
  
  
  /**
   * Gets the descent of this text in mm.
   */
  public float getDescent()
  {
  	return descent;
  }
  
  
  /**
   * Gets the leading of this text in mm.
   */
  public float getLeading()
  {
  	return leading;
  }
  
  
  /**
   * Gets the width of this text in mm.
   */
  public float getWidth()
  {
  	return width;
  }
  
  
  public Font getFont()
  {
  	if (style == null)
  	{
  		return FormattedTextStyle.getDefaultFont();
  	}
  	else
  	{
  		return style.getFont();
  	}
  }
  
  
  @Override public String toString()
  {
  	return text;
  }
  
  
}
