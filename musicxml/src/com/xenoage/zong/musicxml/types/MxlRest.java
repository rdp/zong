package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLWriter.addElement;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.choice.MxlFullNoteContent;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML rest.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="display-step-octave")
public final class MxlRest
	implements MxlFullNoteContent
{
	
	public static final String XML_NAME = "rest";
	
	
	@Override public MxlFullNoteContentType getFullNoteContentType()
	{
		return MxlFullNoteContentType.Rest;
	}
	
	
	@NeverNull public static MxlRest read(Element e)
	{
		return new MxlRest();
	}
	
	
	public void write(Element parent)
	{
		addElement(XML_NAME, parent);
	}

}
