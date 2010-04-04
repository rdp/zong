package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.util.NullTools.notNull;

import java.awt.Color;
import java.awt.Font;

import proxymusic.Credit;
import proxymusic.ScorePartwise;
import proxymusic.Valign;

import com.xenoage.util.font.FontInfo;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.core.text.Alignment;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextElement;
import com.xenoage.zong.data.text.FormattedTextParagraph;
import com.xenoage.zong.data.text.FormattedTextString;
import com.xenoage.zong.data.text.FormattedTextStyle;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.TextFrame;


/**
 * This class reads the credit elements of a MusicXML 2.0
 * document and creates {@link TextFrame}s out of them.
 * 
 * @author Andreas Wenger
 */
class MxlCredits
{
	
	/* TODO(musicxml-in)
	private ScoreDocument scoreDoc;
	private MxlDefaults defaults;
	
	
	/**
	 * Reads the top-level credit elements of the given MusicXML 2.0
	 * document, creates {@link TextFrame}s out of them and adds
	 * them to the given {@link ScoreDocument}, using the given {@link MxlDefaults}.
	 *-/
	public static void read(ScorePartwise doc, ScoreDocument scoreDoc, MxlDefaults defaults)
	{
		new MxlCredits(scoreDoc, defaults).read(doc);
	}
	
	
	private MxlCredits(ScoreDocument scoreDoc, MxlDefaults defaults)
	{
		this.scoreDoc = scoreDoc;
		this.defaults = defaults;
	}
	
	
	/**
	 * Do the work.
	 *-/
	private void read(ScorePartwise doc)
	{
		//get all credit elements
		for (Credit credit : doc.getCredit())
		{
			addTextFrame(credit);
		}
	}
	
	
	/**
	 * Adds the given credit element as a {@link TextFrame}.
	 *-/
	private void addTextFrame(Credit credit)
	{
		//create formatted text
		FormattedText text = createFormattedText(credit);
		//compute position (read only the position of the first credit-words element)
		Point2f position = new Point2f(10, 10);
		Page page = scoreDoc.getDefaultLayout().getPages().get(0);
		proxymusic.FormattedText mxlFirstCreditWords = null;
		float tenths = scoreDoc.getScore(0).getScoreFormat().getInterlineSpace() / 10;
		for (Object o : credit.getLinkOrBookmarkOrCreditImage())
		{
			if (o instanceof proxymusic.FormattedText)
			{
				mxlFirstCreditWords = (proxymusic.FormattedText) o;
				position = new Point2f(
					notNull(mxlFirstCreditWords.getDefaultX(), 10).floatValue() * tenths,
					page.getFormat().getSize().height -
						notNull(mxlFirstCreditWords.getDefaultY(), 10).floatValue() * tenths);
				break;
			}
		}
		//compute size
		//this is the width of the widest paragraph and the height of the sum of all paragraphs
		float maxParagraphWidth = 1; //at least 1 mm
		float sumParagraphsHeight = 1; //at least 1 mm
		for (FormattedTextParagraph paragraph : text.getParagraphs())
		{
			maxParagraphWidth = Math.max(maxParagraphWidth, paragraph.getWidthMm());
			sumParagraphsHeight += paragraph.getHeightMm();
		}
		Size2f size = new Size2f(maxParagraphWidth, sumParagraphsHeight);
		//horizontal alignment:
		//try with halign first, and if not set, use justify
		Alignment alignment = null;
		if (mxlFirstCreditWords != null)
		{
			//use halign for horizontal alignment
			alignment = Util.readAlignment(mxlFirstCreditWords.getHalign());
			//if still unknown, use justify
			if (alignment == null)
			{
				alignment = Util.readAlignment(mxlFirstCreditWords.getJustify());
			}
		}
		if (alignment == null || alignment == Alignment.Left)
		{
			position = position.add(size.width / 2, 0);
		}
		else if (alignment == Alignment.Center)
		{
			//already centered
		}
		else if (alignment == Alignment.Right)
		{
			position = position.add(-size.width / 2, 0);
		}
		//vertical alignment
		Valign valign = null;
		if (mxlFirstCreditWords != null)
		{
			valign = mxlFirstCreditWords.getValign();
		}
		if (valign == null || valign == Valign.TOP)
		{
			position = position.add(0, size.height / 2);
		}
		else if (valign == Valign.MIDDLE)
		{
			//already centered
		}
		else if (valign == Valign.BOTTOM || valign == Valign.BASELINE)
		{
			position = position.add(0, -size.height / 2);
		}
		//create and add TextFrame
		page.addFrame(new TextFrame(text, position, size));
	}


	/**
	 * Creates a {@link FormattedText} and returns it.
	 *-/
	private FormattedText createFormattedText(Credit credit)
	{
		FormattedText text = new FormattedText();
		FormattedTextParagraph paragraph = new FormattedTextParagraph();
		//iterate through all credit-words elements.
		//currently we are only interested in credit-words elements on page 1.
		Alignment alignment = null;
		for (Object mxlElement : credit.getLinkOrBookmarkOrCreditImage())
		{
			if (mxlElement instanceof proxymusic.FormattedText) //credit-words
			{
				proxymusic.FormattedText mxlCreditWords = (proxymusic.FormattedText) mxlElement;
				//read text. if empty, ignore this element
				String textContent = mxlCreditWords.getValue();
				if (textContent.length() == 0)
					continue;
				//apply horizontal alignment, if set, otherwise keep the old value
				Alignment newAlignment = Util.readAlignment(mxlCreditWords.getJustify());
				if (newAlignment != null)
				{
					alignment = newAlignment;
					paragraph.setAlignment(alignment);
				}
				//since we use paragraphs but MusicXML doesn't, split
				//the text where there are line breaks.
				String[] textLines = textContent.split("\n");
				boolean endsWithLineBreak = textContent.endsWith("\n");
				//append the first line to the current paragraph, then create
				//new paragraphs for the following lines
				for (int iLine = 0; iLine < textLines.length; iLine++)
				{
					//read line
					String textLine = textLines[iLine];
					if (textLine.length() > 0)
					{
						//font
						FontInfo fontInfo = new MxlFontInfo(mxlCreditWords).getFontInfo();
						fontInfo = fontInfo.merge(defaults.getWordFontInfo());
						Font font = fontInfo.createFont();
						//color
						Color color = Util.getColor(mxlCreditWords.getColor());
						//create text element
						FormattedTextElement textElement = new FormattedTextString(
							textLine, new FormattedTextStyle(font, color, null));
						paragraph.addElement(textElement);
						if (iLine > 0)
						{
							//not the first line: we have to create a new paragraph
							text.addParagraph(paragraph);
							paragraph = new FormattedTextParagraph();
							//apply horizontal alignment, if set
							if (alignment != null)
							{
								paragraph.setAlignment(alignment);
							}
						}
					}
				}
				if (endsWithLineBreak)
				{
					//create a new paragraph
					text.addParagraph(paragraph);
					paragraph = new FormattedTextParagraph();
					//apply horizontal alignment, if set
					if (alignment != null)
					{
						paragraph.setAlignment(alignment);
					}
				}
			}
		}
		//add non-empty paragraph at the end
		if (paragraph.getElementsCount() > 0)
		{
			text.addParagraph(paragraph);
		}
		return text;
	}
	
	*/

}
