package com.xenoage.zong.layout.frames;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.renderer.GLGraphicsContext;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.zong.renderer.frames.GLTextFrameRenderer;
import com.xenoage.zong.renderer.frames.SwingTextFrameRenderer;


/**
 * A text frame is a frame that contains a
 * formatted text.
 * 
 * It may use multiple fonts, colors and
 * styles and may contain several paragraphs.
 * 
 * @author Andreas Wenger
 */
public class TextFrame
	extends Frame
{
	
	private FormattedText text;
	
	
	/**
	 * Line breaks: Manual means, that if there is no line break in the formatted text,
	 * the text will continue beyond the borders of the frame. Automatic means, that
	 * pragraphs are automatically broken into lines fitting into the borders of the frame.
	 */
	public enum LineBreakStyle { Manual, Automatic };
	private LineBreakStyle lineBreakStyle = LineBreakStyle.Manual;
	private FormattedText cacheTextLineBreak = null;


	/**
	 * Creates a new TextFrame.
	 * @param text      the text content of this frame
	 * @param position  center position of the frame in mm.
   * @param size      size of the frame in mm.
	 */
	public TextFrame(FormattedText text, Point2f position, Size2f size)
	{
		super(position, size);
		this.text = text;
	}
	
	
	/**
   * Gets the text content of this frame.
   */
  public FormattedText getText()
  {
  	return text;
  }
  
  
  /**
   * Sets the text content of this frame.
   */
  public void setText(FormattedText text)
  {
  	this.text = text;
  	updateCache();
  }
  
  
  /**
   * Sets the size of the frame in mm.
   */
  @Override public void setSize(Size2f size)
  {
    this.size = size;
    updateCache();
  }
  
  
  /**
   * Gets the text content of this frame with automatic line breaks,
   * if enabled, otherwise the original text.
   */
  public FormattedText getTextWithLineBreaks()
  {
  	if (lineBreakStyle == LineBreakStyle.Automatic)
  	{
  		return cacheTextLineBreak;
  	}
  	else
  	{
  		return text;
  	}
  }
  
  
  /**
   * Gets the type of line breaks.
   */
  public LineBreakStyle getLineBreakStyle()
	{
		return lineBreakStyle;
	}


  /**
   * Sets the type of line breaks.
   */
	public void setLineBreakStyle(LineBreakStyle lineBreakStyle)
	{
		this.lineBreakStyle = lineBreakStyle;
		updateCache();
	}
  

	/**
   * Paints this frame with the given OpenGL context.
   */
  @Override public void paint(GLGraphicsContext context)
  {
    //paint this frame
    GLTextFrameRenderer.getInstance().paint(this, context);
  }
  
  
  /**
   * Paints this frame with the given Swing context.
   */
  @Override public void paint(SwingGraphicsContext context)
  {
    //paint this frame
    SwingTextFrameRenderer.getInstance().paint(this, context);
  }
  
  
  /**
   * Updates the cache:
   * Text with line breaks for text frame with automatic line break.
   */
  private void updateCache()
  {
  	if (lineBreakStyle == LineBreakStyle.Automatic)
  	{
  		cacheTextLineBreak = text.lineBreak(size.width);
  	}
  	else
  	{
  		cacheTextLineBreak = null;
  	}
  }
	
	
}
