package com.xenoage.zong.musicxml.types;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.xml.XMLReader.elements;
import static com.xenoage.util.xml.XMLWriter.addElement;

import org.w3c.dom.Element;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.annotations.MaybeEmpty;
import com.xenoage.zong.musicxml.types.choice.MxlArticulationsContent;
import com.xenoage.zong.musicxml.types.choice.MxlNotationsContent;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML articulations.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="detached-legato,spiccato,scoop,plop,doit,falloff,breath-mark," +
	"caesura,stress,unstress,other-articulation")
public final class MxlArticulations
	implements MxlNotationsContent
{
	
	public static final String ELEM_NAME = "articulations";
	
	@MaybeEmpty private final PVector<MxlArticulationsContent> content;
	
	
	public MxlArticulations(PVector<MxlArticulationsContent> content)
	{
		this.content = content;
	}

	
	@MaybeEmpty public PVector<MxlArticulationsContent> getContent()
	{
		return content;
	}


	@Override public MxlNotationsContentType getNotationsContentType()
	{
		return MxlNotationsContentType.Articulations;
	}
	
	
	public static MxlArticulations read(Element e)
	{
		PVector<MxlArticulationsContent> content = pvec();
		for (Element c : elements(e))
		{
			String n = c.getNodeName();
			switch (n.charAt(0))
			{
				case 'a':
					if (n.equals(MxlAccent.ELEM_NAME))
						content = content.plus(MxlAccent.read(c));
					break;
				case 's':
					if (n.equals(MxlStrongAccent.ELEM_NAME))
						content = content.plus(MxlStrongAccent.read(c));
					else if (n.equals(MxlStaccato.ELEM_NAME))
						content = content.plus(MxlStaccato.read(c));
					else if (n.equals(MxlStaccatissimo.ELEM_NAME))
						content = content.plus(MxlStaccatissimo.read(c));
					break;
				case 't':
					if (n.equals(MxlTenuto.ELEM_NAME))
						content = content.plus(MxlTenuto.read(c));
					break;
			}
		}
		return new MxlArticulations(content);
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		for (MxlArticulationsContent item : content)
			item.write(e);
	}
	

}
