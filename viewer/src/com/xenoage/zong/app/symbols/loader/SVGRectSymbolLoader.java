package com.xenoage.zong.app.symbols.loader;

import com.xenoage.util.xml.XMLReader;
import com.xenoage.zong.app.symbols.RectSymbol;

import java.awt.geom.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * A SVGRectSymbolLoader creates a RectSymbol
 * from a given SVG document.
 *
 * @author Andreas Wenger
 */
class SVGRectSymbolLoader
{

  
  /**
   * Creates a RectSymbol frsom the given SVG document.
   * If an error occurs, null is returned.
   */
  public static RectSymbol loadRectSymbol(String id, Document doc)
  {
    
    //search for a path
    Element root = XMLReader.root(doc);
    Element eRect = XMLReader.element(root, "rect");
    
    //if not found, search for a group, and a path in this group
    if (eRect == null)
    {
      Element eGroup = XMLReader.element(root, "g");
      if (eGroup != null)
      {
        eRect = XMLReader.element(eGroup, "rect");
      }
    }
    
    //if the rect was found, parse it and create a RectSymbol,
    //otherwise return null.
    if (eRect != null)
    {
      
      //read coordinates
      float x = Float.parseFloat(XMLReader.attribute(eRect, "x"));
      float y = Float.parseFloat(XMLReader.attribute(eRect, "y"));
      float width = Float.parseFloat(XMLReader.attribute(eRect, "width"));
      float height = Float.parseFloat(XMLReader.attribute(eRect, "height"));
      
      //transform path by -1000/-1000 and scale down to 1%
      x -= 1000;
      y -= 1000;
      x *= 0.01f;
      y *= 0.01f;
      width *= 0.01f;
      height *= 0.01f;
      
      Rectangle2D rect = new Rectangle2D.Float(x, y, width, height);
      
      RectSymbol ret = new RectSymbol(id, rect);
      return ret;
    }
    else
    {
    	throw new IllegalArgumentException("No rect element was found!");
    }
  }
  
}
