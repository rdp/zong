package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.Parse.parseChildFloat;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;


/**
 * MusicXML scaling.
 * 
 * @author Andreas Wenger
 */
public final class MxlScaling
{
	
	public static final String ELEM_NAME = "scaling";
	
	private final float millimeters;
	private final float tenths;
	
	
	public MxlScaling(float millimeters, float tenths)
	{
		this.millimeters = millimeters;
		this.tenths = tenths;
	}

	
	public float getMillimeters()
	{
		return millimeters;
	}

	
	public float getTenths()
	{
		return tenths;
	}
	
	
	@NeverNull public static MxlScaling read(Element e)
	{
		return new MxlScaling(
			parseChildFloat(e, "millimeters"),
			parseChildFloat(e, "tenths"));
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		addElement("millimeters", millimeters, e);
		addElement("tenths", tenths, e);
	}
	

}
