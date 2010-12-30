package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLReader.elements;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.Parse.parseInt;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.choice.MxlMusicDataContent;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML attributes.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="editorial,part-symbol,instruments,staff-details," +
	"directive,measure-style", partly="key,time,clef", children="key,time,clef,transpose")
public final class MxlAttributes
	implements MxlMusicDataContent
{
	
	public static final String ELEM_NAME = "attributes";
	
	@MaybeNull private final Integer divisions;
	@MaybeNull private final MxlKey key;
	@MaybeNull private final MxlTime time;
	@MaybeNull private final Integer staves;
	@MaybeNull private final MxlClef clef;
	@MaybeNull private final MxlTranspose transpose;
	
	
	public MxlAttributes(Integer divisions, MxlKey key, MxlTime time, Integer staves,
		MxlClef clef, MxlTranspose transpose)
	{
		this.divisions = divisions;
		this.key = key;
		this.time = time;
		this.staves = staves;
		this.clef = clef;
		this.transpose = transpose;
	}


	@MaybeNull public Integer getDivisions()
	{
		return divisions;
	}


	@MaybeNull public MxlKey getKey()
	{
		return key;
	}


	@MaybeNull public MxlTime getTime()
	{
		return time;
	}
	
	
	@MaybeNull public Integer getStaves()
	{
		return staves;
	}


	@MaybeNull public MxlClef getClef()
	{
		return clef;
	}


	@MaybeNull public MxlTranspose getTranspose()
	{
		return transpose;
	}
	
	
	@Override public MxlMusicDataContentType getMusicDataContentType()
	{
		return MxlMusicDataContentType.Attributes;
	}
	
	
	@NeverNull public static MxlAttributes read(Element e)
	{
		Integer divisions = null;
		MxlKey key = null;
		MxlTime time = null;
		Integer staves = null;
		MxlClef clef = null;
		MxlTranspose transpose = null;
		for (Element child : elements(e))
		{
			String n = child.getNodeName();
			switch (n.charAt(0))
			{
				case 'c':
					if (MxlClef.ELEM_NAME.equals(n))
						clef = MxlClef.read(child);
					break;
				case 'd':
					if ("divisions".equals(n))
						divisions = parseInt(child);
					break;
				case 'k':
					if (MxlKey.ELEM_NAME.equals(n))
						key = MxlKey.read(child);
					break;
				case 's':
					if ("staves".equals(n))
						staves = parseInt(child);
					break;
				case 't':
					if (MxlTime.ELEM_NAME.equals(n))
						time = MxlTime.read(child);
					else if (MxlTranspose.ELEM_NAME.equals(n))
						transpose = MxlTranspose.read(child);
					break;
			}
		}
		return new MxlAttributes(divisions, key, time, staves, clef, transpose);
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		addElement("divisions", divisions, e);
		if (key != null)
			key.write(e);
		if (time != null)
			time.write(e);
		addElement("staves", staves, e);
		if (clef != null)
			clef.write(e);
		if (transpose != null)
			transpose.write(e);
	}
	
	
	
	
	

}
