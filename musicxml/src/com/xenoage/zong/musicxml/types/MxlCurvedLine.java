package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.xml.XMLReader.attribute;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.InvalidMusicXML.invalid;
import static com.xenoage.zong.musicxml.util.Parse.parseAttrIntNull;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.attributes.MxlPosition;
import com.xenoage.zong.musicxml.types.choice.MxlNotationsContent;
import com.xenoage.zong.musicxml.types.enums.MxlPlacement;
import com.xenoage.zong.musicxml.types.enums.MxlStartStopContinue;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML slur or tie.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="line-type,orientation,color")
public final class MxlCurvedLine
	implements MxlNotationsContent
{
	
	public static final String ELEM_NAME_SLUR = "slur";
	public static final String ELEM_NAME_TIED = "tied";
	
	
	public enum MxlElementType
	{
		Slur,
		Tied;
	}
	
	@NeverNull private final MxlElementType elementType;
	@NeverNull private final MxlStartStopContinue type;
	@MaybeNull private final Integer number;
	@NeverNull private final MxlPosition position;
	@MaybeNull private final MxlPlacement placement;
	@NeverNull private final MxlBezier bezier;
	
	private static final int defaultNumberForSlur = 1;
	
	
	public MxlCurvedLine(MxlElementType elementType, MxlStartStopContinue type, Integer number,
		MxlPosition position, MxlPlacement placement, MxlBezier bezier)
	{
		this.elementType = elementType;
		this.type = type;
		this.number = number;
		this.position = position;
		this.placement = placement;
		this.bezier = bezier;
	}

	
	@NeverNull public MxlElementType getElementType()
	{
		return elementType;
	}

	
	@NeverNull public MxlStartStopContinue getType()
	{
		return type;
	}

	
	/**
	 * May be null for tied elements, but never null for slurs.
	 */
	@MaybeNull public Integer getNumber()
	{
		return number;
	}

	
	@NeverNull public MxlPosition getPosition()
	{
		return position;
	}

	
	@MaybeNull public MxlPlacement getPlacement()
	{
		return placement;
	}

	
	@NeverNull public MxlBezier getBezier()
	{
		return bezier;
	}
	
	
	@Override public MxlNotationsContentType getNotationsContentType()
	{
		return MxlNotationsContentType.CurvedLine;
	}
	
	
	@NeverNull public static MxlCurvedLine read(Element e)
	{
		//element type
		MxlElementType elementType = null;
		String eName = e.getNodeName();
		if (ELEM_NAME_SLUR.equals(eName))
			elementType = MxlElementType.Slur;
		else if (ELEM_NAME_TIED.equals(eName))
			elementType = MxlElementType.Tied;
		else
			throw invalid(e);
		//type
		MxlStartStopContinue type = MxlStartStopContinue.read(attribute(e, "type"), e);
		if (type == MxlStartStopContinue.Continue && elementType == MxlElementType.Tied)
			throw invalid(e);
		//other members
		Integer number = parseAttrIntNull(e, "number");
		if (elementType == MxlElementType.Slur)
			number = notNull(number, defaultNumberForSlur);
		MxlPosition position = MxlPosition.read(e);
		MxlPlacement placement = MxlPlacement.read(e);
		MxlBezier bezier = MxlBezier.read(e);
		return new MxlCurvedLine(elementType, type, number, position, placement, bezier);
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(elementType == MxlElementType.Slur ?
			ELEM_NAME_SLUR : ELEM_NAME_TIED, parent);
		addAttribute(e, "type", type.write());
		addAttribute(e, "number", number);
		position.write(e);
		if (placement != null)
			placement.write(e);
		bezier.write(e);
	}
	
	

}
