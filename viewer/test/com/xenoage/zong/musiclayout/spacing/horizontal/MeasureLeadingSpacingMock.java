package com.xenoage.zong.musiclayout.spacing.horizontal;

import static com.xenoage.util.math.Fraction.fr;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.format.ScoreFormat;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.clef.ClefType;
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
		float widthIS = widthMm / ScoreFormat.getDefault().getInterlineSpace();
		ClefNotation notation = new ClefNotation(new Clef(ClefType.G),
			new ElementWidth(widthIS), 0, 1);
		SpacingElement spacing = new SpacingElement(notation.getMusicElement(), fr(0), 0);
		return new MeasureLeadingSpacing(new SpacingElement[]{spacing}, widthIS);
	}
	

}
