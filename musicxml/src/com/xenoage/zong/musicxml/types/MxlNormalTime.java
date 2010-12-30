package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;
import static com.xenoage.zong.musicxml.util.Parse.parseChildInt;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.choice.MxlTimeContent;


/**
 * MusicXML beats/beats-type content for a time element.
 * 
 * Only one beats and beats-type is supported.
 * 
 * @author Andreas Wenger
 */
public final class MxlNormalTime
	implements MxlTimeContent
{
	
	private final int beats;
	private final int beatType;
	
	
	public MxlNormalTime(int beats, int beatType)
	{
		this.beats = beats;
		this.beatType = beatType;
	}

	
	public int getBeats()
	{
		return beats;
	}

	
	public int getBeatType()
	{
		return beatType;
	}
	
	
	@Override public MxlTimeContentType getTimeContentType()
	{
		return MxlTimeContentType.NormalTime;
	}
	
	
	@NeverNull public static MxlNormalTime read(Element e)
	{
		try
		{
			return new MxlNormalTime(
				parseChildInt(e, "beats"), parseChildInt(e, "beat-type"));
		}
		catch (NumberFormatException ex)
		{
			throw invalid(e);
		}
	}
	
	
	public void write(Element e)
	{
		addElement("beats", beats, e);
		addElement("beat-type", beatType, e);
	}

}
