package com.xenoage.util.svg;

import com.xenoage.util.math.Point2f;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;


/**
 * This class creates a GeneralPath from
 * a given SVG path.
 *
 * @author Andreas Wenger
 */
public class SVGPathParser
{
  
  private String d;
  private int pos;
  
  
  /**
   * Creates and returns a GeneralPath from the
   * given d attribute value of a SVG path element.
   */
  public GeneralPath createGeneralPath(String d)
    throws IllegalArgumentException
  {
    this.d = d;
    pos = 0;
    GeneralPath ret = new GeneralPath();
    
    //parse commands
    String token = getNextToken();
    Point2f p, p1, p2, p3;
    float x, y;
    while (token != null)
    {
      Point2D currentPoint = ret.getCurrentPoint();
      char tokenChar = token.charAt(0);

      switch (tokenChar)
      {
	      //MoveTo (absolute)
      	case 'M':
	        p = readPoint();
	        ret.moveTo(p.x, p.y);
	        break;
	      //MoveTo (relative)
      	case 'm':
	        p = readPoint();
	        ret.moveTo((float) currentPoint.getX() + p.x, (float) currentPoint.getY() + p.y);
	        break;
	      //ClosePath
      	case 'Z': case 'z':
	        ret.closePath();
	        break;
	      //LineTo (absolute)
      	case 'L':
	        p = readPoint();
	        ret.lineTo(p.x, p.y);
	        break;
	      //LineTo (relative)
      	case 'l':
      		p = readPoint();
	        ret.lineTo((float) currentPoint.getX() + p.x, (float) currentPoint.getY() + p.y);
	        break;
	      //Horizontal LineTo (absolute)
      	case 'H':
	        x = parseNumericToken(getNextToken());
	        ret.lineTo(x, (float) ret.getCurrentPoint().getY());
	        break;
	      //Horizontal LineTo (relative)
      	case 'h':
	        x = parseNumericToken(getNextToken());
	        ret.lineTo(x, (float) currentPoint.getY() + (float) ret.getCurrentPoint().getY());
	        break;
	      //Vertical LineTo (absolute)
      	case 'V':
	        y = parseNumericToken(getNextToken());
	        ret.lineTo((float) ret.getCurrentPoint().getX(), y);
	        break;
	      //Vertical LineTo (relative)
      	case 'v':
	        y = parseNumericToken(getNextToken());
	        ret.lineTo((float) currentPoint.getX() + (float) ret.getCurrentPoint().getX(), y);
	        break;
	      //CurveTo (absolute)
      	case 'C':
	        p1 = readPoint();
	        p2 = readPoint();
	        p3 = readPoint();
	        ret.curveTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
	        break;
	      //CurveTo (relative)
      	case 'c':
	        p1 = readPoint();
	        p2 = readPoint();
	        p3 = readPoint();
	        ret.curveTo((float) currentPoint.getX() + p1.x, (float) currentPoint.getY() + p1.y,
	          (float) currentPoint.getX() + p2.x, (float) currentPoint.getY() + p2.y,
	          (float) currentPoint.getX() + p3.x, (float) currentPoint.getY() + p3.y);
	        break;
	      //Quadratic CurveTo (absolute)
      	case 'Q':
	        p1 = readPoint();
	        p2 = readPoint();
	        ret.quadTo(p1.x, p1.y, p2.x, p2.y);
	        break;
	      //Quadratic CurveTo (relative)
      	case 'q':
	        p1 = readPoint();
	        p2 = readPoint();
	        ret.quadTo((float) currentPoint.getX() + p1.x, (float) currentPoint.getY() + p1.y,
	          (float) currentPoint.getX() + p2.x, (float) currentPoint.getY() + p2.y);
	        break;
	      //not implemented commands
      	case 'T': case 't': case 'S': case 's': case 'A': case 'a':
	        throw new IllegalArgumentException("Command \"" + token +
	          "\" not implemented yet.");
	      //unknown command
	      default:
	        throw new IllegalArgumentException("Unknown command: \"" + token + "\"");
      }
      
      token = getNextToken();
    }
    
    return ret;
  }
  
  
  /**
   * Gets the next token of the d-String, starting at pos.
   * Returns null, when there is no token any more.
   */
  private String getNextToken()
  {
    
    //skip " " and "," and "\n" and "\r".
    while (pos < d.length() && isWhitespace(d.charAt(pos)))
    {
      pos++;
    }
    
    //when the end of the String is reached, return null
    if (pos >= d.length())
      return null;
    
    //find the end of the token
    char c0 = d.charAt(pos);
    boolean c0Numeric = isNumeric(c0);
    int posEnd = pos;
    for (int i = pos + 1; i < d.length(); i++)
    {
      char ci = d.charAt(i);
      boolean ciNumeric = isNumeric(ci);
      
      //if c0 is numeric, but c1 not (or the other way round), the token is finished
      if (c0Numeric != ciNumeric)
      {
        break;
      }
      
      //if ci is whitespace, the token is finished
      if (isWhitespace(ci))
      {
        break;
      }
      
      posEnd++;
    }
    String ret = d.substring(pos, posEnd + 1);
    
    //new starting point is current end point
    pos = posEnd + 1;
    
    return ret;
    
  }
  
  
  /**
   * Returns true, if the given char is a digit, a dot,
   * a plus or a minus.
   */
  private boolean isNumeric(char c)
  {
    return (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' ||
      c == '5' || c == '6' || c == '7' || c == '8' || c == '9' ||
      c == '.' || c == '-' || c == '+');
  }
  
  
  /**
   * Returns true, if the given char is a whitespace
   * (' ', ',', '\n', '\r').
   */
  private boolean isWhitespace(char c)
  {
    return (c == ' ' || c == ',' || c == '\n' || c == '\r');
  }
  
  
  /**
   * Parse a numeric token.
   */
  private float parseNumericToken(String token)
    throws NumberFormatException
  {
    return Float.parseFloat(token);
  }
  
  
  /**
   * Reads the next two tokens and interprets them as a point.
   */
  private Point2f readPoint()
    throws NumberFormatException
  {
    String x = getNextToken();
    String y = getNextToken();
    return new Point2f(parseNumericToken(x), parseNumericToken(y));
  }

}
