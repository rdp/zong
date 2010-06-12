package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLWriter.addElement;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.choice.MxlFullNoteContent;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML unpitched.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="display-step-octave")
public final class MxlUnpitched
	implements MxlFullNoteContent
{
	
	public static final String XML_NAME = "unpitched";
	
	
	@Override public MxlFullNoteContentType getFullNoteContentType()
	{
		return MxlFullNoteContentType.Unpitched;
	}
	
	
	@NeverNull public static MxlUnpitched read(Element e)
	{
		return new MxlUnpitched();
	}
	
	
	public void write(Element parent)
	{
		addElement(XML_NAME, parent);
	}

}
