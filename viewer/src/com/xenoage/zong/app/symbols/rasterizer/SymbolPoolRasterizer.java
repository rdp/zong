package com.xenoage.zong.app.symbols.rasterizer;

import com.xenoage.zong.app.symbols.SymbolPool;


/**
 * This little program creates the textures for the
 * given symbol pool (usually "default").
 * 
 * @author James Le Cuirot
 * @author Andreas Wenger
 */
public class SymbolPoolRasterizer
{

	public static void main(String[] args)
	{
		try
		{
			new SymbolPool(args[0]);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
}
