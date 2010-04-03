package com.xenoage.zong.app.symbols.rasterizer;

import java.io.FileOutputStream;
import java.io.IOException;

import com.xenoage.util.math.Size2f;
import com.xenoage.util.math.TextureRectangle2f;
import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.loader.SVGSymbolLoader;


/**
 * Try out the {@link SymbolsRasterizer} class.
 * 
 * @author Uli Teschemacher
 */
public class SymbolsRasterizerTry
{
  
	public void SymbolsRasterizerTestMethod()
	{
		SVGSymbolLoader sl = new SVGSymbolLoader();
		Symbol[] s = new Symbol[9];
		s[0] = sl.loadSymbol("data/symbols/default/clef-g.svg");
		s[1] = sl.loadSymbol("data/symbols/default/clef-g.svg");
		s[2] = sl.loadSymbol("data/symbols/default/clef-f.svg");
		s[3] = sl.loadSymbol("data/symbols/default/clef-f.svg");
		s[4] = sl.loadSymbol("data/symbols/default/clef-c.svg");
		s[5] = sl.loadSymbol("data/symbols/default/clef-c.svg");
		s[6] = sl.loadSymbol("data/symbols/default/clef-g-8va.svg");
		s[7] = sl.loadSymbol("data/symbols/default/clef-g-8vb.svg");
		s[8] = sl.loadSymbol("data/symbols/default/clef-g.svg");
		
		Size2f[] sizes = new Size2f[s.length];
		for(int i=0; i<s.length; i++)
		{
			sizes[i]= new Size2f(s[i].getBoundingRect().size);
		}
		SymbolsAdjuster adjuster = new SymbolsAdjuster(1024,1024,sizes,4);
		TextureRectangle2f[] rec = adjuster.getAllTextureRectangles2f();
		
		try
		{
			SymbolsRasterizer.rasterizeSymbols(new FileOutputStream("SymbolsRasterizerTry.png"),
				1024, 1024, s, rec);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
