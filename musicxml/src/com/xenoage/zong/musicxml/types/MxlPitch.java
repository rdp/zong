package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.xml.XMLReader.elementText;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.core.music.Pitch.pi;
import static com.xenoage.zong.musicxml.util.InvalidCore.invalidCore;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;
import static com.xenoage.zong.musicxml.util.Parse.parseChildInt;
import static com.xenoage.zong.musicxml.util.Parse.parseChildIntNull;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.core.music.Pitch;
import com.xenoage.zong.musicxml.types.choice.MxlFullNoteContent;


/**
 * MusicXML pitch.
 * 
 * @author Andreas Wenger
 */
public final class MxlPitch
	implements MxlFullNoteContent
{
	
	public static final String XML_NAME = "pitch";
	
	@NeverNull private final Pitch pitch;


	public MxlPitch(Pitch pitch)
	{
		this.pitch = pitch;
	}
	
	
	@NeverNull public Pitch getPitch()
	{
		return pitch;
	}
	
	
	@Override public MxlFullNoteContentType getFullNoteContentType()
	{
		return MxlFullNoteContentType.Pitch;
	}


	@NeverNull public static MxlPitch read(Element e)
	{
		return new MxlPitch(pi(readStep(e),
			notNull(parseChildIntNull(e, "alter"), 0), parseChildInt(e, "octave")));
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(XML_NAME, parent);
		addElement("step", writeStep(pitch.getStep()), e);
		addElement("alter", pitch.getAlter(), e);
		addElement("octave", pitch.getOctave(), e);
	}
	
	
	private static int readStep(Element e)
	{
		switch (elementText(e, "step").charAt(0))
		{
			case 'C': return 0;
			case 'D': return 1;
			case 'E': return 2;
			case 'F': return 3;
			case 'G': return 4;
			case 'A': return 5;
			case 'B': return 6;
		}
		throw invalid(e);
	}
	
	
	private static char writeStep(int step)
	{
		switch (step)
		{
			case 0: return 'C';
			case 1: return 'D';
			case 2: return 'E';
			case 3: return 'F';
			case 4: return 'G';
			case 5: return 'A';
			case 6: return 'B';
		}
		throw invalidCore(step);
	}

}
