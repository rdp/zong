package com.xenoage.zong.app.symbols;

import com.xenoage.util.Parser;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.util.math.*;
import com.xenoage.util.xml.XMLReader;
import com.xenoage.util.xml.XMLWriter;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.app.opengl.TextureManager;
import com.xenoage.zong.app.symbols.rasterizer.SymbolsAdjuster;
import com.xenoage.zong.app.symbols.rasterizer.SymbolsRasterizer;
import com.xenoage.zong.util.ArrayTools;
import com.xenoage.util.io.IO;
import com.xenoage.util.logging.Log;

import java.io.*;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This class manages information about
 * all texture versions of the symbols.
 *
 * @author Andreas Wenger
 */
public class SymbolTexturePool
{
  
  private int texturePoolID = 0;
  private int texturePoolsCount = 0;
  
  private Hashtable<String, SymbolTexture> textures;
  
  
  /**
   * Creates a new pool for symbol textures.
   * The data is read from data/symbols/{id}-tex.xml.
   * If it does not exist, the pool will be empty.
   * @param id  the ID of the style, e.g. "default"
   */
  public SymbolTexturePool(String id)
  {
    texturePoolID = texturePoolsCount;
    texturePoolsCount++;
    
    textures = new Hashtable<String, SymbolTexture>();
    InputStream in = null;
    try
    {
      in = IO.openInputStream(getTextureXMLPath(id));
    }
    catch (Exception ex)
    {
    }
    if (in != null)
    {
      try
      {
        Document document = XMLReader.readFile(in);
        Element eSymbols = XMLReader.root(document);
        List<Element> eTextures = XMLReader.elements(eSymbols, "texture");
        textures = new Hashtable<String, SymbolTexture>(eTextures.size());
        for (Element eTexture : eTextures)
        {
          String symbolID = XMLReader.attribute(eTexture, "id");
          float x1 = Parser.parseFloat(XMLReader.attribute(eTexture, "x1"));
          float y1 = Parser.parseFloat(XMLReader.attribute(eTexture, "y1"));
          float x2 = Parser.parseFloat(XMLReader.attribute(eTexture, "x2"));
          float y2 = Parser.parseFloat(XMLReader.attribute(eTexture, "y2"));
          if (symbolID != null)
          {
            textures.put(symbolID, new SymbolTexture(
              getTextureID(), new TextureRectangle2f(x1, y1, x2, y2)));
          }
        }
      }
      catch (Exception ex)
      {
        Log.log(Log.ERROR, this, ex);
      }
    }
  }
  
  
  /**
   * Gets the texture for the symbol with the given ID,
   * or null if there is none.
   */
  public SymbolTexture getTexture(String symbolID)
  {
    return textures.get(symbolID);
  }
  
  
  /**
   * Gets the path of the texture XML file for the given ID.
   */
  public static String getTextureXMLPath(String id)
  {
    return "data/symbols/" + id + "-tex.xml";
  }
  
  
  /**
   * Gets the path of the texture PNG file for the given ID
   * with the given width.
   */
  public static String getTexturePNGPath(String id, int mipmapLevel)
  {
    return "data/symbols/" + id + "-tex-" + mipmapLevel + ".png";
  }
  
  
  /**
   * Creates the texture (XML and PNG) for given symbols
   * with the given id.
   */
  public static void createSymbolTextures(
    String id, Hashtable<String, Symbol> symbols, int textureSize,
    boolean createFilesInSystemDir)
  {
    
    //collect the symbols
    ArrayList<Symbol> symbolsList = new ArrayList<Symbol>(symbols.size());
    for (Symbol symbol : symbols.values())
    {
      //use only path symbols
      if (symbol instanceof PathSymbol)
        symbolsList.add(symbol);
    }
    Symbol[] symbolsArray = ArrayTools.toSymbolArray(symbolsList);
    
    //compute the texture coordinates
    int width = textureSize;
    Size2f[] symbolsSizes = new Size2f[symbolsArray.length];
    for (int i = 0; i < symbolsSizes.length; i++)
    {
      symbolsSizes[i] = new Size2f(symbolsArray[i].getBoundingRect().size);
    }
    SymbolsAdjuster adjuster = new SymbolsAdjuster(width, width, symbolsSizes, 10); //TODO
    TextureRectangle2f[] texCords = adjuster.getAllTextureRectangles2f();
    
    //create the PNG file
    //TODO: rasterizeSymbols() should use the util.io.IO class
    try
    {
      int level = 0;
      while (width >= 1)
      {
      	OutputStream os;
      	if (createFilesInSystemDir)
      	{
      		os = new FileOutputStream(getTexturePNGPath(id, level));
      	}
      	else
      	{
      		os = IO.openOutputStream(getTexturePNGPath(id, level));
      	}
        SymbolsRasterizer.rasterizeSymbols(os, width, width, symbolsArray, texCords);
        width /= 2;
        level++;
      }
    }
    catch (IOException ex)
    {
      App.err().report(ErrorLevel.Fatal, Voc.Error_CouldNotCreateTexture, ex);
    }
    
    //create the XML file
    try
    {
      Document document = XMLWriter.createEmptyDocument();
      Element eSymbols = XMLWriter.addElement("symbols", document);
      for (int i = 0; i < symbolsArray.length; i++)
      {
        Element eTexture = XMLWriter.addElement("texture", eSymbols);
        XMLWriter.addAttribute(eTexture, "id", symbolsArray[i].getID());
        XMLWriter.addAttribute(eTexture, "x1", Float.toString(texCords[i].x1));
        XMLWriter.addAttribute(eTexture, "y1", Float.toString(1 - texCords[i].y2));
        XMLWriter.addAttribute(eTexture, "x2", Float.toString(texCords[i].x2));
        XMLWriter.addAttribute(eTexture, "y2", Float.toString(1 - texCords[i].y1));
      }
      OutputStream os;
    	if (createFilesInSystemDir)
    	{
    		os = new FileOutputStream(getTextureXMLPath(id));
    	}
    	else
    	{
    		os = IO.openOutputStream(getTextureXMLPath(id));
    	}
      XMLWriter.writeFile(document, os);
    }
    catch (Exception ex)
    {
    	App.err().report(ErrorLevel.Fatal, Voc.Error_CouldNotCreateTexture, ex);
    }
    
  }
  
  
  /**
   * Gets the ID of the texture of this symbol pool.
   */
  public int getTextureID()
  {
    return TextureManager.IDBASE_SYMBOLS + texturePoolID;
  }
  

}
