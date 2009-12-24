package com.xenoage.zong.app.symbols.loader;

import com.xenoage.util.Parser;
import com.xenoage.util.svg.SVGPathParser;
import com.xenoage.util.xml.XMLReader;
import com.xenoage.zong.app.symbols.PathSymbol;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * A SVGPathSymbolLoader creates a PathSymbol
 * from a given SVG document.
 *
 * @author Andreas Wenger
 */
class SVGPathSymbolLoader
{

  
  /**
   * Creates a PathSymbol from the given SVG document.
   * If an error occurs, an IllegalArgumentException is thrown.
   */
  public static PathSymbol loadPathSymbol(String id, Document doc)
  {
  	Element root = XMLReader.root(doc);
  	
  	//read baseline and ascent, if there
  	Float baseline = null;
  	Float ascent = null;
  	String attr = XMLReader.attribute(root, "score:baseline");
  	if (attr != null)
  		baseline = Parser.parseFloat(attr) * 0.01f - 10;
  	attr = XMLReader.attribute(root, "score:ascent");
  	if (attr != null)
  		ascent = Parser.parseFloat(attr) * 0.01f;
  	
  	//custom left and right border, if there
  	Float leftBorder = null;
  	Float rightBorder = null;
  	attr = XMLReader.attribute(root, "score:leftborder");
  	if (attr != null)
  		leftBorder = Parser.parseFloat(attr) * 0.01f - 10;
  	attr = XMLReader.attribute(root, "score:rightborder");
  	if (attr != null)
  		rightBorder = Parser.parseFloat(attr) * 0.01f - 10;
    
    //search for a path
    Element ePath = XMLReader.element(root, "path");
    
    //if not found, search for a group, and a path in this group
    if (ePath == null)
    {
      Element eGroup = XMLReader.element(root, "g");
      if (eGroup != null)
      {
        ePath = XMLReader.element(eGroup, "path");
      }
      //if still not found, search for another group
      if (ePath == null)
      {
        eGroup = XMLReader.element(eGroup, "g");
        if (eGroup != null)
        {
          ePath = XMLReader.element(eGroup, "path");
        }
      }
    }
    
    //if the path was found, parse it and create a PathSymbol,
    //otherwise return null.
    if (ePath != null && XMLReader.attribute(ePath, "d") != null)
    {
      SVGPathParser pathParser = new SVGPathParser();
      GeneralPath path = pathParser.createGeneralPath(
        XMLReader.attribute(ePath, "d"));
      
      //transform path by -1000/-1000 and scale down to 1%
      AffineTransform transform = new AffineTransform();
      transform.translate(-10, -10);
      transform.scale(0.01d, 0.01d);
      path.transform(transform);
      
      //PathSymbol uses vertically mirrored coordinates
      PathSymbol ret = new PathSymbol(id, path, baseline, ascent, leftBorder, rightBorder);
      return ret;
    }
    else
    {
      throw new IllegalArgumentException("No path element was found!");
    }
  }
  
}
