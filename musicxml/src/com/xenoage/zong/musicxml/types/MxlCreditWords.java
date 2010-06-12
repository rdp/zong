package com.xenoage.zong.musicxml.types;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.xml.XMLReader.elements;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;

import org.w3c.dom.Element;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.annotations.NeverEmpty;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.choice.MxlCreditContent;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML credit-words content for the credit element.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="link,bookmark", partly="credit-words")
public final class MxlCreditWords
	implements MxlCreditContent
{
	
	@NeverEmpty private final PVector<MxlFormattedText> items;

	
	public MxlCreditWords(PVector<MxlFormattedText> items)
	{
		this.items = items;
	}

	
	@NeverEmpty public PVector<MxlFormattedText> getItems()
	{
		return items;
	}
	
	
	@Override public MxlCreditContentType getCreditContentType()
	{
		return MxlCreditContentType.CreditWords;
	}
	
	
	@NeverNull public static MxlCreditWords read(Element parent)
	{
		PVector<MxlFormattedText> items = pvec();
		for (Element e : elements(parent))
		{
			String n = e.getNodeName();
			if (n.equals("credit-words"))
				items = items.plus(MxlFormattedText.read(e));
		}
		if (items.size() < 1)
			throw invalid(parent);
		return new MxlCreditWords(items);
	}
	
	
	public void write(Element parent)
	{
		for (MxlFormattedText item : items)
		{
			Element e = addElement("credit-words", parent);
			item.write(e);
		}
	}


}
