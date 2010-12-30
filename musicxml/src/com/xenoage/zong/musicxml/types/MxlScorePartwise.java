package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.util.xml.XMLReader.elements;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.util.xml.XMLWriter.createEmptyDocument;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.annotations.NeverEmpty;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.partwise.MxlPart;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML score-partwise.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(children="score-header,part")
public final class MxlScorePartwise
{
	
	public static final String ELEM_NAME = "score-partwise";
	
	@NeverNull private final MxlScoreHeader scoreHeader;
	@NeverEmpty private final PVector<MxlPart> parts;
	@NeverNull private final String version;
	
	private static final String defaultVersion = "1.0";

	
	public MxlScorePartwise(MxlScoreHeader scoreHeader, PVector<MxlPart> parts, String version)
	{
		this.scoreHeader = scoreHeader;
		this.parts = parts;
		this.version = version;
	}

	
	@NeverNull public MxlScoreHeader getScoreHeader()
	{
		return scoreHeader;
	}

	
	@NeverEmpty public PVector<MxlPart> getParts()
	{
		return parts;
	}

	
	@NeverNull public String getVersion()
	{
		return version;
	}
	
	
	@NeverNull public static MxlScorePartwise read(Element e)
	{
		PVector<MxlPart> parts = PVector.pvec();
		for (Element c : elements(e))
		{
			if (c.getNodeName().equals(MxlPart.ELEM_NAME))
				parts = parts.plus(MxlPart.read(c));
		}
		if (parts.size() < 1)
			throw invalid(e);
		return new MxlScorePartwise(MxlScoreHeader.read(e), parts,
			notNull(attribute(e, "version"), defaultVersion));
	}
	
	
	public Document write()
	{
		Document doc = createEmptyDocument();
		Element e = doc.createElement(ELEM_NAME);
		doc.appendChild(e);
		scoreHeader.write(e);
		for (MxlPart part : parts)
			part.write(e);
		addAttribute(e, "version", version);
		return doc;
	}
	

}
