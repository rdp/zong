package com.xenoage.util.svg;

import static org.junit.Assert.*;

import org.junit.Test;



/**
 * Test cases for a SVGPathParser.
 *
 * @author Andreas Wenger
 */
public class SVGPathParserTest
{

  @Test public void createGeneralPath()
  {
    SVGPathParser pp = new SVGPathParser();
    String validPath = "M 100 100 L 300 100 L 200 300 z";
    assertNotNull(pp.createGeneralPath(validPath));
    validPath = "M200,300 L400,50 L600,300 L800,550 L1000,300";
    assertNotNull(pp.createGeneralPath(validPath));
  }
  
}
