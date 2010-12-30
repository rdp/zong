package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLWriter.addElement;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.choice.MxlTimeContent;


/**
 * MusicXML senza-misura.
 * 
 * @author Andreas Wenger
 */
public final class MxlSenzaMisura
	implements MxlTimeContent
{
	
	public static final String ELEM_NAME = "senza-misura";
	

	@Override public MxlTimeContentType getTimeContentType()
	{
		return MxlTimeContentType.SenzaMisura;
	}

	
	@NeverNull public static MxlSenzaMisura read(Element e)
	{
		return new MxlSenzaMisura();
	}
	

	@Override public void write(Element e)
	{
		addElement(ELEM_NAME, e);
	}

}
