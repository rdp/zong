package com.xenoage.zong.io.musicxml.in.readers;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.zong.core.music.format.SP.sp;

import com.xenoage.util.annotations.MaybeNull;
import com.xenoage.util.enums.VSide;
import com.xenoage.zong.core.music.format.BezierPoint;
import com.xenoage.zong.core.music.format.Position;
import com.xenoage.zong.core.music.format.SP;
import com.xenoage.zong.core.text.Alignment;
import com.xenoage.zong.musiclayout.Constants;
import com.xenoage.zong.musicxml.types.MxlBezier;
import com.xenoage.zong.musicxml.types.attributes.MxlPosition;
import com.xenoage.zong.musicxml.types.enums.MxlLeftCenterRight;
import com.xenoage.zong.musicxml.types.enums.MxlPlacement;


/**
 * Reads some other MusicXML elements.
 * 
 * @author Andreas Wenger
 */
public final class OtherReader
{
	
	
	@MaybeNull public static Alignment readAlignment(MxlLeftCenterRight mxlLeftCenterRight)
	{
		if (mxlLeftCenterRight != null)
		{
			switch (mxlLeftCenterRight)
			{
				case Left: return Alignment.Left;
				case Center: return Alignment.Center;
				case Right: return Alignment.Right;
			}
		}
		return null;
	}
	
	
	@MaybeNull public static BezierPoint readBezierPoint(MxlPosition mxlPosition,
		MxlBezier mxlBezier, float tenthsMm, int staffLinesCount, float noteLP)
	{
		Float px = mxlPosition.getDefaultX();
		Float py = mxlPosition.getDefaultY();
		Float cx = mxlBezier.getBezierX();
		Float cy = mxlBezier.getBezierY();
		SP point = null;
		SP control = null;
		float halfNoteWidth = Constants.WIDTH_QUARTER / 2;
		if (px != null && py != null)
		{
			float fpx = notNull(px, 0).floatValue();
			float fpy = notNull(py, 0).floatValue();
			//default-x is relative to left side of note. thus, substract the half width
			//of a note (TODO: note type. e.g., whole note is wider)
			point = sp((fpx / 10 - halfNoteWidth) * tenthsMm,
				(staffLinesCount - 1) * 2 + fpy / 10 * 2 - noteLP);
		}
		if (cx != null && cy != null)
		{
			float fcx = notNull(cx, 0).floatValue();
			float fcy = notNull(cy, 0).floatValue();
			control =	sp((fcx / 10 - halfNoteWidth) * tenthsMm, fcy / 10 * 2);
		}
		if (point != null || control != null)
			return new BezierPoint(point, control);
		else
			return null;
	}
	
	
	@MaybeNull public static Position readPosition(MxlPosition mxlPosition,
		float tenthsMm, int staffLinesCount)
	{
		Float x = mxlPosition.getDefaultX();
		Float y = mxlPosition.getDefaultY();
		Float rx = mxlPosition.getRelativeX();
		Float ry = mxlPosition.getDefaultY();
		if (x == null && y == null && rx == null && ry == null)
		{
			return null;
		}
		else
		{
			Float fx = null;
			if (x != null)
			{
				fx = x / 10 * tenthsMm;
			}
			Float fy = null;
			if (y != null)
			{
				fy = (staffLinesCount - 1) * 2 + y / 10 * 2;
			}
			Float frx = null;
			if (rx != null)
			{
				frx = rx / 10 * tenthsMm;
			}
			Float fry = null;
			if (ry != null)
			{
				fry = ry / 10 * 2;
			}
			return new Position(fx, fy, frx, fry);
		}
	}
	
	
	@MaybeNull public static VSide readVSide(MxlPlacement mxlPlacement)
	{
		if (mxlPlacement == null)
			return null;
		else if (mxlPlacement == MxlPlacement.Above)
			return VSide.Top;
		else
			return VSide.Bottom;
	}

}
