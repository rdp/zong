package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.Parse.parseChildInt;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.choice.MxlNoteContent;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML cue note content.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(children="full-note")
public final class MxlCueNote
	implements MxlNoteContent
{
	
	@NeverNull private final MxlFullNote fullNote;
	private final int duration;
	
	
	public MxlCueNote(MxlFullNote fullNote, int duration)
	{
		this.fullNote = fullNote;
		this.duration = duration;
	}

	
	@Override @NeverNull public MxlFullNote getFullNote()
	{
		return fullNote;
	}
	
	
	public int getDuration()
	{
		return duration;
	}


	@Override public MxlNoteContentType getNoteContentType()
	{
		return MxlNoteContentType.Cue;
	}
	
	
	@NeverNull public static MxlCueNote read(Element e)
	{
		return new MxlCueNote(MxlFullNote.read(e), parseChildInt(e, "duration"));
	}
	
	
	public void write(Element e)
	{
		addElement("cue", e);
		fullNote.write(e);
		addElement("duration", duration, e);
	}
	

}
