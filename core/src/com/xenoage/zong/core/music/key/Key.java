package com.xenoage.zong.core.music.key;

import com.xenoage.zong.core.music.ColumnElement;
import com.xenoage.zong.core.music.MeasureElement;
import com.xenoage.zong.core.music.Pitch;


/**
 * Interface for key signatures.
 * 
 * Key signatures may appear for the whole measure column
 * (i.e. for all staves) or also only within single measures.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public interface Key
	extends MeasureElement, ColumnElement
{

	/**
	 * Returns the alterations from the notes from A (0) to G (6).
	 */
	public int[] getAlterations();


	/**
	 * Returns the nearest higher {@link Pitch} in the current key.
	 */
	public Pitch getNearestHigherPitch(Pitch pitch);


	/**
	 * Returns the nearest lower {@link Pitch} in the current key.
	 */
	public Pitch getNearestLowerPitch(Pitch pitch);

}
