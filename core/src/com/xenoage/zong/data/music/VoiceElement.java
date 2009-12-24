package com.xenoage.zong.data.music;

import com.xenoage.zong.data.music.util.DeepCopyCache;


/**
 * Interface for all classes that are child of a {@link Voice}.
 * These are chords and rests.
 * 
 * @author Uli Teschemacher
 */
public abstract class VoiceElement
	extends MusicElement
{
	
	/**
	 * Returns a deep copy of this element, using the given
	 * parent voice and {@link DeepCopyCache}.
	 */
	@Override public abstract VoiceElement deepCopy(Voice parentVoice, DeepCopyCache cache);
	
}
