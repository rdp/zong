package com.xenoage.zong.musicxml.types.groups;

import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.Parse.parseChildInt;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML all-margins.
 * 
 * @author Andreas Wenger
 */
public final class MxlAllMargins
{
	
	private final float leftMargin;
	private final float rightMargin;
	private final float topMargin;
	private final float bottomMargin;
	
	
	public MxlAllMargins(float leftMargin, float rightMargin, float topMargin,
		float bottomMargin)
	{
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;
	}

	
	public float getLeftMargin()
	{
		return leftMargin;
	}

	
	public float getRightMargin()
	{
		return rightMargin;
	}

	
	public float getTopMargin()
	{
		return topMargin;
	}

	
	public float getBottomMargin()
	{
		return bottomMargin;
	}
	
	
	@NeverNull public static MxlAllMargins read(Element e)
	{
		return new MxlAllMargins(
			parseChildInt(e, "left-margin"),
			parseChildInt(e, "right-margin"),
			parseChildInt(e, "top-margin"),
			parseChildInt(e, "bottom-margin"));
	}
	
	
	public void write(Element e)
	{
		addElement("left-margin", leftMargin, e);
		addElement("right-margin", rightMargin, e);
		addElement("top-margin", topMargin, e);
		addElement("bottom-margin", bottomMargin, e);
	}


}
