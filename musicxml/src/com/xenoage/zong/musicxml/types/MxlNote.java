package com.xenoage.zong.musicxml.types;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.xml.XMLReader.element;
import static com.xenoage.util.xml.XMLReader.elements;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.Parse.parseInt;

import java.util.List;

import org.w3c.dom.Element;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.annotations.MaybeEmpty;
import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.choice.MxlMusicDataContent;
import com.xenoage.zong.musicxml.types.choice.MxlNoteContent;
import com.xenoage.zong.musicxml.types.groups.MxlEditorialVoice;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML note.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="type,dot,accidental,time-modification,notehead," +
	"x-position,font,color,printout,dynamics,end-dynamics,attack,release,time-only,pizzicato",
	children="beam,editorial-voice,notations,lyric")
public final class MxlNote
	implements MxlMusicDataContent
{
	
	public static final String ELEM_NAME = "note";
	
	@NeverNull private final MxlNoteContent content;
	@MaybeNull private final MxlInstrument instrument;
	@NeverNull private final MxlEditorialVoice editorialVoice;
	@MaybeNull private final MxlStem stem;
	@MaybeNull private final Integer staff;
	@MaybeEmpty private final PVector<MxlBeam> beams;
	@MaybeEmpty private final PVector<MxlNotations> notations;
	@MaybeEmpty private final PVector<MxlLyric> lyrics;
	
	private static final PVector<MxlBeam> beamsEmpty = pvec();
	private static final PVector<MxlNotations> notationsEmpty = pvec();
	private static final PVector<MxlLyric> lyricsEmpty = pvec();
	
	
	public MxlNote(MxlNoteContent content, MxlInstrument instrument,
		MxlEditorialVoice editorialVoice, MxlStem stem, Integer staff,
		PVector<MxlBeam> beams, PVector<MxlNotations> notations, PVector<MxlLyric> lyrics)
	{
		this.content = content;
		this.instrument = instrument;
		this.editorialVoice = editorialVoice;
		this.stem = stem;
		this.staff = staff;
		this.beams = beams;
		this.notations = notations;
		this.lyrics = lyrics;
	}

	
	@NeverNull public MxlNoteContent getContent()
	{
		return content;
	}
	
	
	@MaybeNull public MxlInstrument getInstrument()
	{
		return instrument;
	}
	
	
	@NeverNull public MxlEditorialVoice getEditorialVoice()
	{
		return editorialVoice;
	}


	@MaybeNull public MxlStem getStem()
	{
		return stem;
	}

	
	@MaybeNull public Integer getStaff()
	{
		return staff;
	}

	
	@MaybeEmpty public PVector<MxlBeam> getBeams()
	{
		return beams;
	}

	
	@MaybeEmpty public PVector<MxlNotations> getNotations()
	{
		return notations;
	}

	
	@MaybeEmpty public PVector<MxlLyric> getLyrics()
	{
		return lyrics;
	}
	
	
	@Override public MxlMusicDataContentType getMusicDataContentType()
	{
		return MxlMusicDataContentType.Note;
	}
	
	
	@NeverNull public static MxlNote read(Element e)
	{
		MxlNoteContent content = readNoteContent(e);
		MxlInstrument instrument = null;
		MxlEditorialVoice editorialVoice = MxlEditorialVoice.read(e);
		List<Element> children = elements(e);
		MxlStem stem = null;
		Integer staff = null;
		PVector<MxlBeam> beams = beamsEmpty;
		PVector<MxlNotations> notations = notationsEmpty;
		PVector<MxlLyric> lyrics = lyricsEmpty;
		for (Element child : children)
		{
			String n = child.getNodeName();
			switch (n.charAt(0)) //performance
			{
				case 's':
					if (n.equals(MxlStem.ELEM_NAME))
						stem = MxlStem.read(child);
					else if (n.equals("staff"))
						staff = parseInt(child);
					break;
				case 'b':
					if (n.equals(MxlBeam.ELEM_NAME))
						beams = beams.plus(MxlBeam.read(child));
					break;
				case 'i':
					if (n.equals(MxlInstrument.ELEM_NAME))
						instrument = MxlInstrument.read(child);
					break;
				case 'n':
					if (n.equals(MxlNotations.ELEM_NAME))
						notations = notations.plus(MxlNotations.read(child));
					break;
				case 'l':
					if (n.equals(MxlLyric.ELEM_NAME))
						lyrics = lyrics.plus(MxlLyric.read(child));
					break;
			}
		}
		return new MxlNote(content, instrument, editorialVoice,
			stem, staff, beams, notations, lyrics);
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		content.write(e);
		if (instrument != null)
			instrument.write(e);
		editorialVoice.write(e);
		if (stem != null)
			stem.write(e);
		addElement("staff", staff, e);
		for (MxlBeam beam : beams)
			beam.write(e);
		for (MxlNotations n : notations)
			n.write(e);
		for (MxlLyric lyric : lyrics)
			lyric.write(e);
	}
	
	
	private static MxlNoteContent readNoteContent(Element e)
	{
		Element firstChild = element(e);
		String n = firstChild.getNodeName();
		if (n.equals("grace"))
			return MxlGraceNote.read(e);
		else if (n.equals("cue"))
			return MxlCueNote.read(e);
		else
			return MxlNormalNote.read(e);
	}

}
