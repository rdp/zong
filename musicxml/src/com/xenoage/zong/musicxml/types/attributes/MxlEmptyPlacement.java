package com.xenoage.zong.musicxml.types.attributes;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.enums.MxlPlacement;


/**
 * MusicXML empty-placement.
 * 
 * @author Andreas Wenger
 */
public final class MxlEmptyPlacement
{
	
	@NeverNull private final MxlPrintStyle printStyle;
	@MaybeNull private final MxlPlacement placement;
	
	public static final MxlEmptyPlacement empty = new MxlEmptyPlacement(MxlPrintStyle.empty, null);
	
	
	public MxlEmptyPlacement(MxlPrintStyle printStyle, MxlPlacement placement)
	{
		this.printStyle = printStyle;
		this.placement = placement;
	}

	
	@NeverNull public MxlPrintStyle getPrintStyle()
	{
		return printStyle;
	}

	
	@MaybeNull public MxlPlacement getPlacement()
	{
		return placement;
	}
	
	
	@NeverNull public static MxlEmptyPlacement read(Element e)
	{
		MxlPrintStyle printStyle = MxlPrintStyle.read(e);
		MxlPlacement placement = MxlPlacement.read(e);
		if (printStyle != MxlPrintStyle.empty || placement != null)
			return new MxlEmptyPlacement(printStyle, placement);
		else
			return empty;
	}
	
	
	public void write(Element e)
	{
		if (this != empty)
		{
			printStyle.write(e);
			if (placement != null)
				placement.write(e);
		}
	}

}
