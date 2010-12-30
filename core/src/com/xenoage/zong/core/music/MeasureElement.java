package com.xenoage.zong.core.music;


/**
 * Measure elements are {@link MusicElement}s, which can
 * appear in the middle of a measure and apply to
 * all voices of the measure.
 * 
 * This is the case for clefs and directions (but directions
 * may also be attached to other elements and apply only to
 * them then). Also keys can be measure elements.
 * 
 * @author Andreas Wenger
 */
public interface MeasureElement
	extends MusicElement
{

}
