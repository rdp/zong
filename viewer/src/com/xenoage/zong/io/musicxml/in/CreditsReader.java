package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.zong.io.musicxml.in.readers.FontInfoReader.readFontInfo;
import static com.xenoage.zong.io.musicxml.in.readers.OtherReader.readAlignment;

import java.awt.Color;
import java.awt.Font;

import com.xenoage.util.font.FontInfo;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.core.format.ScoreFormat;
import com.xenoage.zong.core.text.Alignment;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextElement;
import com.xenoage.zong.data.text.FormattedTextParagraph;
import com.xenoage.zong.data.text.FormattedTextString;
import com.xenoage.zong.data.text.FormattedTextStyle;
import com.xenoage.zong.documents.ScoreDocument;
import com.xenoage.zong.layout.Page;
import com.xenoage.zong.layout.frames.TextFrame;
import com.xenoage.zong.musicxml.types.MxlCredit;
import com.xenoage.zong.musicxml.types.MxlCreditWords;
import com.xenoage.zong.musicxml.types.MxlFormattedText;
import com.xenoage.zong.musicxml.types.MxlScorePartwise;
import com.xenoage.zong.musicxml.types.attributes.MxlColor;
import com.xenoage.zong.musicxml.types.choice.MxlCreditContent.MxlCreditContentType;
import com.xenoage.zong.musicxml.types.enums.MxlVAlign;


/**
 * This class reads the credit elements of a MusicXML 2.0
 * document and creates {@link TextFrame}s out of them.
 * 
 * @author Andreas Wenger
 */
public final class CreditsReader
{
	
	
	/**
	 * Reads the top-level credit elements of the given MusicXML 2.0
	 * document, creates {@link TextFrame}s out of them and adds
	 * them to the given {@link ScoreDocument}, using the given {@link ScoreFormat}.
	 */
	public static void read(MxlScorePartwise mxlScorePartwise, ScoreDocument scoreDoc, ScoreFormat scoreFormat)
	{
		//get all credit elements
		for (MxlCredit credit : mxlScorePartwise.getScoreHeader().getCredits())
		{
			addTextFrame(credit, scoreDoc, scoreFormat);
		}
	}
	
	
	/**
	 * Adds the given credit element as a {@link TextFrame}.
	 */
	private static void addTextFrame(MxlCredit credit, ScoreDocument scoreDoc, ScoreFormat scoreFormat)
	{
		if (credit.getContent().getCreditContentType() == MxlCreditContentType.CreditWords)
		{
			MxlCreditWords mxlCreditWords = (MxlCreditWords) credit.getContent();
			//create formatted text
			FormattedText text = createFormattedText(mxlCreditWords, new FontInfo(scoreFormat.getLyricFont()));
			//compute position (read only the position of the first credit-words element)
			MxlFormattedText mxlFirstCreditWords = null;
			Point2f position = new Point2f(10, 10);
			Page page = scoreDoc.getDefaultLayout().getPages().get(0);
			float tenths = scoreFormat.getInterlineSpace() / 10;
			for (MxlFormattedText mxlFormattedText : mxlCreditWords.getItems())
			{
				mxlFirstCreditWords = mxlFormattedText;
				position = new Point2f(
					notNull(mxlFirstCreditWords.getPrintStyle().getPosition().getDefaultX(), 10f) * tenths,
					page.getFormat().getSize().height -
						notNull(mxlFirstCreditWords.getPrintStyle().getPosition().getDefaultY(), 10f) * tenths);
				break;
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
			Alignment alignment = readAlignment(mxlFirstCreditWords.getHAlign());
			if (alignment == null)
			{
				alignment = readAlignment(mxlFirstCreditWords.getJustify());
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
			MxlVAlign mxlVAlign = null;
			if (mxlFirstCreditWords != null)
			{
				mxlVAlign = mxlFirstCreditWords.getVAlign();
			}
			if (mxlVAlign == null || mxlVAlign == MxlVAlign.Top)
			{
				position = position.add(0, size.height / 2);
			}
			else if (mxlVAlign == MxlVAlign.Middle)
			{
				//already centered
			}
			else if (mxlVAlign == MxlVAlign.Bottom || mxlVAlign == MxlVAlign.Baseline)
			{
				position = position.add(0, -size.height / 2);
			}
			//create and add TextFrame
			page.addFrame(new TextFrame(text, position, size));
		}
	}


	/**
	 * Creates a {@link FormattedText} and returns it.
	 */
	private static FormattedText createFormattedText(MxlCreditWords mxlCreditWords,
		FontInfo defaultFont)
	{
		FormattedText text = new FormattedText();
		FormattedTextParagraph paragraph = new FormattedTextParagraph();
		//iterate through all credit-words elements.
		//currently we are only interested in credit-words elements on page 1.
		Alignment alignment = null;
		for (MxlFormattedText mxlFormattedText : mxlCreditWords.getItems())
		{
			//read text. if empty, ignore this element
			String textContent = mxlFormattedText.getValue();
			if (textContent.length() == 0)
				continue;
			//apply horizontal alignment, if set, otherwise keep the old value
			Alignment newAlignment = readAlignment(mxlFormattedText.getJustify());
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
					FontInfo fontInfo = readFontInfo(mxlFormattedText.getPrintStyle().getFont());
					fontInfo = fontInfo.merge(defaultFont);
					Font font = fontInfo.createFont();
					//color
					Color color = null;
					MxlColor mxlColor = mxlFormattedText.getPrintStyle().getColor();
					if (mxlColor != null)
						color = mxlColor.getValue();
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
		//add non-empty paragraph at the end
		if (paragraph.getElementsCount() > 0)
		{
			text.addParagraph(paragraph);
		}
		return text;
	}


}
