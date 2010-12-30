package com.xenoage.zong.musicxml.types.groups;

import static com.xenoage.util.xml.XMLWriter.addElement;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.util.xml.XMLReader;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML editorial-voice and editorial-voice-direction
 * (they have equal content).
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="footnote,level")
public final class MxlEditorialVoice
{
	
	@MaybeNull private final String voice;
	
	public static final MxlEditorialVoice empty = new MxlEditorialVoice(null);
	
	
	public MxlEditorialVoice(String voice)
	{
		this.voice = voice;
	}
	
	
	@MaybeNull public String getVoice()
	{
		return voice;
	}
	
	
	@NeverNull public static MxlEditorialVoice read(Element e)
	{
		String voice = XMLReader.elementText(e, "voice");
		if (voice != null)
			return new MxlEditorialVoice(voice);
		else
			return empty;
	}
	
	
	public void write(Element e)
	{
		if (this != empty)
		{
			if (voice != null)
				addElement("voice", voice, e);
		}
	}
	

}
