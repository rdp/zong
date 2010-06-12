package com.xenoage.zong.musicxml.types.groups;

import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.Parse.parseChildInt;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML left-right-margins.
 * 
 * @author Andreas Wenger
 */
public final class MxlLeftRightMargins
{
	
	private final float leftMargin;
	private final float rightMargin;
	
	
	public MxlLeftRightMargins(float leftMargin, float rightMargin)
	{
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
	}

	
	public float getLeftMargin()
	{
		return leftMargin;
	}

	
	public float getRightMargin()
	{
		return rightMargin;
	}


	@NeverNull public static MxlLeftRightMargins read(Element e)
	{
		return new MxlLeftRightMargins(
			parseChildInt(e, "left-margin"),
			parseChildInt(e, "right-margin"));
	}
	
	
	public void write(Element e)
	{
		addElement("left-margin", leftMargin, e);
		addElement("right-margin", rightMargin, e);
	}
	

}
