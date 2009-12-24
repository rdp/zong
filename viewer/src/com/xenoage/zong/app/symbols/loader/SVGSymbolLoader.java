package com.xenoage.zong.app.symbols.loader;

import com.xenoage.util.FileTools;
import com.xenoage.util.xml.XMLReader;
import com.xenoage.zong.app.symbols.*;
import com.xenoage.util.io.IO;
import com.xenoage.util.logging.Log;

import java.io.File;

import org.w3c.dom.Document;


/**
 * A SVGSymbolLoader creates symbols
 * from SVG files.
 *
 * @author Andreas Wenger
 */
public class SVGSymbolLoader
{
  
  /**
   * Creates a PathSymbol from the given SVG file.
   * If an error occurs, an IllegalArgumentException is thrown.
   */
  public Symbol loadSymbol(String svgFilepath)
  {
    
    File svgFile = new File(svgFilepath);
    String id = FileTools.getNameWithoutExt(svgFile);
    Log.log(Log.MESSAGE, "Loading symbol \"" + id + "\", file: \"" +
      svgFilepath + "\" ...");
    
    //open the file
    Document doc;
    try
    {
      doc = XMLReader.readFile(IO.openDataFile(svgFilepath));
    }
    catch (Exception ex)
    {
    	throw new IllegalArgumentException("Could not read XML file \"" + svgFilepath + "\"");
    }
    
    //read id element. it has the format "type:id", e.g.
    //"path:clef-g", or "styled:warning". If there is no ":",
    //the type "path" is used.
    //styles: path, styled, rect
    Symbol ret;
    String elementID = XMLReader.attribute(XMLReader.root(doc), "id");
    if (elementID == null || elementID.indexOf(':') == -1)
    {
      //no format information. use path.
      ret = SVGPathSymbolLoader.loadPathSymbol(id, doc);
    }
    else
    {
      String format = elementID.split(":")[0];
      if (format.equals("path"))
      {
        ret = SVGPathSymbolLoader.loadPathSymbol(id, doc);
      }
      else if (format.equals("rect"))
      {
        ret = SVGRectSymbolLoader.loadRectSymbol(id, doc);
      }
      else if (format.equals("styled"))
      {
      	throw new IllegalArgumentException("Could not load \"" + svgFilepath + "\": \"" + format + "\" (currently styled symbols are not supported)");
      }
      else
      {
      	throw new IllegalArgumentException("Unknown symbol format in \"" + svgFilepath + "\": \"" + format + "\"");
      }
    }
    
    return ret;
    
  }

}
