package com.xenoage.zong.app.symbols;

import com.xenoage.util.FileTools;
import com.xenoage.util.error.ErrorLevel;
import com.xenoage.zong.app.App;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.app.symbols.common.CommonSymbol;
import com.xenoage.zong.app.symbols.common.CommonSymbolPool;
import com.xenoage.zong.app.symbols.loader.SVGSymbolLoader;
import com.xenoage.util.io.IO;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;


/**
 * This class stores and provides
 * all available symbols of a given
 * style.
 * 
 * It also manages the texture versions
 * of the symbols.
 *
 * @author Andreas Wenger
 */
public class SymbolPool
{
 
  private String id;
  private Hashtable<String, Symbol> symbols;
  private SymbolTexturePool texturePool = null;
  
  //special pool for very fast access
  private CommonSymbolPool commonSymbolPool;
  
  private WarningSymbol warningSymbol = new WarningSymbol();
  
  
  /**
   * Gets an empty symbol pool.
   */
  public static SymbolPool empty()
  {
  	return new SymbolPool();
  }
  
  
  /**
   * Loads and returns the symbol pool with the given ID or reports an
   * error if not possible.
   */
  public static SymbolPool load(String id)
  {
  	try
    {
      return new SymbolPool(id, false);
    }
    catch (Exception ex)
    {
    	App.err().report(ErrorLevel.Fatal, Voc.Error_CouldNotLoadSymbolPool, ex);
    	return null;
    }
  }
  
  
  /**
   * Loads and returns the default symbol pool or reports an
   * error if not possible.
   */
  public static SymbolPool loadDefault()
  {
  	return load("default");
  }
  
  
  /**
   * Loads and returns the symbol pool with the given ID or reports an
   * error if not possible.
   */
  public static void createFilesInSystemDir(String id)
  {
  	try
    {
      new SymbolPool(id, true);
    }
    catch (Exception ex)
    {
    	App.err().report(ErrorLevel.Fatal, Voc.Error_CouldNotLoadSymbolPool, ex);
    }
  }
  
  
  /**
   * Creates an empty pool for symbols.
   */
  private SymbolPool()
  {
  	this.id = null;
  	this.symbols = new Hashtable<String, Symbol>(0);
  	this.commonSymbolPool = new CommonSymbolPool();
  }
  
  
  /**
   * Creates a new pool for symbols.
   * @param id                 the ID of the style, e.g. "default"
   * @param createFilesInSystemDir
   *                           if true, the XML and texture files (if any)
   *                           will be written to the programs folder, not
   *                           to the user's home folder.
   */
  private SymbolPool(String id, boolean createFilesInSystemDir)
    throws FileNotFoundException
  {
    this.id = id;
    
    //load symbols from data/symbols/<id>/
    String dir = "data/symbols/" + id;
    Date latestSymbolDate = new Date(0);
    if (!IO.existsDataDirectory(dir))
      throw new FileNotFoundException("\"data/symbols/" + id + "\" does not exist!");
    try
    {
      Set<String> files = IO.listDataFiles(dir, FileTools.getSVGFilter());
      symbols = new Hashtable<String, Symbol>(files.size());
      SVGSymbolLoader loader = new SVGSymbolLoader();
      LinkedList<String> symbolsWithErrors = new LinkedList<String>();
      for (String file : files)
      {
      	try
      	{
      		String symbolPath = "data/symbols/" + id + "/" + file;
      		Symbol symbol = loader.loadSymbol(symbolPath);
      		Date symbolDate = IO.getDataFileModificationDate(symbolPath);
      		if (symbolDate != null && symbolDate.after(latestSymbolDate))
      		{
      			latestSymbolDate = symbolDate;
      		}
          symbols.put(symbol.getID(), symbol);
      	}
        catch (IllegalArgumentException ex)
        {
        	symbolsWithErrors.add(file);
        }
      }
      if (symbolsWithErrors.size() > 0)
      {
      	App.err().report(ErrorLevel.Warning, Voc.Error_CouldNotLoadSymbolPool, symbolsWithErrors);
      }
    }
    catch (Exception ex)
    {
      App.err().report(ErrorLevel.Fatal, Voc.Error_CouldNotLoadSymbolPool, ex);
    }
    
    //if the creation of files is forced, if texture of the style is not available,
    //or one of the symbols is newer than this file, (re)create it
    String texXMLPath = SymbolTexturePool.getTextureXMLPath(id);
    String texPNGPath = SymbolTexturePool.getTexturePNGPath(id, 0);
    Date texXMLDate = IO.getDataFileModificationDate(texXMLPath);
    if (createFilesInSystemDir ||
    	!IO.existsDataFile(texXMLPath) || !IO.existsDataFile(texPNGPath) ||
    	(texXMLDate != null && texXMLDate.before(latestSymbolDate)))
    {
      SymbolTexturePool.createSymbolTextures(id, symbols, 1024, createFilesInSystemDir);
    }
    //load the texture pool
    texturePool = new SymbolTexturePool(id);
    
    //assign the textures to the symbols
    for (Symbol symbol : symbols.values())
    {
      symbol.setTexture(texturePool.getTexture(symbol.getID()));
    }
    
    //create and update the common symbol pool
    commonSymbolPool = new CommonSymbolPool();
    commonSymbolPool.update(this);
    
  }
  
  
  /**
   * Gets the symbol with the given ID, or null if not found.
   * TODO: if the symbol is not found, return a warning symbol!
   */
  public Symbol getSymbol(String id)
  {
    return symbols.get(id);
  }
  
  
  /**
   * Gets the given common symbol in constant time.
   * If the symbol is not found, a warning symbol is returned.
   */
  public Symbol getSymbol(CommonSymbol commonSymbol)
  {
    Symbol ret = commonSymbolPool.getSymbol(commonSymbol);
    if (ret == null)
    	ret = warningSymbol;
    return ret;
  }
  
  
  /**
   * Gets the ID of the texture of this symbol pool.
   */
  public int getTextureID()
  {
    return texturePool.getTextureID();
  }


  /**
   * Gets the ID of the symbols style of this pool.
   */
  public String getID()
  {
    return id;
  }
  
  
  /**
   * Computes and returns the width of the given number, e.g. 1733.
   * This is the sum of the widths of all digits and additional
   * gaps between them.
   * @param number  the number, e.g. 0, 5 or 2738.
   * @param gap     the gap between the digits in interline spaces.
   */
  public float computeNumberWidth(int number, float gap)
  {
    float ret = 0;
    String s = Integer.toString(number);
    for (int i = 0; i < s.length(); i++)
    {
      char d = s.charAt(i);
      Symbol symbol = getSymbol("digit-" + d);
      if (symbol != null)
      {
        ret += symbol.getBoundingRect().size.width;
        if (i < s.length() - 1)
          ret += gap;
      }
    }
    return ret;
  }
  

}
