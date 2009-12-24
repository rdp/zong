package com.xenoage.zong.app.symbols.common;

import com.xenoage.zong.app.symbols.Symbol;
import com.xenoage.zong.app.symbols.SymbolPool;
import com.xenoage.zong.app.symbols.WarningSymbol;


/**
 * This class manages an array of all symbols
 * that are part of the CommonSymbol enumeration,
 * allowing access to them in constant time.
 * 
 * @author Andreas Wenger
 */
public class CommonSymbolPool
{

	private Symbol[] symbols = new Symbol[0];
	
	
	/**
	 * Updates the symbols, that means, all
	 * symbols in the pool are reloaded from the
	 * given symbol pool.
	 */
	public void update(SymbolPool pool)
	{
		symbols = new Symbol[CommonSymbol.values().length];
		int i = 0;
		for (CommonSymbol commonSymbol : CommonSymbol.values())
		{
			symbols[i] = pool.getSymbol(commonSymbol.getID());
			i++;
		}
	}
	
	
	/**
	 * Gets the symbol belonging to the given CommonSymbol.
	 */
	public Symbol getSymbol(CommonSymbol commonSymbol)
	{
		if (commonSymbol.ordinal() < symbols.length)
		{
			return symbols[commonSymbol.ordinal()];
		}
		else
		{
			return new WarningSymbol();
		}
	}
	
	
}
