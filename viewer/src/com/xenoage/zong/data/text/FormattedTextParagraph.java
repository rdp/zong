package com.xenoage.zong.data.text;

import java.util.LinkedList;

import com.xenoage.util.iterators.It;
import com.xenoage.util.StringTools;
import com.xenoage.zong.core.text.Alignment;


/**
 * One paragraph within a {@link FormattedText}.
 * 
 * A paragraph is a String with the following attributes for substrings: font
 * name, size, color, bold, italic, underline, strikethrough,
 * superscript/subscript.
 * 
 * @author Andreas Wenger
 */
public class FormattedTextParagraph
{

	private LinkedList<FormattedTextElement> elements = new LinkedList<FormattedTextElement>();
	private Alignment alignment = Alignment.Left;
	
	private float cacheAscent = 0;
	private float cacheDescent = 0;
	private float cacheLeading = 0;
	
	
	/**
	 * Creates a new {@link FormattedTextParagraph}.
	 */
	public FormattedTextParagraph()
	{
	}
	
	
	/**
	 * Creates a new {@link FormattedTextParagraph} with the given alignment.
	 */
	public FormattedTextParagraph(Alignment alignment)
	{
		this.alignment = alignment;
	}
	
	
	/**
	 * Creates a new {@link FormattedTextParagraph} with the given elements and alignment.
	 */
	private FormattedTextParagraph(LinkedList<FormattedTextElement> elements, Alignment alignment)
	{
		this.elements = elements;
		this.alignment = alignment;
		for (FormattedTextElement element : elements)
			updateCache(element);
	}


	/**
	 * Adds the given text, using a default style.
	 */
	public void addElement(String text)
	{
		addElement(new FormattedTextString(text));
	}


	/**
	 * Adds the given text, using the given style.
	 */
	public void addElement(String text, FormattedTextStyle style)
	{
		addElement(new FormattedTextString(text, style));
	}


	/**
	 * Adds the given {@link FormattedTextElement}.
	 */
	public void addElement(FormattedTextElement element)
	{
		elements.add(element);
		updateCache(element);
	}


	/**
	 * Gets the plain text.
	 */
	public String getText()
	{
		String ret = "";
		for (FormattedTextElement e : elements)
			ret += e.getText();
		return ret;
	}
	
	
	/**
	 * Gets the number of elements.
	 */
	public int getElementsCount()
	{
		return elements.size();
	}


	/**
	 * Gets the elements of this formatted text.
	 */
	public It<FormattedTextElement> getElements()
	{
		return new It<FormattedTextElement>(elements);
	}


	/**
	 * Gets the {@link Alignment} of this paragraph.
	 */
	public Alignment getAlignment()
	{
		return alignment;
	}


	/**
	 * Sets the {@link Alignment} of this paragraph.
	 */
	public void setAlignment(Alignment alignment)
	{
		this.alignment = alignment;
	}
	
	
	/**
	 * Gets the ascent of this paragraph in mm, that is the height
	 * of the paragraph above the baseline.
	 */
	public float getAscent()
	{
		return cacheAscent;
	}
	
	
	/**
	 * Gets the descent of this paragraph in mm, that is the height
	 * of the paragraph below the baseline.
	 */
	public float getDescent()
	{
		return cacheDescent;
	}
	
	
	/**
	 * Gets the leading of this paragraph in mm, that is the height
	 * between the bottommost point of this paragraph to the topmost
	 * point of the following paragraph.
	 */
	public float getLeading()
	{
		return cacheLeading;
	}
	
	
	/**
	 * Gets the width of this paragraph in mm.
	 */
	public float getWidthMm()
	{
		float ret = 0;
		for (FormattedTextElement element : elements)
		{
			ret += element.getWidth();
		}
		return ret;
	}
	
	
	/**
	 * Gets the height of this paragraph in mm.
	 */
	public float getHeightMm()
	{
		float maxAscent = 0;
		float maxDescentAndLeading = 0;
		for (FormattedTextElement element : elements)
		{
			maxAscent = Math.max(maxAscent, element.getAscent());
			maxDescentAndLeading = Math.max(maxDescentAndLeading,
				element.getDescent() + element.getLeading());
		}
		return maxAscent + maxDescentAndLeading;
	}
	
	
	/**
   * Breaks this paragraph up into one or more lines so
   * that it fits into the given width and returns the result.
   * The result is no deep copy of the whole paragraph, instead references to the
   * unmodified parts are used.
   */
  public LinkedList<FormattedTextParagraph> lineBreak(float width)
  {
  	LinkedList<FormattedTextParagraph> ret = new LinkedList<FormattedTextParagraph>();
  	if (elements.size() == 0)
  	{
  		//nothing to break up
  		ret.add(this);
  	}
  	else
  	{
  		//queue with elements still to format
  		@SuppressWarnings("unchecked")
  		LinkedList<FormattedTextElement> queue = (LinkedList<FormattedTextElement>) elements.clone();
  		//loop through all elements
  		while (!queue.isEmpty())
  		{
  			//create list to collect elements for current line
  			LinkedList<FormattedTextElement> line = new LinkedList<FormattedTextElement>();
  			float lineWidth = 0;
  			//add elements to the line until width is reached
  			do
				{
  				FormattedTextElement currentElement = queue.removeFirst();
  				line.add(currentElement);
  				lineWidth += currentElement.getWidth();
				}
				while (lineWidth <= width && !queue.isEmpty());
  			//line too wide?
  			if (lineWidth > width)
  			{
  				//yes. we have to do a line break.
  				//for string elements, we can divide the text and put one part in this
  				//line and the other part into the next one. for symbols, we always begin a new line.
  				if (line.getLast() instanceof FormattedTextString)
  				{
  					FormattedTextString lastElement = (FormattedTextString) line.getLast();
	  				float lineWidthBeforeLineBreak = lineWidth;
	  				//first test if line is wide enough for at least one character (if it is a String)
	  				//or the whole symbol (if it is a symbol)
  					FormattedTextString firstCharElement =
	  					new FormattedTextString(lastElement.getText().substring(0, 1), lastElement.getStyle());
	  				if (firstCharElement.getWidth() > width)
	  				{
	  					//not enough space for even one character. return empty list.
	  					return new LinkedList<FormattedTextParagraph>();
	  				}
	  				//spacing character within this line?
	  				boolean lineBreakSuccess = false;
	  				if (containsLineBreakCharacter(line))
	  				{
	  					//go back until the last spacing character.
	  					//search for element to cut
	  					FormattedTextString searchCuttedElement = (FormattedTextString) line.removeLast();
	  					lineWidth -= searchCuttedElement.getWidth();
	  					int queuedElementsCount = 0;
	  				  while (!StringTools.containsLineBreakCharacter(searchCuttedElement.getText()))
	  				  {
	  				  	queue.addFirst(searchCuttedElement);
	  				  	queuedElementsCount++;
	  				  	searchCuttedElement = (FormattedTextString) line.removeLast();
	  				  	lineWidth -= searchCuttedElement.getWidth();
	  				  }
	  				  FormattedTextString cuttedElement = searchCuttedElement;
	  				  //find last spacing character that fits into the given width and split the
	  				  //cuttedElement there
	  				  FormattedTextString forThisLine = null;
	  				  FormattedTextString forThisLineTrimRight = null;
	  				  for (int i = cuttedElement.getText().length() - 1; i >= 0; i--)
	  					{
	  				  	char c = cuttedElement.getText().charAt(i);
	  				  	if (StringTools.isLineBreakCharacter(c))
	  				  	{
		  						forThisLine = new FormattedTextString(cuttedElement.getText().substring(0, i + 1), cuttedElement.getStyle());
		  						//ignore spaces at the end
		  						String forThisLineTrimRightText = StringTools.trimRight(forThisLine.getText());
		  						forThisLineTrimRight = (forThisLineTrimRightText.length() > 0 ?
		  							new FormattedTextString(forThisLineTrimRightText, forThisLine.getStyle()) : null);
	  				  		if (forThisLineTrimRight == null || lineWidth + forThisLineTrimRight.getWidth() <= width)
		  						{
		  							break;
		  						}
	  				  	}
	  					}
	  				  //if the left side of the cutted line is now short enough to fit into the width, we had
	  				  //success and apply the linebreak. otherwise we must do a linebreak in the middle of a word.
	  				  if (forThisLineTrimRight == null || lineWidth + forThisLineTrimRight.getWidth() <= width)
	  				  {
	  				  	lineBreakSuccess = true;
		  					//complete this line
		  				  line.add(forThisLine);
		  					ret.add(new FormattedTextParagraph(line, alignment));
		  					//begin next line
		  					if (forThisLine.getText().length() < cuttedElement.getText().length())
		  					{
			  					FormattedTextString forNextLine = new FormattedTextString(cuttedElement.getText().substring(forThisLine.getText().length()),
			  						cuttedElement.getStyle());
			  					queue.addFirst(forNextLine);
		  					}
	  				  }
	  				  else
	  				  {
	  				  	lineBreakSuccess = false;
	  				  	lineWidth = lineWidthBeforeLineBreak;
	  				  	//restore original line and queue
	  				  	for (int i = 0; i < queuedElementsCount; i++)
	  				  	{
	  				  		line.addLast(queue.removeFirst());
	  				  	}
	  				  	line.addLast(cuttedElement);
	  				  }
	  				}
	  				if (!lineBreakSuccess)
	  				{
	  					//there is no spacing character in this line or we had no success using it,
	  					//so we have to do a linebreak in the middle of a word.
	  					//since we have added element after element, the possible line break must be
	  					//within the last element.
	  					FormattedTextString cuttedElement = (FormattedTextString) line.removeLast();
	  					lineWidth -= cuttedElement.getWidth();
	  					FormattedTextString forThisLine = null;
	  					for (int i = cuttedElement.getText().length() - 1; i >= -1; i--)
	  					{
	  						if (i >= 0)
	  						{
		  						forThisLine = new FormattedTextString(cuttedElement.getText().substring(0, i + 1), cuttedElement.getStyle());
		  						//ignore spaces at the end
		  						String forThisLineTrimRightText = StringTools.trimRight(forThisLine.getText());
		  						FormattedTextString forThisLineTrimRight = (forThisLineTrimRightText.length() > 0 ?
		  							new FormattedTextString(forThisLineTrimRightText, forThisLine.getStyle()) : null);
	  				  		if (forThisLineTrimRight == null || lineWidth + forThisLineTrimRight.getWidth() <= width)
		  						{
		  							break;
		  						}
	  						}
	  						else
	  						{
	  							forThisLine = null;
	  						}
	  					}
	  					//complete this line
	  					if (forThisLine != null)
	  					{
	  						line.add(forThisLine);
	  					}
	  					ret.add(new FormattedTextParagraph(line, alignment));
	  					//begin next line
	  					if (forThisLine == null || forThisLine.getText().length() < cuttedElement.getText().length())
	  					{
	  						FormattedTextString forNextLine = new FormattedTextString(
		  						(forThisLine != null ? cuttedElement.getText().substring(forThisLine.getText().length()) : cuttedElement.getText()),
		  						cuttedElement.getStyle());
		  					queue.addFirst(forNextLine);
	  					}
	  				}
  				}
  				else if (line.getLast() instanceof FormattedTextSymbol)
  				{
  					if (line.size() > 1)
  					{
  						//at least two elements, so one can be placed in this line
	  					//move symbol into next line
	  					FormattedTextElement symbol = line.removeLast();
	  					ret.add(new FormattedTextParagraph(line, alignment));
	  					//begin next line
	  					queue.addFirst(symbol);
  					}
  					else
  					{
  						//not enough space for even one symbol. return empty list.
	  					return new LinkedList<FormattedTextParagraph>();
  					}
  				}
  			}
  			else
  			{
  				//no. we can use exactly that line. that means, this was
  				//the last line and we are finished.
  				ret.add(new FormattedTextParagraph(line, alignment));
  				break;
  			}
  		}
  	}
  	return ret;
  }
	
	
	/**
	 * Updates the cache for ascent, descent and leading, using the given
	 * new element.
	 */
	private void updateCache(FormattedTextElement e)
	{
		cacheAscent = Math.max(cacheAscent, e.getAscent());
		cacheDescent = Math.max(cacheDescent, e.getDescent());
		cacheLeading = Math.max(cacheLeading, e.getLeading());
	}
	
	
	/**
	 * Returns true, if a line break character is found in the given list
	 * of formatted text elements, but not at the end (trailing spaces are ignored).
	 */
	private boolean containsLineBreakCharacter(LinkedList<FormattedTextElement> elements)
	{
		for (FormattedTextElement element : elements)
		{
			if (element != elements.getLast())
			{
				if (StringTools.containsLineBreakCharacter(element.getText()))
					return true;
			}
			else
			{
				return StringTools.containsLineBreakCharacter(StringTools.trimRight(element.getText()));
			}
		}
		return false;
	}
	
	
	@Override public String toString()
  {
  	StringBuilder ret = new StringBuilder();
  	for (FormattedTextElement element : elements)
  	{
  		ret.append(element.toString());
  	}
  	return ret.toString();
  }
	

}
