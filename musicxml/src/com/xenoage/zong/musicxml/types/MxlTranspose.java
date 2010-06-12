package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.Parse.parseChildInt;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML transpose.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="diatonic,octave-change,double")
public final class MxlTranspose
{
	
	public static final String ELEM_NAME = "transpose";
	
	private final int chromatic;

	
	public MxlTranspose(int chromatic)
	{
		this.chromatic = chromatic;
	}

	
	public int getChromatic()
	{
		return chromatic;
	}
	
	
	@NeverNull public static MxlTranspose read(Element e)
	{
		return new MxlTranspose(parseChildInt(e, "chromatic"));
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		addElement("chromatic", chromatic, e);
	}

}
