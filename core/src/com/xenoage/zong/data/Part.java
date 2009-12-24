package com.xenoage.zong.data;

import com.xenoage.zong.data.instrument.Instrument;


/**
 * In most cases a part is simply a single
 * staff assigned to a single instrument.
 * 
 * It can also be group of staves that belong
 * together, e.g. the two staves of a piano.
 * It can have more than one instrument,
 * e.g. in percussion staves where
 * several instruments are notated in a single staff.
 * 
 * @author Andreas Wenger
 */
public final class Part
{

	//the name and the abbreviation (optional) of the part
	private final String name;
	private final String abbreviation;
	
	//the number of staves in this part
	private final int stavesCount;

	//the instrument belonging to this part
	private final Instrument instrument;

  
  /**
   * Creates a new part.
   * @param name          the name of the part
   * @param abbreviation  the abbreviation of the part, or null
   * @param stavesCount   the number of staves in this part
   * @param instrument    the used instrument. if null, the default instrument is used.
   */
	public Part(String name, String abbreviation, int stavesCount, Instrument instrument)
	{
		this.name = name;
		this.abbreviation = abbreviation;
		this.stavesCount = stavesCount;
		if (instrument != null)
			this.instrument = instrument;
		else
			this.instrument = Instrument.getDefaultInstrument(); //TODO: final
	}
	
	
	/**
	 * Gets the name of this part.
	 */
	public String getName()
	{
		return name;
	}

  
  /**
	 * Gets the abbreviation of this part, or null
	 * if not available.
	 */
	public String getAbbreviation()
	{
		return abbreviation;
	}
  
  
  /**
   * Gets the number of staves used in this part.
   */
  public int getStavesCount()
  {
    return stavesCount;
  }


	/**
	 * Gets the instrument used by this part.
	 */
	public Instrument getInstrument()
	{
		return instrument;
	}
  
}
