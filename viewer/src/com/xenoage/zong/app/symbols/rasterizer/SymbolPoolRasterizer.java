package com.xenoage.zong.app.symbols.rasterizer;

import com.xenoage.util.io.IO;
import com.xenoage.util.logging.Log;
import com.xenoage.zong.app.symbols.SymbolPool;


/**
 * This little program creates the textures for the
 * given symbol pool (usually "default").
 * 
 * @author Andreas Wenger
 * @author James Le Cuirot
 */
public class SymbolPoolRasterizer
{

	public static void main(String[] args)
	{
		try
		{
			IO.initApplication(SymbolPoolRasterizer.class.getSimpleName());
			Log.initNoLog();
			SymbolPool.createFilesInSystemDir(args[0]);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
}
