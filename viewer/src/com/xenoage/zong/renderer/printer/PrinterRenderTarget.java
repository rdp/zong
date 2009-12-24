package com.xenoage.zong.renderer.printer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.slurs.DefaultSlurShape;
import com.xenoage.zong.app.symbols.slurs.SlurShape;
import com.xenoage.zong.data.text.Alignment;
import com.xenoage.zong.data.text.FormattedText;
import com.xenoage.zong.data.text.FormattedTextElement;
import com.xenoage.zong.data.text.FormattedTextParagraph;
import com.xenoage.zong.data.text.FormattedTextString;
import com.xenoage.zong.data.text.FormattedTextSymbol;
import com.xenoage.zong.renderer.RenderTarget;
import com.xenoage.zong.renderer.SwingGraphicsContext;
import com.xenoage.zong.util.text.TextLayoutTools;


/**
 * This class contains methods for painting
 * on a printer document.
 *
 * @author Andreas Wenger
 */
public class PrinterRenderTarget
  implements RenderTarget
{
  
  private static PrinterRenderTarget instance = null;
  
  private SwingGraphicsContext currentSwingContext;
  
  
  /**
   * Gets the printer renderer.
   */
  public static PrinterRenderTarget getInstance(
    SwingGraphicsContext currentSwingContext)
  {
    if (instance == null)
    {
      instance = new PrinterRenderTarget();
    }
    instance.currentSwingContext = currentSwingContext;
    return instance;
  }
  
  
  /**
   * Creates a PrinterRenderTarget.
   */
  private PrinterRenderTarget()
  {
  }
  
  
  /**
   * Draws the given {@link FormattedText} to the given position.
   * The y-position is the baseline of the first paragraph.
   * 
   * TODO: UGLY: remove the factor "factor = 0.3453f", find the reason for that mess
   */
  public void drawText(FormattedText text, Point2i position, boolean yIsBaseline, float frameWidth, float viewScaling)
  {
  	Graphics2D g2d = currentSwingContext.getGraphics2D();
  	AffineTransform oldTransform = g2d.getTransform();
  	g2d.translate(position.x, position.y);
  	g2d.scale(viewScaling, viewScaling);
  	
  	//TODO: ask Martin Pabst about right font scaling
  	//this factor was determined by experiments on Andis PC (Ubuntu 8.04)
  	//g2d.scale(72f/96f, 72f/96f);
  	//g2d.scale(0.1f, 0.1f);
  	float factor = 0.3453f;
  	if (!yIsBaseline /* false for text frames */) //TODO: UGLY UGLY UGLY - why do we need this factor only for TextFrames ?!?
  		g2d.scale(factor, factor);
    g2d.setColor(Color.black); //TODO
    
    //g2d.fillRect(-1, -1, -2, -2);
  	
    //print the text frame paragraph for paragraph
  	float offsetX = 0;
  	float offsetY = 0;
  	int line = 0;
  	float wInch = frameWidth / factor;
  	FontRenderContext frc = g2d.getFontRenderContext();
  	
  	for (FormattedTextParagraph p : text.getParagraphs())
  	{
  		if (!yIsBaseline)
  			offsetY += p.getAscent() * (1 / factor);
	    
  	  //adjustment
  	  if (p.getAlignment() == Alignment.Center)
  	  	offsetX = (wInch - p.getWidthMm() / factor) / 2;
  	  else if (p.getAlignment() == Alignment.Right)
  	  	offsetX = wInch - p.getWidthMm() / factor;
  	  else
  	  	offsetX = 0;
  	  
  	  //draw elements
  	  for (FormattedTextElement e : p.getElements())
  	  {
  	  	if (e instanceof FormattedTextString)
  	  	{
  	  		//string
  	  		TextLayout tl = TextLayoutTools.create((FormattedTextString) e, frc);
  	  		tl.draw(g2d, offsetX, offsetY);  
  	  	}
  	  	else
  	  	{
  	  		//symbol
  	  		FormattedTextSymbol fts = (FormattedTextSymbol) e;
  	  		float scaling = fts.getScaling() / factor;
  	  		fts.getSymbol().draw(g2d, Color.black, new Point2f(offsetX + fts.getOffsetX() / factor,
  	  			offsetY + fts.getSymbol().getBaselineOffset() * scaling),
  	  			new Point2f(scaling, scaling));
  	  	}
  	  	offsetX += e.getWidth() * (1 / factor);
  	  }
  	  
	    offsetY += (p.getDescent() + p.getLeading()) * (1 / factor); 
	    line++;
    }
  	
  	g2d.setTransform(oldTransform);
  }
  
  
  /**
   * Draws the given Symbol using the given color to the given
   * position with the given scaling.
   */
  public void drawSymbol(Symbol symbol, Color color,
    Point2f position, Point2f scaling)
  {
    Graphics2D g2d = currentSwingContext.getGraphics2D();
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY); //TEST
    symbol.draw(g2d, color, position, scaling);
  }
  
  
  /**
   * Draws a line.
   * @param p1          starting point of the line
   * @param p2          ending point of the line
   * @param color       color of the line
   * @param lineWidth   width of the line in px
   */
  public void drawLine(Point2i p1, Point2i p2, 
    Color color, int lineWidth)
  {
    Graphics2D g2d = currentSwingContext.getGraphics2D();
    g2d.setColor(color);
    g2d.setStroke(new BasicStroke(lineWidth,
      BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
  }


  /**
   * Draws a staff.
   * @param pos               position of the upper left corner in px
   * @param length            length of the staff in px
   * @param lines             number of lines
   * @param color             color of the lines
   * @param lineWidth         width of the line in px
   * @param interlineSpace    space between lines in px
   */
  public void drawStaff(Point2i pos, int length, int lines,
    Color color, int lineWidth, int interlineSpace)
  {
    Graphics2D g2d = currentSwingContext.getGraphics2D();
    g2d.setColor(color);
    for (int i = 0; i < lines; i++)
    {
      g2d.fillRect(pos.x, pos.y + i * interlineSpace - lineWidth / 2, length, lineWidth);
    }
  }
  
  
  /**
   * Draws a simplified staff (only a filled rectangle).
   * @param pos               position of the upper left corner in px
   * @param length            length of the staff in px
   * @param height            height of the staff in px
   * @param color             color of the lines
   */
  public void drawSimplifiedStaff(Point2i pos, int length, int height,
    Color color)
  {
    Graphics2D g2d = currentSwingContext.getGraphics2D();
    g2d.setColor(color);
    g2d.fillRect(pos.x, pos.y, length, height);
  }
  
  
  /**
   * Draws a filled ellipse.
   * @param pCenter     the center point of the ellipse
   * @param width       the width of the ellipse in px
   * @param height      the height of the ellipse in px
   * @param color       color of the ellipse
   */
  public void fillEllipse(Point2f pCenter, 
    float width, float height, Color color)
  {
    Graphics2D g2d = currentSwingContext.getGraphics2D();
    g2d.setColor(color);
    //use float coordinates to allow Java2D to optimize quality
    g2d.fill(new Ellipse2D.Float(
      pCenter.x - width / 2,
      pCenter.y - height / 2,
      width, height));
  }


  /**
   * Draws a beam using the given quad and color.
   * @param points  the four points in clockwise order,
   *                beginning with the lower left point
   * @param color   the color of the beam
   * @param interlineSpace  the interline space in px
   */
  public void drawBeam(Point2f[] points, Color color, float interlineSpace)
  {
    Graphics2D g2d = currentSwingContext.getGraphics2D();
    
    Rectangle2D beamSymbol = new Rectangle2D.Float(-1f, -0.25f, 2f, 0.5f);
    
    g2d.setColor(color);
    
    AffineTransform g2dTransform = g2d.getTransform();
    
    float imageWidth = points[2].x - points[0].x;
    float imageHeight = points[3].y - points[0].y;
    float beamGrowthHeight = points[2].y - points[0].y;
    
    g2d.translate(points[0].x + imageWidth / 2, points[0].y + imageHeight / 2);
    g2d.shear(0, beamGrowthHeight / imageWidth);
    g2d.scale(imageWidth / beamSymbol.getWidth(),
      (points[1].y - points[0].y) / beamSymbol.getHeight());
    //g2d.fillRect(-5000000, -50000, 10000000, 100000);
    g2d.fill(beamSymbol);
    
    g2d.setTransform(g2dTransform);
  }
  
  
  /**
   * Draws a tie/slur using the given Bézier curve.
   * @param p1      the starting point
   * @param p2      the ending point
   * @param c1      the first control point
   * @param c2      the second control point
   * @param interlineSpace  the interline space in px
   * @param color   the color of the slur
   */
  public void drawCurvedLine(Point2f p1, Point2f p2, Point2f c1, Point2f c2, float interlineSpace, Color color)
  {
  	Graphics2D g2d = currentSwingContext.getGraphics2D();
    g2d.setColor(color);
    SlurShape shape = new DefaultSlurShape();
    shape.setSlur(p1, p2, c1, c2, interlineSpace);
    g2d.fill(shape.getShape());
  }
  
  
  /**
   * Fills a rectangle.
   * @param rect    the rectangle
   * @param color   the color of the rectangle
   */
  public void fillRect(Rectangle2i rect, Color color)
  {
    Graphics2D g2d = currentSwingContext.getGraphics2D();
    g2d.setColor(color);
    g2d.fill(new Rectangle2D.Float(
      rect.position.x, rect.position.y, rect.size.width, rect.size.height));
  }
  

}
