package com.xenoage.zong.data.music.key;

import com.xenoage.zong.data.music.NoVoiceElement;
import com.xenoage.zong.data.music.Pitch;


/**
 * Base class for key signatures.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public abstract class Key
	extends NoVoiceElement
{

	/**
	 * Returns the alterations from the notes from A (0) to G (6).
	 */
	public abstract int[] getAlterations();


	/**
	 * Returns the nearest higher {@link Pitch} in the current key.
	 */
	public abstract Pitch getNearestHigherPitch(Pitch pitch);


	/**
	 * Returns the nearest lower {@link Pitch} in the current key.
	 */
	public abstract Pitch getNearestLowerPitch(Pitch pitch);

}
