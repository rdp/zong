package com.xenoage.zong.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.EnumSet;

import javax.swing.*;
import javax.swing.text.*;

import com.xenoage.util.font.FontInfo;
import com.xenoage.util.font.FontStyle;
import com.xenoage.util.math.Size2f;
import com.xenoage.util.math.Size2i;
import com.xenoage.zong.data.text.Alignment;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextElement;
import com.xenoage.zong.data.text.FormattedTextParagraph;
import com.xenoage.zong.data.text.FormattedTextString;
import com.xenoage.zong.data.text.FormattedTextStyle;
import com.xenoage.zong.data.text.Superscript;
import com.xenoage.zong.gui.util.ScaledEditorKit;


/**
 * This is a Swing component which allows to edit
 * styled text.
 * 
 * TODO: move into editor package after eliminating all
 * usages in the viewer.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class TextEditor extends JTextPane
{
	private int width;
	private int height;
	
	
	/**
	 * Creates an unscaled {@link TextEditor}.
	 */
	public TextEditor()
	{
	}
	
	
	/**
	 * Creates a scaled {@link TextEditor} with the given width, height
	 * and zoom.
	 */
	public TextEditor(int width, int height, float zoom)
	{
		this.width = width;
		this.height = height;
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setEditorKit(new ScaledEditorKit());
		this.getDocument().putProperty("i18n", Boolean.TRUE);
		this.setPreferredSize(new Dimension(width,height));
	}
	
	
	/**
	 * Creates and returns TextEditor for use in a dialog.
	 * It has the given background color and has no border.
	 */
	public static TextEditor createForDialog(Size2i size, Color backgroundColor)
	{
		TextEditor ret = new TextEditor();
		ret.width = size.width;
		ret.height = size.height;
		ret.setPreferredSize(new Dimension(size.width, size.height));
		ret.setBackground(backgroundColor);
		ret.setBorder(BorderFactory.createEmptyBorder());
		return ret;
	}
	

	/**
	 * Imports a {@link FormattedText} and shows it on
	 * this TextEditor.
	 */
	public void importFormattedText(FormattedText input)
	{
		//handle all paragraphs
		StyledDocument styledDoc = this.getStyledDocument();
		SimpleAttributeSet lastStyle = new SimpleAttributeSet();
		Alignment alignment = Alignment.Left;
		int line = 0;
		for (FormattedTextParagraph p : input.getParagraphs())
		{
			//apply the alignment of the paragraph (apply style to the first letter of the paragraph)
			alignment = p.getAlignment();
			SimpleAttributeSet paragraphStyle = new SimpleAttributeSet();
			alignment.applyOnAttributeSet(paragraphStyle);
			styledDoc.setParagraphAttributes(styledDoc.getLength(), 1, paragraphStyle, true);
			//handle all elements within this paragraph
			for (FormattedTextElement t : p.getElements()) //TODO: Symbols ?!
			{
				if (t instanceof FormattedTextString)
				{
					FormattedTextString s = (FormattedTextString) t;
					try
					{
						lastStyle = convertFormatting(s.getStyle());
						styledDoc.insertString(styledDoc.getLength(), s.getText(), lastStyle);
					}
					catch (BadLocationException e)
					{
					}
				}
			}
			//after the paragraph (not after the last): insert a line break
			if (line < input.getParagraphsCount() - 1)
			{
				try
				{
					styledDoc.insertString(styledDoc.getLength(), "\n", lastStyle);
				}
				catch (BadLocationException e)
				{
				}
			}
			line++;
		}
	}

	
	/**
	 * Converts the {@link FormattedTextStyle} in a {@link SimpleAttributeSet}
	 * that is needed for displaying the text.
	 */
	private SimpleAttributeSet convertFormatting(FormattedTextStyle style)
	{
		SimpleAttributeSet attr = new SimpleAttributeSet();
		if (style == null)
		{
			return attr;
		}
		
		//font style
		EnumSet<FontStyle> fontStyle = style.getFontStyle();
		StyleConstants.setBold(attr, fontStyle.contains(FontStyle.Bold));
		StyleConstants.setItalic(attr, fontStyle.contains(FontStyle.Italic));
		StyleConstants.setUnderline(attr, fontStyle.contains(FontStyle.Underline));
		StyleConstants.setStrikeThrough(attr, fontStyle.contains(FontStyle.Strikethrough));
		
		//color
		StyleConstants.setForeground(attr, style.getColor());
		
		//font
		Font font = style.getFont();
		StyleConstants.setFontFamily(attr, font.getFamily());
		StyleConstants.setFontSize(attr, font.getSize());
		
		//superscript
		if (style.getSuperscript() == Superscript.Super)
		{
			StyleConstants.setSuperscript(attr, true);
		}
		else if (style.getSuperscript() == Superscript.Sub)
		{
			StyleConstants.setSubscript(attr, true);
		}
		
		return attr;
	}

	
	/**
	 * Creates a {@link FormattedText} from the content of this text box
	 * and returns it.
	 */
	public FormattedText exportFormattedText()
	{
		FormattedText text = new FormattedText();
		StyledDocument styledDoc = this.getStyledDocument();
		int endPos = styledDoc.getLength();
		AttributeSet attribute = null;
		String textelement = "";
		String letter = "";
		FormattedTextParagraph paragraph = new FormattedTextParagraph();
		boolean paragraphAlignmentSet = false;

		//iterate through the letters. If the style has changed, create a new
		//element, otherwise just attach it to the old one, and create a new
		//paragraph, when '\n' is found
		for (int i = 0; i < endPos; i++)
		{
			try
			{
				letter = styledDoc.getText(i, 1);
			}
			catch (BadLocationException e)
			{
			}
			//line break: create new paragraph
			if (letter.equals("\n"))
			{
				if (!textelement.equals(""))
				{
					paragraph.addElement(textelement, convertFormatting(attribute));
					textelement = "";
				}
				text.addParagraph(paragraph);
				paragraph = new FormattedTextParagraph();
				paragraphAlignmentSet = false;
			}
			else if (attribute != styledDoc.getCharacterElement(i).getAttributes())
			{
				//set alignment, if not already done
				if (!paragraphAlignmentSet)
				{
					paragraph.setAlignment(Alignment.fromAttributeSet(
						styledDoc.getParagraphElement(i).getAttributes()));
					paragraphAlignmentSet = true;
				}
				//style has changed, so save old element and create a new one
				if (!textelement.equals(""))
				{
					paragraph.addElement(textelement, convertFormatting(attribute));
				}
				attribute = styledDoc.getCharacterElement(i).getAttributes();
				textelement = letter;
			}
			else
			{
				//style stayed the same, so just append
				textelement += letter;
			}
		}
		if (!textelement.equals(""))
		{
			//save the last string
			paragraph.addElement(textelement, convertFormatting(attribute));
		}
		if (paragraph.getElementsCount() > 0)
		{
			//add (non-empty) paragraph
			text.addParagraph(paragraph);
		}
		return text;
	}
	

	private FormattedTextStyle convertFormatting(AttributeSet attr)
	{
		if (attr == null)
		{
			return new FormattedTextStyle();
		}
		
		//font style
		EnumSet<FontStyle> fontStyle = FontStyle.getNone();
		if (StyleConstants.isBold(attr))
		{
			fontStyle.add(FontStyle.Bold);
		}
		if (StyleConstants.isItalic(attr))
		{
			fontStyle.add(FontStyle.Italic);
		}
		if (StyleConstants.isUnderline(attr))
		{
			fontStyle.add(FontStyle.Underline);
		}
		if (StyleConstants.isStrikeThrough(attr))
		{
			fontStyle.add(FontStyle.Strikethrough);
		}
		
		//superscript
		Superscript superscript = Superscript.Normal;
		if (StyleConstants.isSuperscript(attr))
		{
			superscript = Superscript.Super;
		}
		if (StyleConstants.isSubscript(attr))
		{
			superscript = Superscript.Sub;
		}
		
		//font
		FontInfo fontInfo = new FontInfo(StyleConstants.getFontFamily(attr),
			(float) StyleConstants.getFontSize(attr), fontStyle);
		
		return new FormattedTextStyle(fontInfo, StyleConstants.getForeground(attr), superscript);
	}

	
	@Override public void repaint(int x, int y, int width, int height)
	{
		super.repaint(0, 0, getWidth(), getHeight());
	}

	
	public void setZoom(float zoom)
	{
		this.getDocument().putProperty("ZOOM_FACTOR", (double) zoom);
		this.repaint();
	}
	
	
	@Override public Dimension getPreferredSize()
	{
		Double z = (Double)this.getDocument().getProperty("ZOOM_FACTOR");
		float zoom=1;
		if (z!=null)
		{
			zoom = z.floatValue();
		}
		return new Dimension((int)(zoom *(float)width),(int)(zoom* (float)height));
		
	}

	
	/**
	 * Computes the optimum size of this {@link TextEditor} in px.
	 */
	public Size2f computeOptimumSize()
	{
		View v = this.getUI().getRootView(this);
		v.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		return new Size2f(v.getPreferredSpan(View.X_AXIS), v.getPreferredSpan(View.Y_AXIS));
	}
	
	
}
