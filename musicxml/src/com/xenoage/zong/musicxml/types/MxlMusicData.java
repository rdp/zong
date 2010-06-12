package com.xenoage.zong.musicxml.types;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;

import org.w3c.dom.Element;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.annotations.NeverEmpty;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.util.xml.XMLReader;
import com.xenoage.zong.musicxml.types.choice.MxlMusicDataContent;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML music-data.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="harmony,figured-bass,sound,grouping,link,bookmark",
	children="note,backup,forward,direction,attributes,print,barline")
public final class MxlMusicData
{
	
	@NeverEmpty private final PVector<MxlMusicDataContent> content;

	
	public MxlMusicData(PVector<MxlMusicDataContent> content)
	{
		this.content = content;
	}

	
	@NeverEmpty public PVector<MxlMusicDataContent> getContent()
	{
		return content;
	}
	
	
	@NeverNull public static MxlMusicData read(Element e)
	{
		PVector<MxlMusicDataContent> content = pvec();
		for (Element c : XMLReader.elements(e))
		{
			MxlMusicDataContent item = null;
			String n = c.getNodeName();
			switch (n.charAt(0))
			{
				case 'a':
					if (n.equals(MxlAttributes.ELEM_NAME))
						item = MxlAttributes.read(c);
					break;
				case 'b':
					if (n.equals(MxlBackup.ELEM_NAME))
						item = MxlBackup.read(c);
					else if (n.equals(MxlBarline.ELEM_NAME))
						item = MxlBarline.read(c);
					break;
				case 'd':
					if (n.equals(MxlDirection.ELEM_NAME))
						item = MxlDirection.read(c);
					break;
				case 'f':
					if (n.equals(MxlForward.ELEM_NAME))
						item = MxlForward.read(c);
					break;
				case 'n':
					if (n.equals(MxlNote.ELEM_NAME))
						item = MxlNote.read(c);
					break;
				case 'p':
					if (n.equals(MxlPrint.ELEM_NAME))
						item = MxlPrint.read(c);
					break;
			}
			if (item != null)
				content = content.plus(item);
		}
		if (content.size() < 1)
			throw invalid(e);
		return new MxlMusicData(content);
	}
	
	
	public void write(Element e)
	{
		for (MxlMusicDataContent item : content)
		{
			item.write(e);
		}
	}
	

}
