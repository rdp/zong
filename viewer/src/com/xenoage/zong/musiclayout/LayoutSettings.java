package com.xenoage.zong.musiclayout;

import static com.xenoage.util.math.Fraction.fr;

import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xenoage.util.NullTools;
import com.xenoage.util.io.IO;
import com.xenoage.util.logging.Log;
import com.xenoage.util.math.Fraction;
import com.xenoage.util.xml.XMLReader;


/**
 * Settings for the musical layout.
 * 
 * One instance of this class is used throughout the
 * whole application, and is loaded on demand from
 * "data/layout/default.xml".
 * 
 * All values are in interline spaces, unless otherwise stated.
 * 
 * Some of the default values are adapted from
 * "Ross: The Art of Music Engraving", page 77.
 * 
 * @author Andreas Wenger
 */
public final class LayoutSettings
{
	
	private static String file = "data/layout/default.xml";
	private static LayoutSettings instance = null;
	
	//duration-to-width mapping
	private HashMap<Fraction, Float> durationWidths = new HashMap<Fraction, Float>();
	//cache
	private Fraction durationWidthsLowestDuration = null, durationWidthsHighestDuration = null;
	private HashMap<Fraction, Float> durationWidthsCache = new HashMap<Fraction, Float>();
	
	//widths
	public float widthSharp;
	public float widthFlat;
	public float widthClef;
	public float widthMeasureEmpty;
	
	//scaling
	public float scalingClefInner; //clef in the middle of a staff
	
	//offsets
	public float offsetMeasureStart; //offset of the first element in a measure
	public float offsetBeatsMinimal; //minimal offset between to different beats
	
	
	public static LayoutSettings layoutSettings()
	{
		if (instance == null)
			instance = new LayoutSettings(file);
		return instance;
	}
	
	
	public static LayoutSettings createFromFile(String file)
	{
		return new LayoutSettings(file);
	}
	
	
	private LayoutSettings(String file)
	{
		try
		{
			Document doc = XMLReader.readFile(IO.openInputStream(file));
			//load the duration-to-width mapping
			Element eWidth = XMLReader.element(XMLReader.root(doc), "width");
			Element eNotes = XMLReader.element(eWidth, "notes");
			List<Element> listNotes = XMLReader.elements(eNotes, "note");
			for (Element el : listNotes)
			{
				//duration format: x/y, e.g. "1/4"
				Fraction duration = Fraction.fromString(XMLReader.attributeNotNull(el, "duration"));
				//width format: x+y/z, eg. "3+1/2"
				float width = Fraction.fromString(XMLReader.attributeNotNull(el, "width")).toFloat();
				durationWidths.put(duration, width);
				//find lowest and highest duration
				if (durationWidthsLowestDuration == null ||
					durationWidthsLowestDuration.compareTo(duration) > 0)
					durationWidthsLowestDuration = duration;
				if (durationWidthsHighestDuration == null ||
					durationWidthsHighestDuration.compareTo(duration) < 0)
					durationWidthsHighestDuration = duration;
			}
			//load widths
			Element e = XMLReader.element(eWidth, "clef");
			widthClef = Float.parseFloat(XMLReader.attributeNotNull(e, "width"));
			e = XMLReader.element(eWidth, "sharp");
			widthSharp = Float.parseFloat(XMLReader.attributeNotNull(e, "width"));
			e = XMLReader.element(eWidth, "flat");
			widthFlat = Float.parseFloat(XMLReader.attributeNotNull(e, "width"));
			e = XMLReader.element(eWidth, "measure");
			widthMeasureEmpty = Float.parseFloat(XMLReader.attributeNotNull(e, "empty"));
			//load scalings
			Element eScaling = XMLReader.element(XMLReader.root(doc), "scaling");
			e = XMLReader.element(eScaling, "clef");
			scalingClefInner = Float.parseFloat(XMLReader.attributeNotNull(e, "inner"));
			//offsets
			Element eOffset = XMLReader.element(XMLReader.root(doc), "offset");
			e = XMLReader.element(eOffset, "measure");
			offsetMeasureStart = Float.parseFloat(XMLReader.attributeNotNull(e, "start"));
			e = XMLReader.element(eOffset, "beats");
			offsetBeatsMinimal = Float.parseFloat(XMLReader.attributeNotNull(e, "minimal"));
		}
		catch (Exception ex)
		{
			Log.log(Log.ERROR, "Could not read the file \"" + file + "\":");
			Log.log(ex);
			//default values
			durationWidths.put(fr(1, 32), 1 + 1/2f);
			durationWidths.put(fr(1, 16), 1 + 3/4f);
			durationWidths.put(fr(1, 8), 2 + 1/2f);
			durationWidths.put(fr(1, 2), 4 + 3/4f);
			widthClef = 4;
			widthSharp = 1.2f;
			widthFlat = 1f;
			widthMeasureEmpty = 8f;
			scalingClefInner = 0.75f;
			offsetMeasureStart = 1;
			offsetBeatsMinimal = 1.5f;
		}
	}
	
	
	/**
	 * Computes and returns the layout that fits to the givenduration.
	 */
	public float getWidth(Fraction duration)
	{
		NullTools.throwNullArg(duration);
		//if available, use defined width
		Float width = durationWidths.get(duration);
		if (width != null)
			return width;
		//if available, use cached computed width
		width = durationWidthsCache.get(duration);
		if (width != null)
			return width;
		//not found. find the greatest lesser duration and the lowest
		//greater duration and interpolate linearly. remember the result
		//to avoid this computation for the future.
		Fraction lowerDur = durationWidthsLowestDuration;
		Fraction higherDur = durationWidthsHighestDuration;
		for (Fraction d : durationWidths.keySet())
		{
			if (d.compareTo(duration) <= 0 && d.compareTo(lowerDur) > 0)
			{
				lowerDur = d;
			}
			if (d.compareTo(duration) >= 0 && d.compareTo(higherDur) < 0)
			{
				higherDur = d;
			}
		}
		float lowerWidth = durationWidths.get(lowerDur);
		float higherWidth = durationWidths.get(higherDur);
		float durationWidth = (lowerWidth + higherWidth) * duration.toFloat() /
			(lowerDur.toFloat() + higherDur.toFloat());
		durationWidthsCache.put(duration, durationWidth);
		return durationWidth;
	}
	

}
