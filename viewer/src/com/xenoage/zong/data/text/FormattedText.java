package com.xenoage.zong.data.text;

import java.util.LinkedList;

import com.xenoage.util.iterators.It;


/**
 * Class for formatted text.
 * 
 * A formatted text contains a list of paragraphs
 * with multi-styled text.
 * 
 * TODO: add a lineBreak-method that returns a new
 * FormattedText based on a given FormattedText that
 * has additional line breaks after a given width.
 *
 * @author Andreas Wenger
 */
public class FormattedText
{
  
  private LinkedList<FormattedTextParagraph> paragraphs = new LinkedList<FormattedTextParagraph>();
  
  
  /**
   * Creates an empty {@link FormattedText}.
   */
  public FormattedText()
  {
  }
  
  
  /**
   * Creates a {@link FormattedText} with the given paragraph.
   */
  public FormattedText(FormattedTextParagraph paragraph)
  {
  	paragraphs.add(paragraph);
  }
  
  
  /**
   * Creates a {@link FormattedText} with a single paragraph, using
   * the given style and alignment.
   */
  public FormattedText(String text, FormattedTextStyle style, Alignment alignment)
  {
		FormattedTextParagraph paragraph = new FormattedTextParagraph();
		paragraph.setAlignment(alignment);
		paragraph.addElement(text, style);
		this.addParagraph(paragraph);
  }
  
  
  /**
	 * Adds a the given paragraph to this text.
	 */
	public void addParagraph(FormattedTextParagraph paragraph)
	{
		this.paragraphs.add(paragraph);
	}
  
	
	/**
	 * Gets the number of paragraphs.
	 */
	public int getParagraphsCount()
	{
		return paragraphs.size();
	}
	
  
  /**
   * Gets the paragraphs of this text.
   */
  public It<FormattedTextParagraph> getParagraphs()
  {
  	return new It<FormattedTextParagraph>(paragraphs);
  }
  
  
  /**
   * Breaks this formatted text up so that it fits into the given width
   * and returns the result.
   * The result is no deep copy of the whole text, instead references to the
   * unmodified parts are used.
   */
  public FormattedText lineBreak(float width)
  {
  	FormattedText ret = new FormattedText();
  	//break up paragraphs
  	for (FormattedTextParagraph paragraph : paragraphs)
  	{
  		for (FormattedTextParagraph paragraphLine : paragraph.lineBreak(width))
  		{
  			ret.addParagraph(paragraphLine);
  		}
  	}
  	return ret;
  }
  
  
  @Override public String toString()
  {
  	StringBuilder ret = new StringBuilder();
  	for (FormattedTextParagraph paragraph : paragraphs)
  	{
  		ret.append(paragraph.toString());
  		ret.append('\n');
  	}
  	ret.delete(ret.length() - 1, ret.length()); //remove last '\n'
  	return ret.toString();
  }
  
  
  /**
   * Gets the width of this text in mm (without automatic line breaks).
   */
  public float getWidth()
  {
  	float maxWidth = 0;
  	for (FormattedTextParagraph paragraph : paragraphs)
  	{
  		maxWidth = Math.max(maxWidth, paragraph.getWidthMm());
  	}
  	return maxWidth;
  }
  
  
  /**
   * Gets the height of this text in mm (without automatic line breaks).
   */
  public float getHeight()
  {
  	float sumHeight = 0;
  	for (FormattedTextParagraph paragraph : paragraphs)
  	{
  		sumHeight += paragraph.getHeightMm();
  	}
  	return sumHeight;
  }


}
