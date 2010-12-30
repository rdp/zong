package com.xenoage.zong.data.text;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JLabel;

import org.junit.Test;

import com.xenoage.util.font.FontInfo;
import com.xenoage.util.font.FontStyle;


/**
 * Test cases for a {@link FormattedTextParagraph}
 * 
 * @author Andreas Wenger
 */
public class FormattedTextParagraphTest
{
	
	
	@Test public void testSimpleTextParagraph()
  {
    FormattedTextParagraph paragraph = new FormattedTextParagraph();
    paragraph.addElement("This is a simple text.");
    assertEquals("This is a simple text.", paragraph.getText());
  }
	
	
	@Test public void testFormattedTextParagraph()
  {
    FormattedTextParagraph paragraph = new FormattedTextParagraph();
    FormattedTextStyle style = new FormattedTextStyle(
    	new FontInfo((String) null, null, EnumSet.of(FontStyle.Bold)), Color.red, Superscript.Super);
    paragraph.addElement("This is a formatted text.", style);
    assertEquals("This is a formatted text.", paragraph.getText());
    style = ((FormattedTextString) paragraph.getElements().iterator().next()).getStyle();
    assertEquals(1, style.getFontStyle().size());
    assertEquals(true, style.getFontStyle().contains(FontStyle.Bold));
    assertEquals(Color.red, style.getColor());
    assertEquals(Superscript.Super, style.getSuperscript());
  }
	
	
	@Test public void testMixedStyledTextParagraph()
  {
    FormattedTextParagraph text = getMixedStyleTextParagraph();
    assertEquals("This is a mixed styled text!", text.getText());
    Iterator<FormattedTextElement> elements = text.getElements();
    FormattedTextStyle style = ((FormattedTextString) elements.next()).getStyle();
    assertEquals(true, style.getFontStyle().contains(FontStyle.Italic));
    assertEquals(true, style.getFontStyle().contains(FontStyle.Underline));
    assertEquals(14, style.getFont().getSize());
    style = ((FormattedTextString) elements.next()).getStyle();
    assertEquals(true, style.getFontStyle().contains(FontStyle.Bold));
    assertEquals(true, style.getFontStyle().contains(FontStyle.Strikethrough));
    assertEquals(Color.green, style.getColor());
  }

	
	public static FormattedTextParagraph getMixedStyleTextParagraph()
  {
  	FormattedTextParagraph paragraph = new FormattedTextParagraph();
    FormattedTextStyle style1 = new FormattedTextStyle(
    	new FontInfo(new JLabel().getFont().getName(), 14f,
    		EnumSet.of(FontStyle.Italic, FontStyle.Underline)));
    paragraph.addElement("This is ", style1);
    FormattedTextStyle style2 = new FormattedTextStyle(
    	new FontInfo(new JLabel().getFont().getName(), 14f,
    		EnumSet.of(FontStyle.Bold, FontStyle.Italic, FontStyle.Underline, FontStyle.Strikethrough)),
    		Color.green, null);
    paragraph.addElement("a mixed styled text!", style2);
    return paragraph;
  }
	
	
	@Test public void lineBreakTest()
  {
  	FormattedTextStyle style = new FormattedTextStyle(new FontInfo("Arial", 72f, null));
  	float widthW = new FormattedTextString("w", style).getWidth();
  	float widthMinus = new FormattedTextString("-", style).getWidth();
  	float widthSpace = new FormattedTextString(" ", style).getWidth();
  	
  	// | : where the line break should happen
  	
  	//1st test: "www|ww"
  	FormattedTextParagraph paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString("wwwww", style));
  	float width = 3.2f * widthW;
  	LinkedList<FormattedTextParagraph> lines = paragraph.lineBreak(width);
  	assertEquals(2, lines.size());
  	assertEquals("www", lines.get(0).getText());
  	assertEquals("ww", lines.get(1).getText());
  	
  	//2nd test: "ww-|www"
  	paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString("ww-www", style));
  	width = 3.2f * widthW + widthMinus;
  	lines = paragraph.lineBreak(width);
  	assertEquals(2, lines.size());
  	assertEquals("ww-", lines.get(0).getText());
  	assertEquals("www", lines.get(1).getText());
  	
  	//3rd test: like 2nd test, but with more elements: "w"+"w-|w"+"ww"
  	paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString("w", style));
  	paragraph.addElement(new FormattedTextString("w-w", style));
  	paragraph.addElement(new FormattedTextString("ww", style));
  	width = 3.2f * widthW + widthMinus;
  	lines = paragraph.lineBreak(width);
  	assertEquals(2, lines.size());
  	assertEquals("ww-", lines.get(0).getText());
  	assertEquals("www", lines.get(1).getText());
  	assertEquals(2, lines.get(0).getElementsCount());
  	assertEquals(2, lines.get(1).getElementsCount());
  	
  	//4th test: break at the right position: "www www |www"
  	paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString("www www www", style));
  	width = 7.2f * widthW + 2 * widthSpace;
  	lines = paragraph.lineBreak(width);
  	assertEquals(2, lines.size());
  	assertEquals("www www ", lines.get(0).getText());
  	assertEquals("www", lines.get(1).getText());
  	
    //5th test: "w|w"
  	paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString("ww", style));
  	width = 1.2f * widthW;
  	lines = paragraph.lineBreak(width);
  	assertEquals(2, lines.size());
  	assertEquals("w", lines.get(0).getText());
  	assertEquals("w", lines.get(1).getText());
  	
    //5th test: "ww", but not even enough space for one w
  	paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString("ww", style));
  	width = 0.8f * widthW;
  	lines = paragraph.lineBreak(width);
  	assertEquals(0, lines.size());
  	
    //6th test: " |ww"
  	paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString(" www", style));
  	width = 3.1f * widthW;
  	lines = paragraph.lineBreak(width);
  	assertEquals(2, lines.size());
  	assertEquals(" ", lines.get(0).getText());
  	assertEquals("www", lines.get(1).getText());
  	
  	//7th test: " |www|ww "
  	paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString(" wwwww ", style));
  	width = 3.1f * widthW;
  	lines = paragraph.lineBreak(width);
  	assertEquals(3, lines.size());
  	assertEquals(" ", lines.get(0).getText());
  	assertEquals("www", lines.get(1).getText());
  	assertEquals("ww ", lines.get(2).getText());
  	
  	//8th test: "ww www  w" -> "w|w |w|w|w  |w"
  	paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString("ww www  w", style));
  	width = 1.1f * widthW;
  	lines = paragraph.lineBreak(width);
  	assertEquals(6, lines.size());
  	assertEquals("w", lines.get(0).getText());
  	assertEquals("w ", lines.get(1).getText());
  	assertEquals("w", lines.get(2).getText());
  	assertEquals("w", lines.get(3).getText());
  	assertEquals("w  ", lines.get(4).getText());
  	assertEquals("w", lines.get(5).getText());
  	
  	//9th test: like 8th test, but using several elements
  	paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString("w", style));
  	paragraph.addElement(new FormattedTextString("w ", style));
  	paragraph.addElement(new FormattedTextString("ww", style));
  	paragraph.addElement(new FormattedTextString("w  w", style));
  	width = 1.1f * widthW;
  	lines = paragraph.lineBreak(width);
  	assertEquals(6, lines.size());
  	assertEquals("w", lines.get(0).getText());
  	assertEquals("w ", lines.get(1).getText());
  	assertEquals("w", lines.get(2).getText());
  	assertEquals("w", lines.get(3).getText());
  	assertEquals("w  ", lines.get(4).getText());
  	assertEquals("w", lines.get(5).getText());
  	
    //10th test: "ww |ww"
  	paragraph = new FormattedTextParagraph();
  	paragraph.addElement(new FormattedTextString("ww ww", style));
  	width = 2.1f * widthW;
  	lines = paragraph.lineBreak(width);
  	assertEquals(2, lines.size());
  	assertEquals("ww ", lines.get(0).getText());
  	assertEquals("ww", lines.get(1).getText());
  	
  }
	

}
