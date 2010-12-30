package com.xenoage.zong.renderer;

import java.awt.Color;

import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Point2i;
import com.xenoage.util.math.Rectangle2i;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.data.text.FormattedText;


/**
 * Interface for a class into which can be
 * rendered (e.g. an offscreen buffer,
 * a printer document, and so on).
 *
 * @author Andreas Wenger
 */
public interface RenderTarget
{
  
  
  /**
   * Draws a line.
   * @param p1              starting point of the line
   * @param p2              ending point of the line
   * @param color           color of the line
   * @param lineWidth       width of the line in px
   */
  public void drawLine(Point2i p1, Point2i p2, 
    Color color, int lineWidth);
  
  
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
    Color color, int lineWidth, int interlineSpace);
  
  
  /**
   * Draws a simplified staff (only a filled rectangle).
   * @param pos               position of the upper left corner in px
   * @param length            length of the staff in px
   * @param height            height of the staff in px
   * @param color             color of the lines
   */
  public void drawSimplifiedStaff(Point2i pos, int length, int height,
    Color color);
  
  
  /**
   * Draws the given AttributedString to the given position.
   * The y-position is the baseline of the first paragraph.
   * TODO: review parameters
   */
  public void drawText(FormattedText text, Point2i position, boolean yIsBaseline, float width, float viewScaling);

  
  /**
   * Draws the given Symbol using the given color to the given
   * position with the given scaling.
   */
  public void drawSymbol(Symbol symbol, Color color,
    Point2f position, Point2f scaling);
  
  
  /**
   * Draws a beam using the given quad and color.
   * @param points  the four points in clockwise order,
   *                beginning with the lower left point
   * @param color   the color of the beam
   * @param interlineSpace  the interline space in px
   */
  public void drawBeam(Point2f[] points, Color color, float interlineSpace);
  
  
  /**
   * Draws a tie/slur using the given Bézier curve.
   * @param p1      the starting point
   * @param p2      the ending point
   * @param c1      the first control point
   * @param c2      the second control point
   * @param interlineSpace  the interline space in px
   * @param color   the color of the slur
   */
  public void drawCurvedLine(Point2f p1, Point2f p2, Point2f c1, Point2f c2, float interlineSpace, Color color);
  
  
  /**
   * Fills a rectangle.
   * @param rect    the rectangle
   * @param color   the color of the rectangle
   */
  public void fillRect(Rectangle2i rect, Color color);
  
  
}
