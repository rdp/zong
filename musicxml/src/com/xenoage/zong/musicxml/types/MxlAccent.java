package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLWriter.addElement;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.attributes.MxlEmptyPlacement;
import com.xenoage.zong.musicxml.types.choice.MxlArticulationsContent;


/**
 * MusicXML accent.
 * 
 * @author Andreas Wenger
 */
public final class MxlAccent
	implements MxlArticulationsContent
{
	
	public static final String ELEM_NAME = "accent";
	
	@NeverNull private final MxlEmptyPlacement emptyPlacement;
	
	public static final MxlAccent defaultInstance = new MxlAccent(MxlEmptyPlacement.empty);

	
	public MxlAccent(MxlEmptyPlacement emptyPlacement)
	{
		this.emptyPlacement = emptyPlacement;
	}

	
	@NeverNull public MxlEmptyPlacement getEmptyPlacement()
	{
		return emptyPlacement;
	}
	
	
	@Override public MxlArticulationsContentType getArticulationsContentType()
	{
		return MxlArticulationsContentType.Accent;
	}
	
	
	@NeverNull public static MxlAccent read(Element e)
	{
		MxlEmptyPlacement emptyPlacement = MxlEmptyPlacement.read(e);
		if (emptyPlacement != MxlEmptyPlacement.empty)
			return new MxlAccent(emptyPlacement);
		else
			return defaultInstance;
	}

	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		emptyPlacement.write(e);
	}
	

}
