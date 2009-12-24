package com.xenoage.zong.data.instrument;

import java.util.List;


/**
 * Unpitched instrument, like drums or percussion.
 * 
 * @author Andreas Wenger
 */
public final class UnpitchedInstrument
	extends Instrument
{
	
	
	/**
	 * Creates a new unpitched instrument.
	 * @param id            the unique id of this instrument
	 * @param name          the international name, beginning with an uppercase letter
	 * @param abbreviation  the international abbreviation, beginning with an uppercase letter
	 * @param groups        the groups this instrument belongs to. null is also allowed.
	 */
	public UnpitchedInstrument(String id, String name, String abbreviation, List<InstrumentGroup> groups)
	{
		super(id, name, abbreviation, groups);
	}
	

}
