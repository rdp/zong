package com.xenoage.zong.core.instrument;

import static com.xenoage.zong.core.music.Pitch.pi;

import java.util.LinkedList;

import com.xenoage.pdlib.IVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.VocByString;


/**
 * Class for an instrument.
 * 
 * There are pitched instruments, like piano or trumpet,
 * and unpitched instruments, like drums. If this information
 * is known, use the corresponding subclasses instead.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public abstract class Instrument
{

	//id and general information
	protected final String id;
	protected final String name; //international name
	protected final String abbreviation; //international abbreviation
	protected final Vector<InstrumentGroup> groups;
	
	//default instrument: piano
	public static final Instrument defaultValue = new PitchedInstrument(
		"default", "Piano", "Pno", null, 0, Transpose.none(),
		pi(6, 0, 1), pi(0, 0, 8), 0);
	
	
	/**
	 * Creates a new instrument.
	 * @param id            the unique id of this instrument
	 * @param name          the international name, beginning with an uppercase letter
	 * @param abbreviation  the international abbreviation, beginning with an uppercase letter
	 * @param groups        the groups this instrument belongs to. null is also allowed.
	 */
	public Instrument(String id, String name, String abbreviation, Vector<InstrumentGroup> groups)
	{
		this.id = id;
		this.name = name;
		this.abbreviation = abbreviation;
		this.groups = groups;
	}
	
	
	/**
	 * Gets the ID of the instrument (e.g. "instrument_piano").
	 */
	public String getID()
	{
		return id;
	}
	
	
	/**
	 * Gets the international name of this instrument.
	 * This is never null.
	 */
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * Gets the localized name of this instrument. If it is undefined, the
	 * international name is returned.
	 */
	public String getLocalName()
	{
		String ret = Lang.getWithNull(new VocByString("Instrument", getID() + "_Name"));
		if (ret == null)
		{
			return name;
		}
		else
		{
			return ret;
		}
	}
	
	
	/**
	 * Gets the international abbreviation of this instrument, or null,
	 * if unknown.
	 */
	public String getAbbreviation()
	{
		return abbreviation;
	}

	
	/**
	 * Gets the localized abbreviation of this instrument. If it is undefined, the
	 * international name is returned.
	 */
	public String getLocalAbbreviation()
	{
		String ret = Lang.getWithNull(new VocByString("Instrument", getID() + "_Abbr"));
		if (ret == null)
		{
			return abbreviation;
		}
		else
		{
			return ret;
		}
	}
	
	
	/**
	 * Returns the groups in which the instrument is listed (e.g. woodwinds,
	 * percussion etc.), or null if undefined.
	 */
	public Vector<InstrumentGroup> getGroups()
	{
		return groups;
	}
	
	
	/**
	 * Returns the groupnames in which the instrument is listed (e.g. woodwinds,
	 * percussion etc.). The returned list is never null.
	 * TODO: needed?
	 */
	public IVector<String> getGroupNames()
	{
		LinkedList<String> ret = new LinkedList<String>();
		if (groups != null)
		{
			for (InstrumentGroup value : groups)
			{
				ret.add(Lang.get(new VocByString("InstrumentGroup", value.getID())));
			}
		}
		return new IVector<String>(ret);
	}


	/**
	 * Returns true, if the given object is an {@link Instrument} with the
	 * same ID as this one, otherwise false.
	 */
	@Override public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		else if (o instanceof Instrument)
		{
			return id.equals(((Instrument) o).getID());
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * Gets the hash code of this instrument.
	 * This is the hash code of its ID.
	 */
	@Override public int hashCode()
	{
		return id.hashCode();
	}


}
