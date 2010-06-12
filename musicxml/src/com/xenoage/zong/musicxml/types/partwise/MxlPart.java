package com.xenoage.zong.musicxml.types.partwise;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.util.xml.XMLReader.elements;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;

import org.w3c.dom.Element;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.annotations.NeverEmpty;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML part in a partwise score.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(children="measure")
public final class MxlPart
{
	
	public static final String ELEM_NAME = "part";
	
	@NeverEmpty private final PVector<MxlMeasure> measures;
	@NeverNull private final String id;
	
	
	public MxlPart(PVector<MxlMeasure> measures, String id)
	{
		this.measures = measures;
		this.id = id;
	}
	
	
	@NeverEmpty public PVector<MxlMeasure> getMeasures()
	{
		return measures;
	}

	
	@NeverNull public String getID()
	{
		return id;
	}


	public static MxlPart read(Element e)
	{
		PVector<MxlMeasure> measures = pvec();
		for (Element c : elements(e))
		{
			if (c.getNodeName().equals(MxlMeasure.ELEM_NAME))
				measures = measures.plus(MxlMeasure.read(c));
		}
		if (measures.size() < 1)
			throw invalid(e);
		return new MxlPart(measures, throwNull(attribute(e, "id"), e));
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		for (MxlMeasure measure : measures)
			measure.write(e);
		addAttribute(e, "id", id);
	}
	

}
