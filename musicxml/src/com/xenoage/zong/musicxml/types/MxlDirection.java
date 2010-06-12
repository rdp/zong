package com.xenoage.zong.musicxml.types;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.Parse.parseChildIntNull;

import org.w3c.dom.Element;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverEmpty;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.util.xml.XMLReader;
import com.xenoage.zong.musicxml.types.choice.MxlMusicDataContent;
import com.xenoage.zong.musicxml.types.enums.MxlPlacement;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML direction.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="offset,editorial-voice-direction,directive",
	partly="direction-type", children="direction-type,sound")
public final class MxlDirection
	implements MxlMusicDataContent
{
	
	public static final String ELEM_NAME = "direction";
	
	@NeverNull private final PVector<MxlDirectionType> directionTypes;
	@MaybeNull private final Integer staff;
	@MaybeNull private final MxlSound sound;
	@MaybeNull private final MxlPlacement placement;
	
	
	public MxlDirection(PVector<MxlDirectionType> directionTypes, Integer staff, MxlSound sound,
		MxlPlacement placement)
	{
		this.directionTypes = directionTypes;
		this.staff = staff;
		this.sound = sound;
		this.placement = placement;
	}

	
	@NeverEmpty public PVector<MxlDirectionType> getDirectionTypes()
	{
		return directionTypes;
	}

	
	@MaybeNull public Integer getStaff()
	{
		return staff;
	}

	
	@MaybeNull public MxlSound getSound()
	{
		return sound;
	}

	
	@MaybeNull public MxlPlacement getPlacement()
	{
		return placement;
	}


	@Override public MxlMusicDataContentType getMusicDataContentType()
	{
		return MxlMusicDataContentType.Direction;
	}
	
	
	/**
	 * Returns null, when no supported content was found.
	 */
	@MaybeNull public static MxlDirection read(Element e)
	{
		PVector<MxlDirectionType> directionTypes = pvec();
		MxlSound sound = null;
		for (Element child : XMLReader.elements(e))
		{
			String n = child.getNodeName();
			if (n.equals(MxlDirectionType.ELEM_NAME))
			{
				MxlDirectionType directionType = MxlDirectionType.read(child);
				if (directionType != null)
					directionTypes = directionTypes.plus(directionType);
			}
			else if (n.equals(MxlSound.ELEM_NAME))
			{
				sound = MxlSound.read(child);
			}
		}
		if (directionTypes.size() > 0)
		{
			return new MxlDirection(directionTypes, parseChildIntNull(e, "staff"),
				sound, MxlPlacement.read(e));
		}
		else
		{
			return null;
		}
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		for (MxlDirectionType directionType : directionTypes)
			directionType.write(e);
		addAttribute(e, "staff", staff);
		if (sound != null)
			sound.write(e);
		if (placement != null)
			placement.write(e);
	}
	

}
