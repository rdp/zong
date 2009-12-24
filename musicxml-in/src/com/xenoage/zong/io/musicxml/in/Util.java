package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.util.NullTools.notNull;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.JAXBElement;

import com.xenoage.util.Parser;
import com.xenoage.util.enums.VSide;
import com.xenoage.zong.data.music.barline.BarlineStyle;
import com.xenoage.zong.data.music.directions.DynamicsType;
import com.xenoage.zong.data.music.format.BezierPoint;
import com.xenoage.zong.data.music.format.Position;
import com.xenoage.zong.data.music.format.SP;
import com.xenoage.zong.data.text.Alignment;
import com.xenoage.zong.musiclayout.Constants;

import proxymusic.AboveBelow;
import proxymusic.BarStyle;
import proxymusic.BarStyleColor;
import proxymusic.LeftCenterRight;


/**
 * Some useful methods for reading MusicXML 2.0.
 * 
 * @author Andreas Wenger
 */
class Util
{
	
	private static final Hashtable<String, DynamicsType> dynamicsMap =
		new Hashtable<String, DynamicsType>();
	
	static
	{
		dynamicsMap.put("p", DynamicsType.p);
		dynamicsMap.put("pp", DynamicsType.pp);
		dynamicsMap.put("ppp", DynamicsType.ppp);
		dynamicsMap.put("pppp", DynamicsType.pppp);
		dynamicsMap.put("ppppp", DynamicsType.ppppp);
		dynamicsMap.put("pppppp", DynamicsType.pppppp);
		dynamicsMap.put("f", DynamicsType.f);
		dynamicsMap.put("ff", DynamicsType.ff);
		dynamicsMap.put("fff", DynamicsType.fff);
		dynamicsMap.put("ffff", DynamicsType.ffff);
		dynamicsMap.put("fffff", DynamicsType.fffff);
		dynamicsMap.put("ffffff", DynamicsType.ffffff);
		dynamicsMap.put("mp", DynamicsType.mp);
		dynamicsMap.put("mf", DynamicsType.mf);
		dynamicsMap.put("sf", DynamicsType.sf);
		dynamicsMap.put("sfp", DynamicsType.sfp);
		dynamicsMap.put("sfpp", DynamicsType.sfpp);
		dynamicsMap.put("fp", DynamicsType.fp);
		dynamicsMap.put("rf", DynamicsType.rf);
		dynamicsMap.put("rfz", DynamicsType.rfz);
		dynamicsMap.put("sfz", DynamicsType.sfz);
		dynamicsMap.put("sffz", DynamicsType.sffz);
		dynamicsMap.put("fz", DynamicsType.fz);
	}
	
	
	public static Alignment readAlignment(LeftCenterRight mxlValue)
	{
		if (mxlValue != null)
		{
			switch (mxlValue)
			{
				case LEFT: return Alignment.Left;
				case CENTER: return Alignment.Center;
				case RIGHT: return Alignment.Right;
			}
		}
		return null;
	}
	
	
	/**
	 * Reads the color from the given element.
	 * If unknown, null is returned.
	 */
	public static Color getColor(String mxlColor)
	{
		if (mxlColor == null)
			return null;
		if (mxlColor.length() == 7)
		{
			//format: #rrggbb
			if (mxlColor.charAt(0) != '#')
				return null;
			Integer r = Parser.parseInteger16Null(mxlColor.substring(1, 3));
			Integer g = Parser.parseInteger16Null(mxlColor.substring(3, 5));
			Integer b = Parser.parseInteger16Null(mxlColor.substring(5, 7));
			if (r == null || g == null || b == null)
				return null;
			return new Color(r, g, b, 255);
		}
		else if (mxlColor.length() == 9)
		{
			//format: ##aarrggbb
			if (mxlColor.charAt(0) != '#')
				return null;
			Integer a = Parser.parseInteger16Null(mxlColor.substring(1, 3));
			Integer r = Parser.parseInteger16Null(mxlColor.substring(3, 5));
			Integer g = Parser.parseInteger16Null(mxlColor.substring(5, 7));
			Integer b = Parser.parseInteger16Null(mxlColor.substring(7, 9));
			if (mxlColor == null || r == null || g == null || b == null)
				return null;
			return new Color(r, g, b, a);
		}
		else
			return null;
	}
	
	
	/**
	 * Reads the {@link DynamicsType} from the given {@link proxymusic.Dynamics} element.
	 * If there is more than one element, just the first is read at this moment.
	 * If unknown, null is returned.
	 */
	public static DynamicsType getDynamicsType(proxymusic.Dynamics mxlDynamics)
	{
		List<JAXBElement<?>> elements = mxlDynamics.getPOrPpOrPpp();
		if (elements.size() > 0)
		{
			return dynamicsMap.get(elements.get(0).getName().getLocalPart());
		}
		return null;
	}
	
	
	/**
	 * Reads the {@link BezierPoint} from the given {@link proxymusic.Slur} element.
	 * If at bezier-x is given, all values are read (using 0 for default),
	 * otherwise null is returned.
	 */
	public static BezierPoint getBezierPoint(proxymusic.Slur mxlSlur, float tenthsMm,
		int staffLinesCount, float noteLP)
	{
		return getBezierPoint(mxlSlur.getDefaultX(), mxlSlur.getDefaultY(),
			mxlSlur.getBezierX(), mxlSlur.getBezierY(), tenthsMm, staffLinesCount, noteLP);
	}
	
	
	/**
	 * Reads the {@link BezierPoint} from the given {@link proxymusic.Tied} element.
	 * If at bezier-x is given, all values are read (using 0 for default),
	 * otherwise null is returned.
	 */
	public static BezierPoint getBezierPoint(proxymusic.Tied mxlTied, float tenthsMm,
		int staffLinesCount, float noteLP)
	{
		return getBezierPoint(mxlTied.getDefaultX(), mxlTied.getDefaultY(),
			mxlTied.getBezierX(), mxlTied.getBezierY(), tenthsMm, staffLinesCount, noteLP);
	}
	
	
	private static BezierPoint getBezierPoint(BigDecimal px, BigDecimal py, BigDecimal cx, BigDecimal cy,
		float tenthsMm, int staffLinesCount, float noteLP)
	{
		SP point = null;
		SP control = null;
		float halfNoteWidth = Constants.WIDTH_QUARTER / 2;
		if (px != null && py != null)
		{
			float fpx = notNull(px, 0).floatValue();
			float fpy = notNull(py, 0).floatValue();
			//default-x is relative to left side of note. thus, substract the half width
			//of a note (TODO: note type. e.g., whole note is wider)
			point = new SP((fpx / 10 - halfNoteWidth) * tenthsMm,
				(staffLinesCount - 1) * 2 + fpy / 10 * 2 - noteLP);
		}
		if (cx != null && cy != null)
		{
			float fcx = notNull(cx, 0).floatValue();
			float fcy = notNull(cy, 0).floatValue();
			control =	new SP((fcx / 10 - halfNoteWidth) * tenthsMm, fcy / 10 * 2);
		}
		if (point != null || control != null)
			return new BezierPoint(point, control);
		else
			return null;
	}
	
	
	/**
	 * Reads the position of a {@link proxymusic.FormattedText} element. If at least one
	 * attribute is known, an instance of the {@link Position} class is returned,
	 * otherwise null.
	 */
	public static Position readPosition(proxymusic.FormattedText text, float tenthsMm, int staffLinesCount)
	{
		BigDecimal x = text.getDefaultX();
		BigDecimal y = text.getDefaultY();
		BigDecimal rx = text.getRelativeX();
		BigDecimal ry = text.getRelativeY();
		return readPosition(x, y, rx, ry, tenthsMm, staffLinesCount);
	}
	
	
	/**
	 * Reads the position of a {@link proxymusic.Pedal} element. If at least one
	 * attribute is known, an instance of the {@link Position} class is returned,
	 * otherwise null.
	 */
	public static Position readPosition(proxymusic.Pedal pedal, float tenthsMm, int staffLinesCount)
	{
		BigDecimal x = pedal.getDefaultX();
		BigDecimal y = pedal.getDefaultY();
		BigDecimal rx = pedal.getRelativeX();
		BigDecimal ry = pedal.getRelativeY();
		return readPosition(x, y, rx, ry, tenthsMm, staffLinesCount);
	}
	
	
	/**
	 * Reads the position of a {@link proxymusic.Wedge} element. If at least one
	 * attribute is known, an instance of the {@link Position} class is returned,
	 * otherwise null.
	 */
	public static Position readPosition(proxymusic.Wedge wedge, float tenthsMm, int staffLinesCount)
	{
		BigDecimal x = wedge.getDefaultX();
		BigDecimal y = wedge.getDefaultY();
		BigDecimal rx = wedge.getRelativeX();
		BigDecimal ry = wedge.getRelativeY();
		return readPosition(x, y, rx, ry, tenthsMm, staffLinesCount);
	}
	
	
	private static Position readPosition(BigDecimal x, BigDecimal y, BigDecimal rx,	BigDecimal ry,
		float tenthsMm, int staffLinesCount)
	{
		if (x == null && y == null && rx == null && ry == null)
		{
			return null;
		}
		else
		{
			Float fx = null;
			if (x != null)
			{
				fx = x.floatValue() / 10 * tenthsMm;
			}
			Float fy = null;
			if (y != null)
			{
				fy = (staffLinesCount - 1) * 2 + y.floatValue() / 10 * 2;
			}
			Float frx = null;
			if (rx != null)
			{
				frx = rx.floatValue() / 10 * tenthsMm;
			}
			Float fry = null;
			if (ry != null)
			{
				fry = ry.floatValue() / 10 * 2;
			}
			return new Position(fx, fy, frx, fry);
		}
	}
	
	
	public static VSide getVSide(AboveBelow placement)
	{
		if (placement == null)
			return null;
		else if (placement == AboveBelow.ABOVE)
			return VSide.Top;
		else
			return VSide.Bottom;
	}
	
	
	/**
	 * Gets the {@link BarlineStyle} that fits to the given MusicXML barline style.
	 * Returns null if none is found.
	 */
	public static BarlineStyle getBarlineStyle(BarStyleColor style)
	{
		if (style == null)
			return null;
		BarStyle barStyle = style.getValue();
		if (barStyle == null)
			return null;
		else
		{
			switch (barStyle)
			{
				//TODO: missing: TICK, SHORT
				case REGULAR: return BarlineStyle.Regular;
				case DOTTED: return BarlineStyle.Dotted;
				case DASHED: return BarlineStyle.Dashed;
				case HEAVY: return BarlineStyle.Heavy;
				case LIGHT_LIGHT: return BarlineStyle.LightLight;
				case LIGHT_HEAVY: return BarlineStyle.LightHeavy;
				case HEAVY_LIGHT: return BarlineStyle.HeavyLight;
				case HEAVY_HEAVY: return BarlineStyle.Heavy;
				case NONE: return BarlineStyle.None;
				default: return null;
			}
		}
	}
	

}
