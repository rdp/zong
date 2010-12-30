package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.xml.XMLReader.element;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.parseChildIntNull;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.enums.MxlClefSign;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;
import com.xenoage.zong.musicxml.util.Parse;


/**
 * MusicXML clef.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="line,additional,size,print-style,print-object")
public final class MxlClef
{
	
	public static final String ELEM_NAME = "clef";
	
	@NeverNull private final MxlClefSign sign;
	private final int clefOctaveChange;
	private final int number;
	
	private static final int defaultClefOctaveChange = 0;
	private static final int defaultNumber = 1;
	
	
	public MxlClef(MxlClefSign sign, int clefOctaveChange, int number)
	{
		this.sign = sign;
		this.clefOctaveChange = clefOctaveChange;
		this.number = number;
	}

	
	@NeverNull public MxlClefSign getSign()
	{
		return sign;
	}

	
	public int getClefOctaveChange()
	{
		return clefOctaveChange;
	}

	
	public int getNumber()
	{
		return number;
	}
	
	
	public static MxlClef read(Element e)
	{
		return new MxlClef(MxlClefSign.read(throwNull(element(e, MxlClefSign.ELEM_NAME), e)),
			notNull(parseChildIntNull(e, "clef-octave-change"), defaultClefOctaveChange),
			notNull(Parse.parseAttrIntNull(e, "number"), defaultNumber));
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		sign.write(e);
		addElement("clef-octave-change", clefOctaveChange, e);
		addAttribute(e, "number", number);
	}
	

}
