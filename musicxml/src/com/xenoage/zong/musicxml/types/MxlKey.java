package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLWriter.addElement;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.xml.XMLWriter;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;
import com.xenoage.zong.musicxml.util.Parse;


/**
 * MusicXML key.
 * 
 * Currently only the fifths element is supported.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="non-traditional-key,key-octave,number,print-style,print-object",
	partly="traditional-key")
public final class MxlKey
{
	
	public static final String ELEM_NAME = "key";
	
	private final int fifths;

	
	public MxlKey(int fifths)
	{
		this.fifths = fifths;
	}

	
	public int getFifths()
	{
		return fifths;
	}
	
	
	/**
	 * Returns null, if the key is unsupported.
	 */
	@MaybeNull public static MxlKey read(Element e)
	{
		Integer fifths = Parse.parseChildIntNull(e, "fifths");
		if (fifths != null)
			return new MxlKey(fifths);
		else
			return null;
	}
	
	
	public void write(Element parent)
	{
		Element e = XMLWriter.addElement(ELEM_NAME, parent);
		addElement("fifths", fifths, e);
	}
	

}
