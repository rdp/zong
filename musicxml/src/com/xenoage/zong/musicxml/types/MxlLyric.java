package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLReader.element;
import static com.xenoage.util.xml.XMLWriter.addElement;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverEmpty;
import com.xenoage.zong.musicxml.types.choice.MxlLyricContent;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML lyric.
 * 
 * Consists of a {@link MxlLyricContent}.
 * Other information is ignored.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="end-line,end-paragraph,editorial,number,name,justify,position," +
	"placement,color", children="")
public final class MxlLyric
{
	
	public static final String ELEM_NAME = "lyric";
	
	@NeverEmpty private final MxlLyricContent content;

	
	public MxlLyric(MxlLyricContent content)
	{
		this.content = content;
	}

	
	public MxlLyricContent getContent()
	{
		return content;
	}
	
	
	/**
	 * Reads and returns the lyric content of the given element,
	 * or returns null if unsupported.
	 */
	@MaybeNull public static MxlLyric read(Element e)
	{
		MxlLyricContent content = null;
		Element firstChild = element(e);
		String n = firstChild.getNodeName();
		if (n.equals("syllabic") || n.equals("text"))
		{
			content = MxlSyllabicText.read(e);
		}
		else if (n.equals("extend"))
		{
			content = MxlExtend.read(e);
		}
		if (content != null)
		{
			return new MxlLyric(content);
		}
		else
		{
			return null;
		}
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		content.write(e);
	}
	

}
