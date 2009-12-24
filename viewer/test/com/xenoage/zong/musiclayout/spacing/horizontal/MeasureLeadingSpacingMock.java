package com.xenoage.zong.musiclayout.spacing.horizontal;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.format.ScoreFormat;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.musiclayout.notations.ClefNotation;


/**
 * Mock class that can be used when a
 * {@link MeasureLeadingSpacing} is needed
 * but when its content is unimportant.
 * 
 * @author Andreas Wenger
 */
public class MeasureLeadingSpacingMock
{
	
	
	/**
	 * Creates an easy MeasureLeadingSpacing (with a g-clef)
	 * that has the given width in mm.
	 */
	public static MeasureLeadingSpacing createGClefSpacing(float widthMm)
	{
		float widthIS = widthMm / new ScoreFormat().getInterlineSpace();
		ClefNotation notation = new ClefNotation(new Clef(ClefType.G),
			new ElementWidth(widthIS), 0, 1);
		SpacingElement spacing = new SpacingElement(notation.getMusicElement(), new Fraction(0), 0);
		return new MeasureLeadingSpacing(new SpacingElement[]{spacing}, widthIS);
	}
	

}
