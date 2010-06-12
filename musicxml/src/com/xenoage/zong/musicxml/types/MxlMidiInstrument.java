package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.parseChildIntNull;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML midi-instrument.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="midi-name,midi-bank,midi-unpitched,volume," +
	"pan,elevation")
public final class MxlMidiInstrument
{
	
	public static final String ELEM_NAME = "midi-instrument";
	
	@MaybeNull private final Integer midiChannel;
	@MaybeNull private final Integer midiProgram;
	@NeverNull private final String id;
	
	
	public MxlMidiInstrument(Integer midiChannel, Integer midiProgram, String id)
	{
		this.midiChannel = midiChannel;
		this.midiProgram = midiProgram;
		this.id = id;
	}

	
	@MaybeNull public Integer getMidiChannel()
	{
		return midiChannel;
	}


	@MaybeNull public Integer getMidiProgram()
	{
		return midiProgram;
	}

	
	@NeverNull public String getID()
	{
		return id;
	}
	
	
	@NeverNull public static MxlMidiInstrument read(Element e)
	{
		return new MxlMidiInstrument(
			parseChildIntNull(e, "midi-channel"),
			parseChildIntNull(e, "midi-program"),
			throwNull(attribute(e, "id"), e));
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		addElement("midi-channel", midiChannel, e);
		addElement("midi-program", midiProgram, e);
		addAttribute(e, "id", id);
	}
	

}
