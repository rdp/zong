package com.xenoage.zong.renderer.frames;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.xenoage.util.math.Point2i;
import com.xenoage.zong.gui.components.TextEditor;
import com.xenoage.zong.layout.frames.Frame;
import com.xenoage.zong.layout.frames.TextFrame;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.zong.renderer.printer.PrinterRenderTarget;
import com.xenoage.util.Units;


/**
 * Swing renderer for a text frame.
 * 
 * Notice that this renderer is not optimized
 * for performance in any way. It should
 * only be used for printing.
 * 
 * @author Andreas Wenger
 */
public class SwingTextFrameRenderer
  extends SwingFrameRenderer
{
  
  private static SwingTextFrameRenderer instance = null;
  
  
  public static SwingTextFrameRenderer getInstance()
  {
    if (instance == null)
      instance = new SwingTextFrameRenderer();
    return instance;
  }
  
  
  private SwingTextFrameRenderer()
  {
  }
  
  
  /**
   * Paints the given {@link TextFrame} with the given Swing context.
   * This method is called by the paint-method, the transformations
   * are alread done.
   */
  @Override protected void paintTransformed(Frame frame,
    SwingGraphicsContext context)
  {
  	
  	TextFrame textFrame = (TextFrame) frame;
  	Graphics2D g2d = context.getGraphics2D();
  	
  	float w = frame.getSize().width;
  	float h = frame.getSize().height;
  	g2d.translate(-w/2, -h/2);
  	
  	PrinterRenderTarget.getInstance(context).drawText(textFrame.getTextWithLineBreaks(), new Point2i(0, 0), false, w, 1);
  	
  	/* //TEST
  	g2d.setColor(Color.green);
  	g2d.setStroke(new BasicStroke(1));
  	g2d.drawRect(0, 0, (int) frame.getSize().width, (int) frame.getSize().height); //*/

  }

  
  /**
   * Creates and returns a {@link TextEditor} for the
   * given {@link TextFrame}.
   */
  public static TextEditor createTextEditor(TextFrame textFrame)
  {
    TextEditor editor = new TextEditor(
      Units.mmToPx(textFrame.getSize().width, 1),
      Units.mmToPx(textFrame.getSize().height, 1), 1);
    editor.setBackground(new Color(1, 1, 1, 0));
    editor.importFormattedText(textFrame.getText());
    editor.setSize(editor.getPreferredSize()); 
    return editor;
  }

}
