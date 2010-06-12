package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML instrument.
 * 
 * @author Andreas Wenger
 */
public final class MxlInstrument
{
	
	public static final String ELEM_NAME = "instrument";
	
	@NeverNull private final String id;

	
	public MxlInstrument(String id)
	{
		this.id = id;
	}

	
	@NeverNull public String getID()
	{
		return id;
	}
	
	
	@NeverNull public static MxlInstrument read(Element e)
	{
		return new MxlInstrument(throwNull(attribute(e, "id"), e));
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		addAttribute(e, "id", id);
	}
	

}
