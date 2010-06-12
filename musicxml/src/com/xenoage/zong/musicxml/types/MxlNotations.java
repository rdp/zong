package com.xenoage.zong.musicxml.types;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.xml.XMLReader.elements;
import static com.xenoage.util.xml.XMLWriter.addElement;

import org.w3c.dom.Element;

import com.xenoage.pdlib.PVector;
import com.xenoage.zong.musicxml.types.choice.MxlNotationsContent;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML notations.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="tuplet,glissando,slide,ornaments,technical,fermata," +
	"arpeggiate,non-arpeggiate,accidental-mark,other-notation",
	children="slur,tied,articulations,dynamics")
public final class MxlNotations
{
	
	public static final String ELEM_NAME = "notations";
	
	private final PVector<MxlNotationsContent> elements;
	
	
	public MxlNotations(PVector<MxlNotationsContent> elements)
	{
		this.elements = elements;
	}

	
	public PVector<MxlNotationsContent> getElements()
	{
		return elements;
	}
	
	
	public static MxlNotations read(Element e)
	{
		PVector<MxlNotationsContent> elements = pvec();
		for (Element child : elements(e))
		{
			String childName = child.getNodeName();
			MxlNotationsContent element = null;
			if (childName.equals(MxlArticulations.ELEM_NAME))
			{
				element = MxlArticulations.read(child);
			}
			else if (childName.equals(MxlDynamics.ELEM_NAME))
			{
				element = MxlDynamics.read(child);
			}
			else if (childName.equals(MxlCurvedLine.ELEM_NAME_SLUR) ||
				childName.equals(MxlCurvedLine.ELEM_NAME_TIED))
			{
				element = MxlCurvedLine.read(child);
			}
			if (element != null)
			{
				elements = elements.plus(element);
			}
		}
		return new MxlNotations(elements);
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		for (MxlNotationsContent element : elements)
		{
			element.write(e);
		}
	}
	

}
