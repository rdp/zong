package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.zong.musicxml.util.Parse.parseAttrFloatNull;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML x-position and y-position.
 * 
 * @author Andreas Wenger
 */
public final class MxlBezier
{

	@MaybeNull private final Float bezierOffset, bezierOffset2, bezierX, bezierY, bezierX2, bezierY2;
	
	public static final MxlBezier empty = new MxlBezier(null, null, null, null, null, null);
	
	
	public MxlBezier(Float bezierOffset, Float bezierOffset2, Float bezierX, Float bezierY,
		Float bezierX2, Float bezierY2)
	{
		this.bezierOffset = bezierOffset;
		this.bezierOffset2 = bezierOffset2;
		this.bezierX = bezierX;
		this.bezierY = bezierY;
		this.bezierX2 = bezierX2;
		this.bezierY2 = bezierY2;
	}

	
	@MaybeNull public Float getBezierOffset()
	{
		return bezierOffset;
	}

	
	@MaybeNull public Float getBezierOffset2()
	{
		return bezierOffset2;
	}

	
	@MaybeNull public Float getBezierX()
	{
		return bezierX;
	}

	
	@MaybeNull public Float getBezierY()
	{
		return bezierY;
	}

	
	@MaybeNull public Float getBezierX2()
	{
		return bezierX2;
	}

	
	@MaybeNull public Float getBezierY2()
	{
		return bezierY2;
	}


	@NeverNull public static MxlBezier read(Element e)
	{
		Float bezierOffset = parseAttrFloatNull(e, "bezier-offset");
		Float bezierOffset2 = parseAttrFloatNull(e, "bezier-offset-2");
		Float bezierX = parseAttrFloatNull(e, "bezier-x");
		Float bezierY = parseAttrFloatNull(e, "bezier-y");
		Float bezierX2 = parseAttrFloatNull(e, "bezier-x2");
		Float bezierY2 = parseAttrFloatNull(e, "bezier-y2");
		if (bezierOffset != null || bezierOffset2 != null || bezierX != null ||
			bezierY != null || bezierX2 != null || bezierY2 != null)
			return new MxlBezier(bezierOffset, bezierOffset2, bezierX, bezierY, bezierX2, bezierY2);
		else
			return empty;
	}
	
	
	public void write(Element e)
	{
		addAttribute(e, "bezier-offset", bezierOffset);
		addAttribute(e, "bezier-offset-2", bezierOffset2);
		addAttribute(e, "bezier-x", bezierX);
		addAttribute(e, "bezier-y", bezierY);
		addAttribute(e, "bezier-x2", bezierX2);
		addAttribute(e, "bezier-y2", bezierY2);
	}
	

}
