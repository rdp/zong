package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.xml.XMLReader.elements;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.throwNull;
import static com.xenoage.zong.musicxml.util.Parse.parseAttrIntNull;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.choice.MxlCreditContent;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML credit.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="link,bookmark", children="credit-words")
public final class MxlCredit
{
	
	public static final String ELEM_NAME = "credit";
	
	@NeverNull private final MxlCreditContent content;
	private final int page;
	
	private static final int defaultPage = 1;

	
	public MxlCredit(MxlCreditContent content, int page)
	{
		this.content = content;
		this.page = page;
	}

	
	@NeverNull public MxlCreditContent getContent()
	{
		return content;
	}

	
	public int getPage()
	{
		return page;
	}
	
	
	@NeverNull public static MxlCredit read(Element e)
	{
		MxlCreditContent content = null;
		for (Element c : elements(e))
		{
			String n = c.getNodeName();
			if (n.equals("credit-image"))
			{
				content = MxlImage.read(c);
				break;
			}
			else if (n.equals("credit-words"))
			{
				content = MxlCreditWords.read(e);
				break;
			}
		}
		return new MxlCredit(
			throwNull(content, e),
			notNull(parseAttrIntNull(e, "page"), defaultPage));
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		content.write(e);
		addAttribute(e, "page", page);
	}
	

}
