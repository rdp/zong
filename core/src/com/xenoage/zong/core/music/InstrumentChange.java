package com.xenoage.zong.core.music;

import com.xenoage.zong.core.instrument.Instrument;


/**
 * This element indicates the change of an instrument
 * within a staff.
 * 
 * @author Andreas Wenger
 */
public final class InstrumentChange
	implements MeasureElement
{

	private final Instrument instrument;


	public InstrumentChange(Instrument instrument)
	{
		this.instrument = instrument;
	}


	public Instrument getInstrument()
	{
		return instrument;
	}


	@Override public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		else if (o instanceof InstrumentChange)
		{
			return instrument.equals(((InstrumentChange) o).instrument);
		}
		else
		{
			return false;
		}
	}

	
}
