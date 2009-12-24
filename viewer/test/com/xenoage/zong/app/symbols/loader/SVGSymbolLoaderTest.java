package com.xenoage.zong.app.symbols.loader;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Test cases for a SVGSymbolLoader
 *
 * @author Andreas Wenger
 */
public class SVGSymbolLoaderTest
{

  @Test public void loadPathSymbol()
  {
    //load element "clef-g" from the default symbol set.
    SVGSymbolLoader sl = new SVGSymbolLoader();
    assertNotNull(sl.loadSymbol(
      "data/symbols/default/clef-g.svg"));
  }
  
}

