package com.xenoage.zong.data.instrument;


/**
 * Instrument group, like woodwind, brass or
 * keyboard.
 * 
 * @author Andreas Wenger
 */
public final class InstrumentGroup
{
	
	private final String id;
	private final String name; //international name
	
	
	public InstrumentGroup(String id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	
	public String getID()
	{
		return id;
	}
	
	
	public String getName()
	{
		return name;
	}

}
