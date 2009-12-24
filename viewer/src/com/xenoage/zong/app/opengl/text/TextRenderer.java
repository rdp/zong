package com.xenoage.zong.app.opengl.text;

import java.awt.Color;
import java.awt.Font;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;

import com.xenoage.util.math.Point2f;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextElement;
import com.xenoage.zong.data.text.FormattedTextParagraph;
import com.xenoage.zong.data.text.FormattedTextString;
import com.xenoage.zong.data.text.FormattedTextStyle;
import com.xenoage.zong.data.text.FormattedTextSymbol;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.RenderingQuality;


/**
 * This class provides methods to draw strings in OpenGL,
 * including a method to draw a {@link FormattedText}.
 * 
 * It is based on JOGL's {@link com.sun.opengl.util.j2d.TextRenderer}
 * class.
 * 
 * It can be used only within a single GL context.
 * 
 * @author Andreas Wenger
 */
public class TextRenderer
{
	
	private TextRendererManager manager;
	
	
	public TextRenderer()
	{
		this.manager = new TextRendererManager();
	}
	
	
	/**
	 * Draws a {@link FormattedText} on the given OpenGL context.
	 * @param text         the paragraph to draw
	 * @param x            the leftmost position of the paragraphs
	 * @param y            the vertical position of the baseline plus the ascent of
	 *                     the first paragraph (a.k.a. the topmost position of the bounding box)
	 * @param yIsBaseline  if true, y is the baseline coordinate, if false, it is like described above
	 * @param width        the width of the bounding box (needed only for non-left-aligned paragraphs)
	 * @param scaling      scaling factor. to get 100% zoom on screen, use <code>Unit.mmToPx(1, 1)</code>
	 * @param context      the current GLGraphicsContext
	 */
	public void drawFormattedText(FormattedText text, float x, float y, boolean yIsBaseline, float width,
		float scaling, GLGraphicsContext context)
	{
		//apply scaling
		GL gl = GLContext.getCurrent().getGL();
		gl.glPushMatrix();
    gl.glScalef(scaling, -1 * scaling, 1f); //-1: OpenGL-TextRenderer works vertically mirrored
    y = -y;
    //draw text
		for (FormattedTextParagraph paragraph : text.getParagraphs())
		{
			if (!yIsBaseline)
				y -= paragraph.getAscent() * scaling; //- instead of + because JOGL TextRenderer works mirrored
			drawFormattedTextParagraph(paragraph, x / scaling, y / scaling, width, context);
			y -= paragraph.getDescent() * scaling;
			if (yIsBaseline)
				y -= paragraph.getLeading() * scaling;
		}
		//reset scaling
		gl.glPopMatrix();
	}
	
	
	/**
	 * Draws a {@link FormattedTextParagraph} on the current OpenGL context.
	 * @param paragraph  the text line to draw
	 * @param x          the leftmost position of the paragraph
	 * @param y          the vertical position of the baseline of the paragraph
	 * @param width      the width of the bounding box (needed only for non-left-aligned paragraphs)
	 * @param context    the current GLGraphicsContext
	 */
	private void drawFormattedTextParagraph(FormattedTextParagraph paragraph, float x, float y,
		float width, GLGraphicsContext context)
	{
		//offset for non-left-aligned paragraph
		switch (paragraph.getAlignment())
		{
			case Center: x += (width - paragraph.getWidthMm()) / 2; break;
			case Right: x += width - paragraph.getWidthMm(); break;
		}
		for (FormattedTextElement element : paragraph.getElements())
		{
			drawFormattedTextElement(element, x, y, context);
			x += element.getWidth();
		}
	}
	
	
	/**
	 * Draws a {@link FormattedTextElement} on the current OpenGL context.
	 * @param element  the element to draw
	 * @param x        the leftmost position of the element
	 * @param y        the vertical position of the baseline of the element
	 * @param context  the current GLGraphicsContext
	 */
	private void drawFormattedTextElement(FormattedTextElement element, float x, float y,
		GLGraphicsContext context)
	{
		if (element instanceof FormattedTextString)
		{
			drawFormattedTextString((FormattedTextString) element, x, y);
		}
		else if (element instanceof FormattedTextSymbol)
		{
			drawFormattedTextSymbol((FormattedTextSymbol) element, x, y, context);
		}
	}
	
	
	/**
	 * Draws a {@link FormattedTextString} on the current OpenGL context.
	 * @param string   the text element to draw
	 * @param x        the leftmost position of the element
	 * @param y        the vertical position of the baseline of the element
	 */
	private void drawFormattedTextString(FormattedTextString string, float x, float y)
	{
		//get renderer for the element's font
		FormattedTextStyle style = string.getStyle();
		Font font = style.getFont();
		com.sun.opengl.util.j2d.TextRenderer renderer = manager.getRenderer(font);
		float scaleFactor = manager.getScaleFactor(font.getSize2D());
		//begin rendering
		renderer.begin3DRendering();
		//color
		if (style != null && style.getColor() != null)
		{
			renderer.setColor(style.getColor());
		}
		else //TEST
		{
			renderer.setColor(Color.BLACK);
		}
		renderer.draw3D(string.getText(), x, y, 0, scaleFactor);
		//end rendering
		renderer.end3DRendering();
	}
	
	
	/**
	 * Draws a {@link FormattedTextSymbol} on the current OpenGL context.
	 * @param symbol   the musical symbol to draw
	 * @param x        the leftmost position of the element
	 * @param y        the vertical position of the baseline of the element
	 * @param context  the current GLGraphicsContext
	 */
	private void drawFormattedTextSymbol(FormattedTextSymbol symbolElement, float x, float y,
		GLGraphicsContext context)
	{
		Symbol symbol = symbolElement.getSymbol();
		float scaling = symbolElement.getScaling();
		symbol.draw(context, Color.black, new Point2f(x + symbolElement.getOffsetX(),
			y - symbol.getBaselineOffset() * scaling),
			new Point2f(scaling, -1 * scaling));
	}
	

}
